-- ATTENTIION: File must not contain ; to close a statement instead each statement is closed by / in a separate line

--
-- SQL-Script, um die AUTHENTICATION-DB in einen Devel-Modus zu setzen
-- (Pfade und eMails etc. werden auf Test-Werte geaendert).
--
-- Sequences um 100 erhoehen
alter sequence S_ROLE_0 increment by 100
/
alter sequence S_DEPARTMENT_0 increment by 100
/

update DB set URL='${db.jdbc.url}', SCHEMA='${db.hurrican.user.schema}' where ID in (4, 5, 10)
/
update DB set URL='${db.jdbc.url}', SCHEMA='${db.scheduler.user.schema}' where ID in (7)
/
update DB set URL='${db.taifun.jdbc.url}', SCHEMA='${db.taifun.user.schema}' where ID in (3)
/
update DB set URL='${db.kup.jdbc.url}', SCHEMA='${db.kup.user.schema}' where ID in (11,12)
/
update DB set URL='${db.monline.jdbc.url}', SCHEMA='${db.monline.user.schema}' where ID in (13)
/

update ACCOUNT set ACCOUNTUSER='${db.scheduler.user.schema}' where DB_ID=7
/

-- MAX_ACTIVE und MAX_IDLE von cc.writer (Connection auf Hurrican-DB) erhoehen, damit im Connection-Fehler
-- (Cannot get a connection, pool error Timeout waiting for idle object) verhindert werden.
update ACCOUNT set MAX_ACTIVE=15, MAX_IDLE=8 where ACC_NAME='cc.writer'
/

update USERS set ACTIVE='1' where LOGINNAME='UnitTest' or LOGINNAME='WitaUnitTest' or LOGINNAME='STOnline' or LOGINNAME='STVoice' or
     LOGINNAME='SysInt' or LOGINNAME='FirstLevel' or LOGINNAME='AuftragMgm' or LOGINNAME='ZPConnect' or LOGINNAME='vertrieb'
    or LOGINNAME='Dispo' or LOGINNAME='zpresourcen' or LOGINNAME='zptest' or LOGINNAME='ammuc' or LOGINNAME='schulung'
/

CREATE OR REPLACE FORCE VIEW V_EXT_SERVICE_PROVIDER (ID, NAME)
AS
   SELECT   ID, NAME
     FROM   ${db.hurrican.user.schema}.T_EXT_SERVICE_PROVIDER
/
CREATE OR REPLACE FORCE VIEW V_NIEDERLASSUNG (ID, TEXT)
AS
   SELECT   ID, TEXT
     FROM   ${db.hurrican.user.schema}.T_NIEDERLASSUNG
/

-- UserSession fuer AcceptanceTests anlegen
Insert into USERSESSION
   (SESSION_ID, USER_ID, LOGIN_TIME, DEPRECATION_TIME, HOSTUSER,
    HOSTNAME, IP, APP_ID, APP_VERSION, VERSION)
 Values
   (111, 1220, sysdate, TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'hurrican',
    'localhost', '127.0.0.1', 4, '1.0', 0)
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
