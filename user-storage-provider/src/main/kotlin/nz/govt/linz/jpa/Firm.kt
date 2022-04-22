package nz.govt.linz.jpa

import javax.persistence.*

@Table(name = "crs_user")
@Entity
data class Firm(
    @Id
    val id: String,
    val corporateName: String,
    val status: UserStatus,

    @Column(name = "cus_account_ref")
    val cusAccountRef: String?,

    @OneToMany(mappedBy = "firm")
    private val firmRelationships: List<FirmUser>
) {

    val hasCredit: Boolean
        get() {
            return "C99999" != cusAccountRef
        }

    val associations: List<FirmUser>
        get() = if (status == UserStatus.ACTIVE) {
            firmRelationships.filter { it.isAssociated() }
        } else {
            emptyList()
        }
}
