CREATE OR REPLACE TRIGGER TRBI_RANGIERUNG BEFORE INSERT on T_RANGIERUNG
for each row
declare
begin
   -- ID und DATEW setzen
   IF :new.RANGIER_ID is null THEN
       select S_T_RANGIERUNG_0.nextval into :new.RANGIER_ID from dual;
   END IF;
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
   SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
   :new.LAST_CHANGE_BY := USER;
end;
/
