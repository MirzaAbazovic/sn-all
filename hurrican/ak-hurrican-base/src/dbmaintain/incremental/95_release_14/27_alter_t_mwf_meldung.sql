-- Tabelle T_MWF_MELDUNG um Spalte SMS_STATUS erweitern
alter table T_MWF_MELDUNG add (SMS_STATUS VARCHAR2(25 BYTE));

-- check constraint auf SMS_STATUS
alter table T_MWF_MELDUNG add constraint CHK_SMS_STATUS check (
  SMS_STATUS in ('OFFEN','GESENDET','KEINE_RN','VERALTET','UNGUELTIG','UNERWUENSCHT','KEINE_CONFIG','FALSCHER_AUFTRAGSTATUS'));

-- Initialbefüllung mit Status 'VERALTET'
update T_MWF_MELDUNG set SMS_STATUS = 'VERALTET';

-- SMS_STATUS muss in neuem Datensatz immer mit dem Default 'OFFEN' belegt werden
alter table T_MWF_MELDUNG modify (SMS_STATUS VARCHAR2(25 BYTE)  default 'OFFEN' not null);