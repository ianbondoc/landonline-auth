package nz.govt.linz.jpa

import io.ebean.annotation.Formula
import javax.persistence.*

@Entity
@Table(name = "crs_user")
class User(
    @Id
    val id: String,
    val type: String,
    val title: String?,
    val givenNames: String,
    val surname: String,
    val emailAddress: String,
    val preferredName: String,
    val loginType: LoginType,
    @Column(name = "login")
    val loginFlag: LoginFlag,
    val status: UserStatus,
    @Column(name = "locked_out")
    val lockedOutStatus: LockedOutStatus,

    @OneToMany(mappedBy = "user")
    val firmRelationships: List<FirmUser>,

    @OneToOne
    @JoinColumn(name = "id", nullable = true)
    val userLastLogin: UserLastLogin?,

    @Formula(select = "(select genxml(row(type), \"type\")::lvarchar from crs_type_of_user where usr_id = \${ta}.id)")
    private val categoriesXml: String?,

    @Formula(select = "(select genxml(row(prf_id), \"profile\")::lvarchar from crs_user_profile where usr_id = \${ta}.id)")
    private val profilesXml: String?,
) {
    val associatedFirms: List<Firm>
        get() = firmRelationships.filter { it.isAssociated() && it.firm.status == UserStatus.ACTIVE }
            .map { it.firm }

    val categories: List<String>
        get() = categoriesXml?.let {
            Regex("type=\"(.+)\"").findAll(categoriesXml).map { "CATEGORY_${it.groupValues[1]}" }.distinct().toList()
        } ?: emptyList()

    val profiles: List<String>
        get() = profilesXml?.let {
            Regex("prf_id=\"(.+)\"").findAll(profilesXml).map { Profile.byId(it.groupValues[1]) }
                .map { "PROFILE_${it.name}" }.distinct().toList()
        } ?: emptyList()
}
