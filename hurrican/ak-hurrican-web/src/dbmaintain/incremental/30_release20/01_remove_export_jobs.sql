DELETE FROM QRTZ_CRON_TRIGGERS where TRIGGER_NAME in (
    'exportAuftraege4KdpMFullTrigger', 'exportAuftraege4KdpMFullWETrigger', 'exportAuftraege4KdpMTrigger');

DELETE FROM QRTZ_TRIGGERS where TRIGGER_NAME in (
    'exportAuftraege4KdpMFullTrigger', 'exportAuftraege4KdpMFullWETrigger', 'exportAuftraege4KdpMTrigger');

DELETE FROM QRTZ_JOB_LISTENERS where JOB_NAME in ('exportAuftraege4KdpMFullJob', 'exportAuftraege4KdpMJob');
DELETE FROM QRTZ_JOB_DETAILS where JOB_NAME in ('exportAuftraege4KdpMFullJob', 'exportAuftraege4KdpMJob');
