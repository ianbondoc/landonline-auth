package nz.govt.linz.jpa

import io.ebean.annotation.DbEnumValue

enum class UserStatus(@get:DbEnumValue val dbValue: String) {
    ACTIVE("ACTV"),
    INACTIVE("DACT")
}
