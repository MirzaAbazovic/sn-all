CREATE TABLE T_VORABSTIMMUNG_ABGEBEND (
	ID 						NUMBER(19) 	NOT NULL,
    ES_TYP 					CHAR(1) 	NOT NULL, 
    AUFTRAG_ID 				NUMBER(19) 	NOT NULL,
	CARRIER_ID 	NUMBER(19),
	ABGESTIMMTER_PV_TERMIN 	DATE,
	BESCHREIBUNG_ABLEHNUNG 	VARCHAR2(50 BYTE)
    );
    
ALTER TABLE T_VORABSTIMMUNG_ABGEBEND ADD CONSTRAINT FK_VORABST_ABGD_2_AUFTRAG FOREIGN KEY (AUFTRAG_ID) REFERENCES T_AUFTRAG (ID);
ALTER TABLE T_VORABSTIMMUNG_ABGEBEND ADD (
  CONSTRAINT T_VORABST_ABGD_ES_TYP
  CHECK (ES_TYP IN ('A', 'B')));
   
ALTER TABLE T_VORABSTIMMUNG_ABGEBEND ADD CONSTRAINT FK_VORABST_ABGD_2_CARRIER FOREIGN KEY (CARRIER_ID) REFERENCES T_CARRIER (ID);
ALTER TABLE T_VORABSTIMMUNG_ABGEBEND ADD CONSTRAINT VORABST_ABGD_UNIQUE UNIQUE (ES_TYP, AUFTRAG_ID);

GRANT SELECT ON T_VORABSTIMMUNG_ABGEBEND TO R_HURRICAN_READ_ONLY;

GRANT INSERT, SELECT, UPDATE ON T_VORABSTIMMUNG_ABGEBEND TO R_HURRICAN_USER;

CREATE SEQUENCE S_T_VORABST_ABGD_0;

GRANT SELECT ON S_T_VORABST_ABGD_0 TO PUBLIC;
