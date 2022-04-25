package nz.govt.linz

import nz.govt.linz.service.AuthStatus
import nz.govt.linz.service.UserService
import org.keycloak.component.ComponentModel
import org.keycloak.credential.CredentialInput
import org.keycloak.credential.CredentialInputUpdater
import org.keycloak.credential.CredentialInputValidator
import org.keycloak.models.*
import org.keycloak.models.credential.PasswordCredentialModel
import org.keycloak.storage.StorageId
import org.keycloak.storage.UserStorageProvider
import org.keycloak.storage.user.UserLookupProvider
import org.keycloak.storage.user.UserQueryProvider
import org.slf4j.LoggerFactory
import java.util.stream.Stream

// TODO: Review all available search/query functions
class LinzUserStorageProvider(
    private val session: KeycloakSession,
    private val model: ComponentModel
) :
    UserStorageProvider, UserLookupProvider.Streams, UserQueryProvider.Streams, CredentialInputUpdater.Streams,
    CredentialInputValidator {

    private val log by lazy { LoggerFactory.getLogger(LinzUserStorageProvider::class.java) }

    private val userService: UserService

    init {
        log.info("CREATING LinzUserStorageProvider : $session : ${session::class.qualifiedName}")
        userService = session.getAttribute(UserService.ATTRIBUTE, UserService::class.java)
    }

    override fun supportsCredentialType(credentialType: String?): Boolean {
        log.info("supportsCredentialType: $session : $credentialType")
        return PasswordCredentialModel.TYPE == credentialType
    }

    override fun isConfiguredFor(realm: RealmModel?, user: UserModel?, credentialType: String?): Boolean {
        log.info("isConfiguredFor: $session : $realm, $user, $credentialType")
        return supportsCredentialType(credentialType)
    }

    override fun isValid(realm: RealmModel?, user: UserModel?, input: CredentialInput?): Boolean {
        log.info("isValid: $session : $realm : $user : $input")
        if (!(supportsCredentialType(input?.type) && input is UserCredentialModel)) {
            return false
        }
        requireNotNull(user)

        val userId = StorageId.externalId(user.id)
        val password = input.challengeResponse
        val authStatus = userService.authenticate(userId, password)
        log.info("Result: $authStatus")

        return when (authStatus) {
            AuthStatus.LOGIN_AUTHORISED,
                // this just means password expired but still allowed due to grace period
            AuthStatus.PASSWORD_PAST_EXPIRATION_DATE,
                // this means user exceeded grace period and is required to change password
                // this will be handled later as required actions
            AuthStatus.NO_GRACE_LOGINS_LEFT -> true
            else -> false
        }
    }

    override fun updateCredential(realm: RealmModel?, user: UserModel?, input: CredentialInput?): Boolean {
        log.info("updateCredential: $session : $realm : $user : $input")
        userService.changePassword(requireNotNull(user).username, requireNotNull(input).challengeResponse)
        return true
    }

    override fun getUserById(realm: RealmModel?, id: String?): UserModel? {
        log.info("getUserById: $session : $realm : $id")
        return getUserByUsername(realm, StorageId.externalId(id))
    }

    override fun getUserByUsername(realm: RealmModel?, username: String?): UserModel? {
        log.info("getUserByUsername: $session : $realm : $username")
        return userService.findUserById(requireNotNull(username))
            ?.let { LinzUserAdapter(session, requireNotNull(realm), model, it) }
    }

    override fun getUserByEmail(realm: RealmModel?, email: String?): UserModel? {
        log.info("getUserByEmail: $session : $realm : $email")
        return userService.findUserByEmail(requireNotNull(email))
            ?.let { LinzUserAdapter(session, requireNotNull(realm), model, it) }
    }

    override fun getUsersCount(realm: RealmModel?, search: String?, groupIds: Set<String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getUsersCount(realm: RealmModel?, params: Map<String, String>?, groupIds: Set<String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getUsersStream(realm: RealmModel?, firstResult: Int?, maxResults: Int?): Stream<UserModel> {
        log.info("getUsersStream: $session : $realm : $firstResult : $maxResults")
        return userService.getUsers(firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, requireNotNull(realm), model, it) }
    }

    override fun getUsersCount(realm: RealmModel?, search: String?): Int {
        log.info("getUsersCount: $session : $realm : $search")
        return userService.countUsersLikeUserId(requireNotNull(search))
    }

    override fun searchForUserStream(
        realm: RealmModel?,
        search: String?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("searchForUserStream(search): $session : $realm : $search : $firstResult : $maxResults")
        return userService.getUsersLikeUserId(requireNotNull(search), firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, requireNotNull(realm), model, it) }
    }

    override fun getUsersCount(realm: RealmModel?, params: Map<String, String>?): Int {
        return userService.countUsers()
    }

    override fun searchForUserStream(
        realm: RealmModel?,
        params: Map<String, String>?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("searchForUserStream(params): $session : $realm : $params : $firstResult : $maxResults")
        return userService.getUsers(firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, requireNotNull(realm), model, it) }
    }

    override fun getGroupMembersStream(
        realm: RealmModel?,
        group: GroupModel?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("getGroupMembersStream: $session : $realm : $group : $firstResult : $maxResults")
        TODO("Not yet implemented")
    }

    override fun getRoleMembersStream(
        realm: RealmModel?,
        role: RoleModel?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("getRoleMembersStream: $session : $realm : $role : $firstResult : $maxResults")
        return super.getRoleMembersStream(realm, role, firstResult, maxResults)
    }

    override fun searchForUserByUserAttributeStream(
        realm: RealmModel?,
        attrName: String?,
        attrValue: String?
    ): Stream<UserModel> {
        log.info("searchForUserByUserAttributeStream: $session : $realm : $attrName : $attrValue")
        TODO("Not yet implemented")
    }

    override fun disableCredentialType(realm: RealmModel?, user: UserModel?, credentialType: String?) {
        log.info("disableCredentialType: $session : $realm : $user : $credentialType")
    }

    override fun getDisableableCredentialTypesStream(realm: RealmModel?, user: UserModel?): Stream<String> {
        log.info("getDisableableCredentialTypes: $session : $realm : $user")
        return Stream.of()
    }

    override fun close() {}
}