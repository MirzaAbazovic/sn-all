-- Neuer Bearbeitungsstatus wurde hinzugef�gt; Constraint mu� ge�nderz werden --

alter table T_USER_TASK drop constraint T_USER_TASK_BEARBEITUNG_STATUS;

alter table T_USER_TASK add (
  constraint T_USER_TASK_BEARBEITUNG_STATUS
  check (BEARBEITUNG_STATUS in ('OFFEN','KUNDE_NICHT_ERREICHT','KUNDE_MELDET_SICH','TV_60_TAGE','INFO_MAILED','CLOSED')));

