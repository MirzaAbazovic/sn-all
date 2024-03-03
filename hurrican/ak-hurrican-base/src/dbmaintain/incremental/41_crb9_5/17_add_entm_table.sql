CREATE TABLE T_MWF_ENTM
(
   ID                    NUMBER (19, 0) NOT NULL,
   KUNDEN_NUMMER         VARCHAR2 (10) NOT NULL,
   EXT_AUFTRAGS_NR       VARCHAR2 (20) NOT NULL,
   VERTRAGS_NUMMER       VARCHAR2 (10),
   ENTGELTTERMIN         DATE,
   VERSION               NUMBER (19, 0) DEFAULT 0 NOT NULL,
   PRIMARY KEY (ID)
);

GRANT SELECT, INSERT, UPDATE ON T_MWF_ENTM TO R_HURRICAN_USER;
GRANT SELECT ON T_MWF_ENTM TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_MWF_ENTM_0
   START WITH 1;

GRANT SELECT ON S_T_MWF_ENTM_0 TO PUBLIC;
