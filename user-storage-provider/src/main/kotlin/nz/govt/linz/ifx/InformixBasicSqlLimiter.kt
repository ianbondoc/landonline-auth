package nz.govt.linz.ifx

import io.ebean.config.dbplatform.BasicSqlLimiter

class InformixBasicSqlLimiter : BasicSqlLimiter {
    private val LIMIT = "limit"
    private val OFFSET = "skip"

    override fun limit(dbSql: String, firstRow: Int, maxRows: Int): String {

        val sb = StringBuilder(50 + dbSql.length)

        sb.append(dbSql)

        if (firstRow > 0) {
            sb.append(" ").append(OFFSET).append(" ")
            sb.append(firstRow)
        }
        if (maxRows > 0) {
            sb.append(" ").append(LIMIT)
            sb.append(" ").append(maxRows)
        }

        return sb.toString()
    }
}
