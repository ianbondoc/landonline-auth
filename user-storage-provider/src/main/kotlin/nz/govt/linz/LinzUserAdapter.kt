package nz.govt.linz

import nz.govt.linz.jpa.LoginType
import nz.govt.linz.jpa.User
import org.keycloak.common.util.MultivaluedHashMap
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.RoleModel
import org.keycloak.models.UserModel
import org.keycloak.storage.StorageId
import org.keycloak.storage.adapter.AbstractUserAdapter

class LinzUserAdapter(session: KeycloakSession, realm: RealmModel, model: ComponentModel, private val user: User) :
    AbstractUserAdapter.Streams(session, realm, model) {

    init {
        storageId = StorageId(model.id, user.id)
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

    override fun getFirstAttribute(name: String?): String? =
        attributes.getOrDefault(checkNotNull(name), emptyList()).let { if (it.isEmpty()) null else it.first() }

    override fun getAttributes(): Map<String, List<String>> = MultivaluedHashMap<String, String>().apply {
        add(UserModel.USERNAME, username)
        add(UserModel.EMAIL, email)
        add(UserModel.FIRST_NAME, firstName)
        add(UserModel.LAST_NAME, lastName)
        add("title", user.title)
        add("preferredName", user.preferredName)
        add("loginType", user.loginType.name)
    }

    override fun getRoleMappingsInternal(): Set<RoleModel> = roles

    override fun appendDefaultRolesToRoleMappings(): Boolean = false

    override fun appendDefaultGroups(): Boolean = false
}
