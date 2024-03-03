-- Die T_TAM_USER_TASK tabelle wird in T_USER_TASK umbenannt da es sowohl für die Berabeitung
-- der TAMs als auch der Kuendigungen DTAG gebraucht wird

alter table T_TAM_USER_TASK drop constraint T_TAM_USER_TASK_TAM_STATUS;

revoke select on S_T_TAM_USER_TASK_0 from public;
drop sequence S_T_TAM_USER_TASK_0;

revoke select, insert, update, delete on T_TAM_USER_TASK from R_HURRICAN_USER;

alter table T_TAM_USER_TASK rename to T_USER_TASK;

alter table T_USER_TASK add VORGANG_TYP VARCHAR2(10 BYTE);
update T_USER_TASK set VORGANG_TYP = 'TAM';

alter table T_USER_TASK rename column TAM_STATUS to BEARBEITUNG_STATUS;

alter table T_USER_TASK add (
  constraint T_USER_TASK_BEARBEITUNG_STATUS
  check (BEARBEITUNG_STATUS in ('OFFEN','KUNDE_NICHT_ERREICHT','KUNDE_MELDET_SICH','TV_60_TAGE','INFO_MAILED')));

grant select, insert, update, delete on T_USER_TASK to R_HURRICAN_USER;
grant select on T_USER_TASK to R_HURRICAN_READ_ONLY;

create sequence S_T_USER_TASK_0 start with 1;
grant select on S_T_USER_TASK_0 to public;

alter table T_CB_VORGANG rename column TAM_USER_TASK_ID to USER_TASK_ID;
