delete from QRTZ_CRON_TRIGGERS where trigger_name='exportNeuschaltungenCommandJobTrigger';
delete from QRTZ_TRIGGERS where trigger_name='exportNeuschaltungenCommandJobTrigger';
delete from QRTZ_JOB_LISTENERS where job_name='exportNeuschaltungenCommandJob';
delete from QRTZ_JOB_DETAILS where job_name='exportNeuschaltungenCommandJob';
