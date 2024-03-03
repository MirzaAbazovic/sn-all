alter table T_HVT_UMZUG drop constraint CHK_HVT_UMZUG_STATUS;
alter table T_HVT_UMZUG add CONSTRAINT CHK_HVT_UMZUG_STATUS check (status in ('OFFEN', 'IN_PLANUNG', 'ERLEDIGT', 'DEAKTIVIERT'));
