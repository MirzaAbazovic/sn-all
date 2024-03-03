--
-- SQL-Script, um Trigger anzulegen
--

-- Insert-Trigger fuer die Tabelle T_DSLAM_PROFILE_CHANGE
drop trigger TRI_DSLAM_PROFILE_CHANGE;
create trigger TRI_DSLAM_PROFILE_CHANGE before insert on T_DSLAM_PROFILE_CHANGE
for each row
when (new.ID is null)
begin
 select S_T_DSLAM_PROFILE_CHANGE_0.nextval into :new.ID from dual;
end;
/
commit;

-- Insert-Trigger fuer die Tabelle T_EQUIPMENT
drop trigger TRI_EQUIPMENT;
create trigger TRI_EQUIPMENT before insert on T_EQUIPMENT
for each row
when (new.EQ_ID is null)
begin
 select S_T_EQUIPMENT_0.nextval into :new.EQ_ID from dual;
end;
/
commit;

-- Insert-Trigger fuer die Tabelle T_RANGIERUNG
drop trigger TRI_RANGIERUNG;
create trigger TRI_RANGIERUNG before insert on T_RANGIERUNG
for each row
when (new.RANGIER_ID is null)
begin
 select S_T_RANGIERUNG_0.nextval into :new.RANGIER_ID from dual;
end;
/
commit;

