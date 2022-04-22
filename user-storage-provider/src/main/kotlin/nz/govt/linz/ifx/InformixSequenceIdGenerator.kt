package nz.govt.linz.ifx

import io.ebean.BackgroundExecutor
import io.ebean.config.dbplatform.SequenceBatchIdGenerator
import javax.sql.DataSource

class InformixSequenceIdGenerator(be: BackgroundExecutor?, ds: DataSource?, seqName: String?, batchSize: Int) :
    SequenceBatchIdGenerator(be, ds, seqName, batchSize) {
    private val baseSql: String = "SELECT $seqName.NEXTVAL FROM SYSMASTER:SYSDUAL LIMIT 1"
    override fun getSql(batchSize: Int): String {
        return baseSql
    }
}
