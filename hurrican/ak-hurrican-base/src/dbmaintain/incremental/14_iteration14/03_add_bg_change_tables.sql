--
-- Script um die Tabellenstruktur fuer Baugruppenaenderungen aufzubauen
--

create table T_HW_BG_CHANGE (
    ID NUMBER(10) NOT NULL
  , HVT_STANDORT_ID NUMBER(10) NOT NULL
  , PLANNED_DATE DATE NOT NULL
  , PLANNED_FROM VARCHAR2(25) NOT NULL
  , CHANGE_TYPE_REF_ID NUMBER(10)
  , EXECUTED_AT DATE
  , EXECUTED_FROM VARCHAR2(25)
  , CANCELLED_AT DATE
  , CANCELLED_FROM VARCHAR2(25)
  , VERSION NUMBER(18) DEFAULT 0 NOT NULL
);
comment on table T_HW_BG_CHANGE is 'Tabelle fuer Planungen von Baugruppen-Schwenks';

create SEQUENCE S_T_HW_BG_CHANGE_0 start with 1;
grant select on S_T_HW_BG_CHANGE_0 to public;

ALTER TABLE T_HW_BG_CHANGE ADD CONSTRAINT PK_T_HW_BG_CHANGE PRIMARY KEY (ID);

CREATE INDEX IX_FK_HWBGC_2_HVTSTD ON T_HW_BG_CHANGE (HVT_STANDORT_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE ADD CONSTRAINT FK_HWBGC_2_HVTSTD
  FOREIGN KEY (HVT_STANDORT_ID) REFERENCES T_HVT_STANDORT (HVT_ID_STANDORT);

CREATE INDEX IX_FK_HWBGCTYP_2_REFERENCE ON T_HW_BG_CHANGE (CHANGE_TYPE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE ADD CONSTRAINT FK_HWBGCTYP_2_REFERENCE
  FOREIGN KEY (CHANGE_TYPE_REF_ID) REFERENCES T_REFERENCE (ID);


GRANT SELECT ON T_HW_BG_CHANGE TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_HW_BG_CHANGE TO R_HURRICAN_USER;



create table T_HW_BG_CHANGE_PORT2PORT (
    ID NUMBER(10) NOT NULL
  , HW_BG_CHANGE_ID NUMBER(10) NOT NULL
  , EQ_ID_OLD NUMBER(10)
  , EQ_ID_NEW NUMBER(10)
  , EQ_STATE_ORIG_OLD VARCHAR2(50)
  , EQ_STATE_ORIG_NEW VARCHAR2(50)
  , RANG_FREIGABE_ORIG_OLD CHAR(1)
  , RANG_FREIGABE_ORIG_NEW CHAR(1)
  , VERSION NUMBER(18) DEFAULT 0 NOT NULL
);
comment on table T_HW_BG_CHANGE_PORT2PORT is 'Tabelle fuer Protokollierung der Port-Wechsel';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_OLD is 'ID des <alten> Equipments';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_NEW is 'ID des <neuen> Equipments';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_STATE_ORIG_OLD is 'Status des <alten> Equipments VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_STATE_ORIG_NEW is 'Status des <neuen> Equipments VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.RANG_FREIGABE_ORIG_OLD is 'Status der <alten> Rangierung VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.RANG_FREIGABE_ORIG_NEW is 'Status der <neuen> Rangierung VOR der Planung - wichtig bei Abbruch der Planung';

create SEQUENCE S_T_HW_BG_CHANGE_PORT2PORT_0 start with 1;
grant select on S_T_HW_BG_CHANGE_PORT2PORT_0 to public;

ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT PK_T_HW_BG_CHANGE_PORT2PORT PRIMARY KEY (ID);

CREATE INDEX IX_FK_HWBGCP2P_2_HWBGC ON T_HW_BG_CHANGE_PORT2PORT (HW_BG_CHANGE_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_HWBGC
  FOREIGN KEY (HW_BG_CHANGE_ID) REFERENCES T_HW_BG_CHANGE (ID);

GRANT SELECT ON T_HW_BG_CHANGE_PORT2PORT TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_HW_BG_CHANGE_PORT2PORT TO R_HURRICAN_USER;



