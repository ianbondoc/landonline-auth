package nz.govt.linz

import nz.govt.linz.jpa.FirmUser
import nz.govt.linz.jpa.LockedOutStatus
import nz.govt.linz.jpa.LoginType
import nz.govt.linz.jpa.User
import nz.govt.linz.service.UserService
import org.keycloak.common.util.MultivaluedHashMap
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.RoleModel
import org.keycloak.models.UserModel
import org.keycloak.storage.StorageId
import org.keycloak.storage.adapter.AbstractUserAdapter
import org.keycloak.util.JsonSerialization
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

class LinzUserAdapter(session: KeycloakSession, realm: RealmModel, model: ComponentModel, private val user: User) :
    AbstractUserAdapter.Streams(session, realm, model) {

    private val log by lazy { LoggerFactory.getLogger(LinzUserAdapter::class.java) }

    private val userService: UserService

    init {
        log.info("CREATING LinzUserAdapter: $session")
        storageId = StorageId(model.id, user.id)
        userService = session.getAttribute(UserService.ATTRIBUTE, UserService::class.java)
    }

    override fun getRequiredActionsStream(): Stream<String> {
        log.info("getRequiredActionsStream: $session")
        return if (user.lockedOutStatus == LockedOutStatus.MUST_CHANGE_PASSWORD_ON_NEXT_LOGIN) {
            return Stream.of(UserModel.RequiredAction.UPDATE_PASSWORD.name)
        } else Stream.empty()
    }

    override fun addRequiredAction(action: String?) {
        if (action == UserModel.RequiredAction.UPDATE_PASSWORD.name) {
            userService.setRequiredChangePassword(user.id)
        }
    }

    override fun addRequiredAction(action: UserModel.RequiredAction?) {
        addRequiredAction(requireNotNull(action).name)
    }

    override fun removeRequiredAction(action: String?) {
        // since stored proc already removes update password status in db we just ignore this call
        log.info("removeRequiredAction: $session, $action")
    }

    override fun removeRequiredAction(action: UserModel.RequiredAction?) {
        removeRequiredAction(requireNotNull(action).name)
    }

    private val roles by lazy {
        val userTypeRole = listOf(
            if (user.loginType == LoginType.INTERNAL) {
                "ROLE_INTERNAL_USER"
            } else {
                "ROLE_EXTERNAL_USER"
            }
        )
        (userTypeRole + user.profiles + user.categories).map { LinzRoleAdapter(it, realm) }.toSet()
    }

    override fun getUsername(): String = user.id

    override fun getFirstName(): String = user.givenNames

    override fun getLastName(): String = user.surname

    override fun getEmail(): String = user.emailAddress

    override fun getCreatedTimestamp(): Long {
        return user.creationDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: System.currentTimeMillis()
    }

    override fun getFirstAttribute(name: String?): String? =
        attributes.getOrDefault(checkNotNull(name), emptyList()).let { if (it.isEmpty()) null else it.first() }

    override fun getAttributes(): Map<String, List<String>> = MultivaluedHashMap<String, String>().apply {
        add(UserModel.USERNAME, username)
        add(UserModel.EMAIL, email)
        add(UserModel.FIRST_NAME, firstName)
        add(UserModel.LAST_NAME, lastName)
        add("title", user.title)
        add("preferredName", user.preferredName)
        add("loginType", user.loginType.dbValue)
        add("lastLogin", user.lastLogin?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        add("firms", user.firmAssociations.toJson())
        add("roles", JsonSerialization.writeValueAsString(user.categories))
        add("profiles", JsonSerialization.writeValueAsString(user.profiles))
    }

    // leaving this here if we want to make firms as groups - not really
    //override fun getGroupsInternal(): Set<GroupModel> = user.associatedFirms.map { LinzGroupAdapter(it) }.toSet()

    override fun getRoleMappingsInternal(): Set<RoleModel> = roles

    override fun appendDefaultRolesToRoleMappings(): Boolean = false

    override fun appendDefaultGroups(): Boolean = false
}

private fun List<FirmUser>.toJson(): String? {
    return JsonSerialization.writeValueAsString(map {
        mapOf(
            "id" to it.id.firmId,
            "name" to it.firm.corporateName,
            "privileges" to it.privileges
        )
    })
}
