package nz.govt.linz.jpa

import io.ebean.annotation.DbEnumValue

enum class LoginFlag(@get:DbEnumValue val dbValue: String) {
    CAN_LOGIN("Y"), CANNOT_LOGIN("N")
}
