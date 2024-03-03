-- already executed in production environment but here as dbMaintain script due to documentation issues
ALTER TABLE T_EXCEPTION_LOG MODIFY(ERROR_MESSAGE VARCHAR2(2048 BYTE));
