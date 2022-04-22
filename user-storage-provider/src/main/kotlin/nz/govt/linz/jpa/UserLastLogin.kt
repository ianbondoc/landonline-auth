package nz.govt.linz.jpa

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "crs_user_last_login")
@Entity
data class UserLastLogin(
    @Id
    @Column(name = "usr_id")
    val usrId: String,

    @Column(name = "prior_login")
    val lastLogin: OffsetDateTime?
)
