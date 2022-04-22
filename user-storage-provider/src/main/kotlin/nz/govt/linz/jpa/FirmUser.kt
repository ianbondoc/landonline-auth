package nz.govt.linz.jpa

import io.ebean.annotation.Formula
import javax.persistence.*

@Embeddable
data class FirmUserId(
    @Column(name = "usr_id_user")
    val userId: String,

    @Column(name = "usr_id_firm")
    val firmId: String
)

@Entity
@Table(name = "crs_firm_user")
class FirmUser(
    @EmbeddedId
    val id: FirmUserId,

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "usr_id_user")
    val user: User,

    @MapsId("firmId")
    @ManyToOne
    @JoinColumn(name = "usr_id_firm")
    val firm: Firm,

    val email: String?,
    private val associated: String,

    @Formula(
        select = "(select genxml(row(privilege_name), \"row\")::lvarchar FROM " +
                "(SELECT DISTINCT gpp.privilege_name " +
                "FROM crs_user_group ugp " +
                "JOIN crs_group grp on ugp.grp_id = grp.id " +
                "JOIN crs_grp_privilege gpp on gpp.grp_id = grp.id " +
                "WHERE ugp.usr_id = \${ta}.usr_id_user " +
                "AND grp.firm_id = \${ta}.usr_id_firm " +
                "UNION " +
                "SELECT privilege_name " +
                "FROM crs_user_privilege upp " +
                "WHERE upp.usr_id = \${ta}.usr_id_user " +
                "AND upp.usr_id_firm = \${ta}.usr_id_firm))"
    )
    private val privilegesXml: String?
) {
    fun isAssociated(): Boolean = associated == "Y"

    val privileges: List<String>
        get() = privilegesXml?.let {
            Regex("privilege_name=\"(.+)\"").findAll(privilegesXml).map { it.groupValues[1] }
                .distinct().toList()
        } ?: emptyList()
}
