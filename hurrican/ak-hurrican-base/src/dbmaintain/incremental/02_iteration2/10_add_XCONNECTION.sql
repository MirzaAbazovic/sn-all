--
-- SQL-Script, um die notwendige Datenstruktur fuer
-- CrossConnections zu erstellen
--

-- Equipment wird um CVLAN (=InnerTag) erweitert
ALTER TABLE T_EQUIPMENT ADD CVLAN NUMBER(9);
COMMENT ON COLUMN T_EQUIPMENT.CVLAN IS 'CVLAN ID (VC or VLAN) for the port';

-- Equipment um Flag erweitern, ueber das definiert wird,
-- ob ein Port eine manuell ueberschriebene Cross-Connection
-- Konfiguration besitzt
ALTER TABLE T_EQUIPMENT ADD MANUAL_CONFIGURATION CHAR(1) DEFAULT '0';
COMMENT ON COLUMN T_EQUIPMENT.MANUAL_CONFIGURATION
  IS 'Defines, if the port has a manual configuration of the cross connection values';


CREATE TABLE T_EQ_CROSS_CONNECTION (
       ID NUMBER(10) NOT NULL
     , EQUIPMENT_ID NUMBER(10) NOT NULL
     , CC_TYPE_REF_ID NUMBER(10) NOT NULL
     , NE_OUTER_TAG NUMBER(9)
     , NE_INNER_TAG NUMBER(9)
     , CPE_OUTER_TAG NUMBER(9)
     , CPE_INNER_TAG NUMBER(9)
     , VALID_FROM DATE NOT NULL
     , VALID_TO DATE NOT NULL
     , USERW VARCHAR2(25) NOT NULL
);

ALTER TABLE T_EQ_CROSS_CONNECTION ADD CONSTRAINT PK_T_EQ_CROSS_CONNECTION PRIMARY KEY (ID);

CREATE INDEX IX_FK_XCONN_2_REF ON T_EQ_CROSS_CONNECTION (CC_TYPE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EQ_CROSS_CONNECTION ADD CONSTRAINT FK_XCONN_2_REF
  FOREIGN KEY (CC_TYPE_REF_ID) REFERENCES T_REFERENCE (ID);

CREATE INDEX IX_FK_XCONN_2_EQ ON T_EQ_CROSS_CONNECTION (EQUIPMENT_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_EQ_CROSS_CONNECTION ADD CONSTRAINT FK_XCONN_2_EQ
  FOREIGN KEY (EQUIPMENT_ID) REFERENCES T_EQUIPMENT (EQ_ID);

COMMENT ON TABLE T_EQ_CROSS_CONNECTION IS 'Table to define the cross connections for a port';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.EQUIPMENT_ID IS 'Equipment reference for which the cross connection is defined';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.CC_TYPE_REF_ID IS 'Type of the cross connection (QOSAWARE, XCONNECT, RESIDENTIAL_BRIDGE)';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.NE_OUTER_TAG IS 'Outer Tag of the net element (e.g. DSLAM)';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.NE_INNER_TAG IS 'Inner Tag of the net element (e.g. DSLAM)';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.CPE_OUTER_TAG IS 'Outer Tag of the CPE';
COMMENT ON COLUMN T_EQ_CROSS_CONNECTION.CPE_INNER_TAG IS 'Inner Tag of the CPE';
commit;


-- Referenz-Werte fuer die CrossConnection Types anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20000, 'XCONN_TYPES', 'CrossConnection', '1', 10, 'Definiert den Cross-Connection Typ <CrossConnection> (Default)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20001, 'XCONN_TYPES', 'QOS_AWARE', '1', 20, 'Definiert den Cross-Connection Typ <QOS_AWARE>');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20002, 'XCONN_TYPES', 'RES_BRIDGE', '1', 30, 'Definiert den Cross-Connection Typ <RES_BRIDGE>');
commit;


