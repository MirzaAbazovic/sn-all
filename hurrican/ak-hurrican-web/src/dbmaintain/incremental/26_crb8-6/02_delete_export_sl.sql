delete from QRTZ_CRON_TRIGGERS where trigger_name='exportSLTrigger';
delete from QRTZ_TRIGGERS where trigger_name='exportSLTrigger';
delete from QRTZ_JOB_LISTENERS where job_name='exportSLJob';
delete from QRTZ_JOB_DETAILS where job_name='exportSLJob';
