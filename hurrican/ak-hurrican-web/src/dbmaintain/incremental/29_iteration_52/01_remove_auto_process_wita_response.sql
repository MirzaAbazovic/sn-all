DELETE FROM QRTZ_CRON_TRIGGERS where TRIGGER_NAME='automaticallyProcessWitaResponseJobTrigger';
DELETE FROM QRTZ_TRIGGERS where TRIGGER_NAME='automaticallyProcessWitaResponseJobTrigger';
DELETE FROM QRTZ_JOB_LISTENERS where JOB_NAME='automaticallyProcessWitaResponseJob';
DELETE FROM QRTZ_JOB_DETAILS where JOB_NAME='automaticallyProcessWitaResponseJob';
