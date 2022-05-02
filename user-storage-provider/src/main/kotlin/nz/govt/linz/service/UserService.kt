package nz.govt.linz.service

import com.google.common.io.BaseEncoding
import io.ebean.DB
import io.ebean.Database
import io.ebean.ExpressionList
import io.ebean.annotation.DbEnumValue
import io.ebean.annotation.Transactional
import io.ebeaninternal.api.ScopedTransaction
import nz.govt.linz.jpa.LockedOutStatus
import nz.govt.linz.jpa.LoginFlag
import nz.govt.linz.jpa.LoginType
import nz.govt.linz.jpa.User
import org.slf4j.LoggerFactory
import java.util.stream.Stream

class UserService(private val database: Database) {

    private val log by lazy { LoggerFactory.getLogger(UserService::class.java) }

    private val encryptionService = LegacyEncryptionService()

    fun authenticate(username: String, password: String): AuthStatus {
        log.info("Authenticating $username | $password")

        val encryptedPassword = encryptionService.encrypt(username, password)

        val query = """
            execute function cf_cse_check_login(
                p_user_id = :username,
                p_password = :encryptedPassword,
                p_connection_type = :connectionType
            );
        """.trimIndent()

        log.info("Authenticating $username | $encryptedPassword")

        val result = database.findDto(AuthResult::class.java, query)
            .setParameter("username", username)
            .setParameter("encryptedPassword", encryptedPassword)
            .setParameter("connectionType", "INIT")
            .findOne()

        checkNotNull(result)

        if (result.status == AuthStatus.LOGIN_AUTHORISED.dbValue) {
            // apply conditions that are not handled by the called procedure
            // Breached user ?
            if (result.loginFlag == LoginFlag.CANNOT_LOGIN.dbValue) {
                result.status = AuthStatus.NOT_PERMITTED_TO_LOGIN.dbValue
            } else {
                // not internal users need to be associated with an active firm.
                val user = getUserById(username)
                if (user.loginType != LoginType.INTERNAL && user.associatedFirms.isEmpty()) {
                    result.status = AuthStatus.NOT_ASSOCIATED_WITH_ANY_FIRM.dbValue
                }
            }
        }
        return AuthStatus.fromDbValue(result.status)
    }

    @Transactional
    fun changePassword(username: String, password: String) {
        (DB.currentTransaction() as ScopedTransaction).markNotQueryOnly()

        val hexPassword = BaseEncoding.base16().encode(password.toByteArray())
        val encryptedPassword = encryptionService.encrypt(username, password)

        try {

            setUserForAudit(username)

            val query = """
            execute procedure cp_cse_change_pwd (
                al_key = TODAY::INT,
                ast_userid = :username,
                ast_hexoldpasswd = NULL,
                ast_hexnewpasswd = cf_cse_enc_encrypt(TODAY, :hexPassword),
                ast_encpasswd = :encryptedPassword,
                ast_mode = 'ADMIN_MODIFY'
            );
            """.trimIndent()

            // Return value is the user's email address, which is not required
            database.sqlUpdate(query)
                .setParameter("username", username)
                .setParameter("hexPassword", hexPassword)
                .setParameter("encryptedPassword", encryptedPassword)
                .execute()
        } finally {
            setUserForAudit(null)
        }
    }

    /**
     * Set the username in the DB session, which is used for auditing purposes. The firm is set
     * as NULL in all cases, and this can be changed if and when we need to know the firm for
     * a proc (and don't want to change the proc to not require it) as that is only used in a
     * couple of places.
     */
    private fun setUserForAudit(username: String?) {
        database.sqlUpdate("execute procedure cp_ccl_setUserIdAndFirmId(avc_user_id = :user, avc_firm_id = null)")
            .setParameter("user", username)
            .execute()
    }

    fun getUserById(userId: String): User =
        checkNotNull(findUserById(userId)) { "Expected user with id $userId not found" }

    fun findUserById(userId: String): User? =
        database.find(User::class.java).where().isValidUser().idEq(userId).findOne()

    fun findUserByEmail(email: String): User? =
        database.find(User::class.java).where().isValidUser().eq("emailAddress", email).findOne()

    fun countUsers() = database.find(User::class.java).where().isValidUser().findCount()

    fun getUsers(firstResult: Int, maxResult: Int): Stream<User> =
        database.find(User::class.java).where()
            .isValidUser()
            .let { if (firstResult < 0) it else it.setFirstRow(firstResult) }
            .let { if (maxResult < 0) it else it.setMaxRows(maxResult) }
            .findList().stream()

    fun countUsersLikeUserId(search: String): Int =
        database.find(User::class.java).where()
            .isValidUser()
            .contains("id", search)
            .findCount()

    fun getUsersLikeUserId(search: String, firstResult: Int, maxResult: Int): Stream<User> =
        database.find(User::class.java).where()
            .isValidUser()
            .contains("id", search)
            .setFirstRow(firstResult)
            .setMaxRows(maxResult)
            .findList().stream()

    private fun <T> ExpressionList<T>.isValidUser() =
        eq("type", "PERS").isIn("loginType", LoginType.INTERNAL, LoginType.EXTERNAL)

    @Transactional
    fun setRequiredChangePassword(userId: String) {
        database.update(User::class.java).set("lockedOutStatus", LockedOutStatus.MUST_CHANGE_PASSWORD_ON_NEXT_LOGIN)
            .where().idEq(userId).update()
    }

    fun shutdown() {
        database.shutdown(true, false)
    }

    companion object {
        const val ATTRIBUTE = "session.userService"
    }
}

class AuthResult(
    var status: String,
    val loginsRemaining: Int,
    val pwdPerm: String, // unused
    // 'Y' or 'N' where N should translate to
    val loginFlag: String
)

enum class AuthStatus(@get:DbEnumValue val dbValue: String) {
    LOGIN_AUTHORISED("AUTH"),

    /*
     * login is still authorised, but if all grace logins have
     * been used, account's locked flag is set, and will
     * prevent subsequent logins until the password is reset
     */
    PASSWORD_PAST_EXPIRATION_DATE("PSWE"),

    NO_GRACE_LOGINS_LEFT("NOGR"),
    BAD_LOGIN_COUNT_EXCEEDED("FLCE"),
    USER_HAS_BEEN_DEACTIVATED("DACT"),
    USER_HAS_BEEN_LOCKED_OUT("LOCK"),
    INVALID_USERID_OR_PASSWORD("NONE"),

    // cannot login (not locked)
    TEMPORARY_PASSWORD_EXPIRED("TPEX"),
    INVALID_PARAMETER_VALUE("UERR"),

    // loginFlag = 'N' (not returned by stored proc)
    NOT_PERMITTED_TO_LOGIN("NOLG"),

    // not returned by stored proc
    NOT_ASSOCIATED_WITH_ANY_FIRM("NFRM");

    companion object {
        private val reverseLookup = values().associateBy { it.dbValue }
        fun fromDbValue(dbValue: String): AuthStatus =
            reverseLookup[dbValue] ?: throw EnumConstantNotPresentException(AuthStatus::class.java, dbValue)
    }
}

