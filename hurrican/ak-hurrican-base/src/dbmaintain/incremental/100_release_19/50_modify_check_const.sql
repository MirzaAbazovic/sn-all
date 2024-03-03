alter table T_HVT_UMZUG modify STATUS VARCHAR2(15);

alter table T_HVT_UMZUG drop constraint CHK_HVT_UMZUG_STATUS;
alter table T_HVT_UMZUG add CONSTRAINT CHK_HVT_UMZUG_STATUS check
  (status in ('OFFEN', 'PLANUNG_ERFOLGT', 'AUSGEFUEHRT', 'BEENDET', 'DEAKTIVIERT'));
