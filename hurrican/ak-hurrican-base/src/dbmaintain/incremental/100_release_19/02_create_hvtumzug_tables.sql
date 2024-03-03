CREATE TABLE T_HVT_UMZUG
(
   ID decimal(19) PRIMARY KEY NOT NULL,
   HVT_STANDORT_ID decimal(19) NOT NULL,
   KVZ_NUMMER VARCHAR2(5 BYTE),
   BEARBEITER VARCHAR2(64 BYTE),
   STATUS  VARCHAR2(12) not null,
   SWITCH_AT TIMESTAMP,
   IMPORT_AT TIMESTAMP,
   XLSDATA BLOB
);

alter table T_HVT_UMZUG add constraint CHK_HVT_UMZUG_STATUS check (STATUS in ('OFFEN', 'IN_PLANNUNG', 'ERLEDIGT'));

CREATE SEQUENCE S_T_HVT_UMZUG START WITH 1;

CREATE TABLE T_HVT_UMZUG_DETAIL
(
   HVT_UMZUG_ID decimal(19) NOT NULL,
   AUFTRAG_ID decimal(19),
   PROD_ID decimal(19),
   AUFTRAG_STATUS_ID decimal(19),
   BEREITSTELLUNG_AM date,
   VTRNR varchar2(25 BYTE),
   UEVT_ID decimal(19),
   UEVT_ID_NEU decimal(19)
);

ALTER TABLE T_HVT_UMZUG_DETAIL ADD CONSTRAINT HVTUMZUGDETAIL_FK_HVTUMZUG
FOREIGN KEY (HVT_UMZUG_ID)
REFERENCES T_HVT_UMZUG(ID);
