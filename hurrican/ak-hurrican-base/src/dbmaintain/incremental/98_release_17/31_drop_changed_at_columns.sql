-- trigger entfernen, wenn vorhanden
CREATE OR REPLACE PROCEDURE drop_trigger_if_exists(trigger_name IN varchar2)
AS
    trigger_exists number;
    BEGIN
        SELECT count(*) INTO trigger_exists FROM USER_OBJECTS WHERE OBJECT_TYPE = 'TRIGGER' AND OBJECT_NAME = trigger_name;

        IF trigger_exists > 0 THEN
            execute immediate concat('drop trigger ', trigger_name);
        END IF;
   END;
/

call drop_trigger_if_exists('T_REFERENCE_CHANGED_AT');
call drop_trigger_if_exists('T_PHYSIKTYP_CHANGED_AT');
call drop_trigger_if_exists('T_TECH_LEISTUNG_CHANGED_AT');
call drop_trigger_if_exists('T_DSLAM_PROFILE_CHANGED_AT');

drop procedure drop_trigger_if_exists;

-- drop changed_at columns ...
alter table T_REFERENCE drop column changed_at;
alter table T_PHYSIKTYP drop column changed_at;
alter table T_TECH_LEISTUNG drop column changed_at;
alter table T_DSLAM_PROFILE drop column changed_at;