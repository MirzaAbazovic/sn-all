DELETE FROM QRTZ_CRON_TRIGGERS where TRIGGER_NAME='synchGeoIdCacheJobTriggerDay' or TRIGGER_NAME='synchGeoIdCacheJobTriggerNight';
DELETE FROM QRTZ_TRIGGERS where TRIGGER_NAME='synchGeoIdCacheJobTriggerDay' or TRIGGER_NAME='synchGeoIdCacheJobTriggerNight';
DELETE FROM QRTZ_JOB_LISTENERS where JOB_NAME='synchGeoIdCacheJob';
DELETE FROM QRTZ_JOB_DETAILS where JOB_NAME='synchGeoIdCacheJob';
