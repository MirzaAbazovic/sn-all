
Insert into JOB_EXECUTION
   (ID, JOB_NAME, JOB_CLASS, START_TIME, END_TIME, NEXT_TIME)
 Values
   (S_JOB_EXECUTION_0.nextVal, 'synchGeoIdCacheJob', 'de.mnet.hurrican.scheduler.job.geoid.GeoIdSyncCacheJob',
    TO_DATE('01/26/2011 14:30:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/26/2011 14:30:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/26/2011 14:30:00', 'MM/DD/YYYY HH24:MI:SS'));

