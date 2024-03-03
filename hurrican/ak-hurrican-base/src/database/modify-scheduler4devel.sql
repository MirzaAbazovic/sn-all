-- ATTENTIION: File must not contain ; to close a statement instead each statement is closed by / in a separate line

-- delete all sleeping/suspended jobs and triggers of scheduler for development database
DELETE FROM QRTZ_CRON_TRIGGERS
/
DELETE FROM QRTZ_BLOB_TRIGGERS
/
DELETE FROM QRTZ_SIMPLE_TRIGGERS
/
DELETE FROM QRTZ_TRIGGER_LISTENERS
/
DELETE FROM QRTZ_TRIGGERS
/
DELETE FROM QRTZ_JOB_LISTENERS
/
DELETE FROM QRTZ_JOB_DETAILS
/

-- sequence pauschal erhoehen, da Oracle es nicht schafft einen konsistenten dump zu erzeugen ...
declare
       type tabs is table of number index by pls_integer;
       c tabs;
    begin
       for counter in (select sequence_name n from user_sequences)
    loop
        execute immediate
       'select '||counter.n||'.nextval from dual connect by level<=1000'
        bulk collect into c;
    end loop;
end;
/
