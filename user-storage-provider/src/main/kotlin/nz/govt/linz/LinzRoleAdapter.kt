package nz.govt.linz

import org.keycloak.models.RealmModel
import org.keycloak.models.RoleContainerModel
import org.keycloak.models.RoleModel
import org.keycloak.storage.ReadOnlyException
import java.util.stream.Stream

class LinzRoleAdapter(private val role: String, private val realmModel: RealmModel) : RoleModel {

    override fun getId(): String = role

    override fun getName(): String = role

    override fun setName(name: String?) = throw ReadOnlyException("role is read only")

    override fun getDescription(): String = "$role role"

    override fun setDescription(description: String?) = throw ReadOnlyException("role is read only")

    override fun isComposite(): Boolean = false

    override fun addCompositeRole(role: RoleModel?) = throw ReadOnlyException("role is read only")

    override fun removeCompositeRole(role: RoleModel?) = throw ReadOnlyException("role is read only")

    override fun getCompositesStream(search: String?, first: Int?, max: Int?): Stream<RoleModel> =
        throw ReadOnlyException("role is read only")

    override fun isClientRole(): Boolean = false

    override fun getContainerId(): String = realmModel.id

    override fun getContainer(): RoleContainerModel = realmModel

    override fun hasRole(role: RoleModel?): Boolean = false

    override fun setSingleAttribute(name: String?, value: String?) = throw ReadOnlyException("role is read only")

    override fun setAttribute(name: String?, values: MutableList<String>?) =
        throw ReadOnlyException("role is read only")

    override fun removeAttribute(name: String?) = throw ReadOnlyException("role is read only")

    override fun getFirstAttribute(name: String?): String {
        return super.getFirstAttribute(name)
    }

    override fun getAttributeStream(name: String?): Stream<String> = throw ReadOnlyException("role is read only")

    override fun getAttributes(): MutableMap<String, MutableList<String>> = throw ReadOnlyException("role is read only")
}