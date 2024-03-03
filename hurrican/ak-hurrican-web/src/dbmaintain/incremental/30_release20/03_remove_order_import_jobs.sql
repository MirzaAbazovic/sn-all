DELETE FROM QRTZ_JOB_LISTENERS where JOB_NAME in ('importMaxiAuftraegeJob', 'importOptionOrderJob');
DELETE FROM QRTZ_JOB_DETAILS where JOB_NAME in ('importMaxiAuftraegeJob', 'importOptionOrderJob');
