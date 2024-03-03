-- @terminator /
-- @operation connect as sys and create bulk directories for external tables
CREATE OR REPLACE DIRECTORY BULKACCOUNT_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ACCOUNT/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKACCOUNT_0_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ACCOUNT-0/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKACCOUNT_1_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ACCOUNT-1/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKACCOUNT_2_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ACCOUNT-2/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKACCOUNT_3_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ACCOUNT-3/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKIAC_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.IAC/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKUDR_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.UDR/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKBILLING_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.BILLING/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKARCHIVE_INSERT_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ARCHIVE_INSERT/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY BULKARCHIVE_PRINT_${db.user.postfix}
AS '${db.taifun.data.basedir}/bulk/BULK.ARCHIVE_PRINT/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY SAPIS_DATA_DIR_${db.user.postfix}
AS '${db.taifun.data.basedir}/SAPIS/Import/${db.user.postfix}'
/
CREATE OR REPLACE DIRECTORY TAIFUN_DATAPUMP_${db.user.postfix}
AS '${db.taifun.data.basedir}/datapump/${db.user.postfix}'
/
-- @operation grant read, write on directories to database owner
GRANT READ,WRITE ON DIRECTORY BULKACCOUNT_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKACCOUNT_0_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKACCOUNT_1_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKACCOUNT_2_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKACCOUNT_3_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKIAC_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKUDR_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKBILLING_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKARCHIVE_INSERT_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY BULKARCHIVE_PRINT_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY SAPIS_DATA_DIR_${db.user.postfix} TO ${db.user.schema}
/
GRANT READ,WRITE ON DIRECTORY TAIFUN_DATAPUMP_${db.user.postfix} TO ${db.user.schema}
/
