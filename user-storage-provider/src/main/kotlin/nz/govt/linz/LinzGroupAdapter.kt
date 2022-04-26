package nz.govt.linz

import nz.govt.linz.jpa.Firm
import org.keycloak.models.ClientModel
import org.keycloak.models.GroupModel
import org.keycloak.models.RoleModel
import org.keycloak.storage.ReadOnlyException
import java.util.stream.Stream

class LinzGroupAdapter(val firm: Firm) : GroupModel.Streams {

    override fun getId(): String = firm.id
    override fun getName(): String = firm.id
    override fun setName(name: String?) = throw ReadOnlyException("group is read only")
    override fun setSingleAttribute(name: String?, value: String?) = throw ReadOnlyException("group is read only")
    override fun setAttribute(name: String?, values: MutableList<String>?) =
        throw ReadOnlyException("group is read only")

    override fun removeAttribute(name: String?) = throw ReadOnlyException("group is read only")
    override fun getFirstAttribute(name: String?): String? = null
    override fun getAttributeStream(name: String?): Stream<String> = Stream.empty()
    override fun getAttributes(): Map<String, List<String>> = emptyMap()
    override fun getParent(): GroupModel? = null
    override fun getParentId(): String? = null
    override fun getSubGroupsStream(): Stream<GroupModel> = Stream.empty()
    override fun setParent(group: GroupModel?) = throw ReadOnlyException("group is read only")
    override fun addChild(subGroup: GroupModel?) = throw ReadOnlyException("group is read only")
    override fun removeChild(subGroup: GroupModel?) = throw ReadOnlyException("group is read only")
    override fun getRealmRoleMappingsStream(): Stream<RoleModel> = Stream.empty()
    override fun getClientRoleMappingsStream(app: ClientModel?): Stream<RoleModel> = Stream.empty()
    override fun hasRole(role: RoleModel?): Boolean = false
    override fun grantRole(role: RoleModel?) = throw ReadOnlyException("group is read only")
    override fun getRoleMappingsStream(): Stream<RoleModel> = Stream.empty()
    override fun deleteRoleMapping(role: RoleModel?) = throw ReadOnlyException("group is read only")
}