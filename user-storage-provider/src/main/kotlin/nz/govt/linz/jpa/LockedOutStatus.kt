package nz.govt.linz.jpa

import io.ebean.annotation.DbEnumValue

enum class LockedOutStatus(@get:DbEnumValue val dbValue: String) {
    NOT_LOCKED_OUT("F"),
    LOCKED_OUT("T"),
    MUST_CHANGE_PASSWORD_ON_NEXT_LOGIN("C")
}
