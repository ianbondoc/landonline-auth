package nz.govt.linz

import io.ebean.Database
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
    private val model: ComponentModel,
    database: Database
) :
    UserStorageProvider, UserLookupProvider.Streams, UserQueryProvider.Streams, CredentialInputUpdater.Streams,
    CredentialInputValidator {

    private val log by lazy { LoggerFactory.getLogger(LinzUserStorageProvider::class.java) }

    private val userService: UserService

    init {
        log.info("CREATING LinzUserStorageProvider : ${session::class.qualifiedName}")
        userService = UserService(database)
    }

    override fun supportsCredentialType(credentialType: String?): Boolean {
        log.info("supportsCredentialType: $credentialType")
        return PasswordCredentialModel.TYPE == credentialType
    }

    override fun isConfiguredFor(realm: RealmModel?, user: UserModel?, credentialType: String?): Boolean {
        log.info("isConfiguredFor: $realm, $user, $credentialType")
        return supportsCredentialType(credentialType)
    }

    override fun isValid(realm: RealmModel?, user: UserModel?, input: CredentialInput?): Boolean {
        log.info("isValid: $realm : $user : $input")
        if (!(supportsCredentialType(input?.type) && input is UserCredentialModel)) {
            return false
        }
        requireNotNull(user)

        val userId = StorageId.externalId(user.id)
        val password = input.challengeResponse
        log.info("Credentials: $userId : $password")
        return userService.authenticate(userId, password)
    }

    override fun updateCredential(realm: RealmModel?, user: UserModel?, input: CredentialInput?): Boolean {
        log.info("updateCredential: $realm : $user : $input")
        TODO("Not yet implemented")
    }

    override fun getUserById(realm: RealmModel?, id: String?): UserModel? {
        log.info("getUserById: $realm : $id")
        return getUserByUsername(realm, StorageId.externalId(id))
    }

    override fun getUserByUsername(realm: RealmModel?, username: String?): UserModel? {
        log.info("getUserByUsername: $realm : $username")
        return userService.findUserById(checkNotNull(username))
            ?.let { LinzUserAdapter(session, checkNotNull(realm), model, it) }
    }

    override fun getUserByEmail(realm: RealmModel?, email: String?): UserModel? {
        log.info("getUserByEmail: $realm : $email")
        return userService.findUserByEmail(checkNotNull(email))
            ?.let { LinzUserAdapter(session, checkNotNull(realm), model, it) }
    }

    override fun getUsersCount(realm: RealmModel?, search: String?, groupIds: Set<String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getUsersCount(realm: RealmModel?, params: Map<String, String>?, groupIds: Set<String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getUsersStream(realm: RealmModel?, firstResult: Int?, maxResults: Int?): Stream<UserModel> {
        log.info("getUsersStream: $realm : $firstResult : $maxResults")
        return userService.getUsers(firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, checkNotNull(realm), model, it) }
    }

    override fun getUsersCount(realm: RealmModel?, search: String?): Int {
        return userService.countUsersLikeUserId(checkNotNull(search))
    }

    override fun searchForUserStream(
        realm: RealmModel?,
        search: String?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("searchForUserStream(search): $realm : $search : $firstResult : $maxResults")
        return userService.getUsersLikeUserId(checkNotNull(search), firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, checkNotNull(realm), model, it) }
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
        log.info("searchForUserStream(params): $realm : $params : $firstResult : $maxResults")
        return userService.getUsers(firstResult ?: -1, maxResults ?: -1)
            .map { LinzUserAdapter(session, checkNotNull(realm), model, it) }
    }

    override fun getGroupMembersStream(
        realm: RealmModel?,
        group: GroupModel?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("getGroupMembersStream: $realm : $group : $firstResult : $maxResults")
        TODO("Not yet implemented")
    }

    override fun getRoleMembersStream(
        realm: RealmModel?,
        role: RoleModel?,
        firstResult: Int?,
        maxResults: Int?
    ): Stream<UserModel> {
        log.info("getRoleMembersStream: $realm : $role : $firstResult : $maxResults")
        return super.getRoleMembersStream(realm, role, firstResult, maxResults)
    }

    override fun searchForUserByUserAttributeStream(
        realm: RealmModel?,
        attrName: String?,
        attrValue: String?
    ): Stream<UserModel> {
        log.info("searchForUserByUserAttributeStream: $realm : $attrName : $attrValue")
        TODO("Not yet implemented")
    }

    override fun disableCredentialType(realm: RealmModel?, user: UserModel?, credentialType: String?) {
        log.info("disableCredentialType: $realm : $user : $credentialType")
    }

    override fun getDisableableCredentialTypesStream(realm: RealmModel?, user: UserModel?): Stream<String> {
        log.info("getDisableableCredentialTypes: $realm : $user")
        return Stream.of()
    }

    override fun close() {}
}