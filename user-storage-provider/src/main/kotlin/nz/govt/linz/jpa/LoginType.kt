package nz.govt.linz.jpa

import io.ebean.annotation.DbEnumValue

enum class LoginType(@get:DbEnumValue val dbValue: String) {
    EXTERNAL("EXTN"),
    INTERNAL("INTN"),
    PUBLIC("PUBL"),
    SERVER("SRVR")
}
