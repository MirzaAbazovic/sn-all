-- Neuer Bearbeitungsstatus wurde hinzugefügt; Constraint muß geänderz werden --

alter table T_USER_TASK drop constraint T_USER_TASK_BEARBEITUNG_STATUS;

alter table T_USER_TASK add (
  constraint T_USER_TASK_BEARBEITUNG_STATUS
  check (BEARBEITUNG_STATUS in ('OFFEN','KUNDE_NICHT_ERREICHT','KUNDE_MELDET_SICH','TV_60_TAGE','INFO_MAILED','CLOSED')));

