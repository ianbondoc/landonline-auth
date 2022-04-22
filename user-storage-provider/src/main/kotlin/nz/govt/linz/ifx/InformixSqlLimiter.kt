package nz.govt.linz.ifx

import io.ebean.config.dbplatform.SqlLimitRequest
import io.ebean.config.dbplatform.SqlLimitResponse
import io.ebean.config.dbplatform.SqlLimiter

class InformixSqlLimiter : SqlLimiter {
    private val LIMIT = "limit"
    private val OFFSET = "skip"

    override fun limit(request: SqlLimitRequest): SqlLimitResponse {

        val dbSql = request.dbSql

        val sb = StringBuilder(50 + dbSql.length)
        sb.append("select ")
        if (request.isDistinct) {
            sb.append("distinct ")
        }

        sb.append(dbSql)

        val firstRow = request.firstRow
        val maxRows = request.maxRows

        if (maxRows > 0 || firstRow > 0) {
            if (firstRow > 0) {
                sb.append(" ").append(OFFSET).append(" ")
                sb.append(firstRow)
            }
            sb.append(" ").append(LIMIT).append(" ").append(maxRows)
        }

        val sql = request.dbPlatform.completeSql(sb.toString(), request.ormQuery)

        return SqlLimitResponse(sql)
    }
}
