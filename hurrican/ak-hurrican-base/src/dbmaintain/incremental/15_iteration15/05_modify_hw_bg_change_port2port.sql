
drop table T_HW_BG_CHANGE_PORT2PORT;
create table T_HW_BG_CHANGE_PORT2PORT (
    ID NUMBER(10) NOT NULL
  , HW_BG_CHANGE_ID NUMBER(10) NOT NULL
  , EQ_ID_OLD NUMBER(10)
  , EQ_ID_OLD_IN NUMBER(10)
  , EQ_ID_NEW NUMBER(10)
  , EQ_ID_NEW_IN NUMBER(10)
  , EQ_STATE_ORIG_OLD VARCHAR2(50)
  , EQ_STATE_ORIG_NEW VARCHAR2(50)
  , RANG_FREIGABE_ORIG_OLD CHAR(1)
  , RANG_FREIGABE_ORIG_NEW CHAR(1)
  , VERSION NUMBER(18) DEFAULT 0 NOT NULL
);
comment on table T_HW_BG_CHANGE_PORT2PORT is 'Tabelle fuer Protokollierung der Port-Wechsel';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_OLD is 'ID des <alten> Equipments';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_OLD_IN is 'ID des zugehoerigen <alten> Equipments (2. ADSL Port)';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_NEW is 'ID des <neuen> Equipments';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_ID_NEW_IN is 'ID des zugehoerigen <neuen> Equipments (2. ADSL Port)';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_STATE_ORIG_OLD is 'Status des <alten> Equipments VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.EQ_STATE_ORIG_NEW is 'Status des <neuen> Equipments VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.RANG_FREIGABE_ORIG_OLD is 'Status der <alten> Rangierung VOR der Planung - wichtig bei Abbruch der Planung';
comment on column T_HW_BG_CHANGE_PORT2PORT.RANG_FREIGABE_ORIG_NEW is 'Status der <neuen> Rangierung VOR der Planung - wichtig bei Abbruch der Planung';

grant select on S_T_HW_BG_CHANGE_PORT2PORT_0 to public;

ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT PK_T_HW_BG_CHANGE_PORT2PORT PRIMARY KEY (ID);

CREATE INDEX IX_FK_HWBGCP2P_2_HWBGC ON T_HW_BG_CHANGE_PORT2PORT (HW_BG_CHANGE_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_HWBGC
  FOREIGN KEY (HW_BG_CHANGE_ID) REFERENCES T_HW_BG_CHANGE (ID);

CREATE INDEX IX_FK_HWBGCP2P_2_EQNEW ON T_HW_BG_CHANGE_PORT2PORT (EQ_ID_NEW) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_EQNEW
  FOREIGN KEY (EQ_ID_NEW) REFERENCES T_EQUIPMENT (EQ_ID);

CREATE INDEX IX_FK_HWBGCP2P_2_EQOLD ON T_HW_BG_CHANGE_PORT2PORT (EQ_ID_OLD) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_EQOLD
  FOREIGN KEY (EQ_ID_OLD) REFERENCES T_EQUIPMENT (EQ_ID);

CREATE INDEX IX_FK_HWBGCP2P_2_EQNEWIN ON T_HW_BG_CHANGE_PORT2PORT (EQ_ID_NEW_IN) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_EQNEWIN
  FOREIGN KEY (EQ_ID_NEW_IN) REFERENCES T_EQUIPMENT (EQ_ID);

CREATE INDEX IX_FK_HWBGCP2P_2_EQOLDIN ON T_HW_BG_CHANGE_PORT2PORT (EQ_ID_OLD_IN) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE_PORT2PORT ADD CONSTRAINT FK_HWBGCP2P_2_EQOLDIN
  FOREIGN KEY (EQ_ID_OLD_IN) REFERENCES T_EQUIPMENT (EQ_ID);


GRANT SELECT ON T_HW_BG_CHANGE_PORT2PORT TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_HW_BG_CHANGE_PORT2PORT TO R_HURRICAN_USER;