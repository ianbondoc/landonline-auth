package nz.govt.linz.ifx

import io.ebean.BackgroundExecutor
import io.ebean.Query
import io.ebean.config.dbplatform.DatabasePlatform
import io.ebean.config.dbplatform.IdType
import io.ebean.config.dbplatform.PlatformIdGenerator
import javax.sql.DataSource

class InformixPlatform : DatabasePlatform() {

    init {
        // These classes are copied and modified from the basic implementations, because Informix requires
        // SKIP and LIMIT rather than LIMIT and OFFSET.
        this.sqlLimiter = InformixSqlLimiter()
        this.basicSqlLimiter = InformixBasicSqlLimiter()

        // Use Identity and getGeneratedKeys
        this.dbIdentity.idType = IdType.IDENTITY
        this.dbIdentity.isSupportsGetGeneratedKeys = true
        this.dbIdentity.isSupportsSequence = true
        this.dbIdentity.isSupportsIdentity = true

        // Prevent use of unsupported syntax for IN clauses
        this.idInExpandedForm = true
    }

    override fun createSequenceIdGenerator(be: BackgroundExecutor?, ds: DataSource?, stepSize: Int, seqName: String?): PlatformIdGenerator {
        return InformixSequenceIdGenerator(be, ds, seqName, sequenceBatchSize)
    }

    override fun getName(): String {
        return "InformixDatabase"
    }

    override fun withForUpdate(sql: String?, lockWait: Query.LockWait?, lockType: Query.LockType?): String {
        return "$sql for update"
    }
}
