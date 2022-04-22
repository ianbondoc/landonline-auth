package nz.govt.linz

import org.keycloak.models.RealmModel
import org.keycloak.models.RoleContainerModel
import org.keycloak.models.RoleModel
import java.util.stream.Stream

class LinzRoleAdapter(private val role: String, private val realmModel: RealmModel): RoleModel {

    override fun getId(): String = role

    override fun getName(): String = role

    override fun setName(name: String?) {
        TODO("Not yet implemented")
    }

    override fun getDescription(): String = "$role role"

    override fun setDescription(description: String?) {
        TODO("Not yet implemented")
    }

    override fun isComposite(): Boolean = false

    override fun addCompositeRole(role: RoleModel?) {
        TODO("Not yet implemented")
    }

    override fun removeCompositeRole(role: RoleModel?) {
        TODO("Not yet implemented")
    }

    override fun getCompositesStream(search: String?, first: Int?, max: Int?): Stream<RoleModel> {
        TODO("Not yet implemented")
    }

    override fun isClientRole(): Boolean = false

    override fun getContainerId(): String = realmModel.id

    override fun getContainer(): RoleContainerModel = realmModel

    override fun hasRole(role: RoleModel?): Boolean = false

    override fun setSingleAttribute(name: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun setAttribute(name: String?, values: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun removeAttribute(name: String?) {
        TODO("Not yet implemented")
    }

    override fun getFirstAttribute(name: String?): String {
        return super.getFirstAttribute(name)
    }

    override fun getAttributeStream(name: String?): Stream<String> {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): MutableMap<String, MutableList<String>> {
        TODO("Not yet implemented")
    }
}