CREATE TABLE T_MWF_AUFNEHMENDER_PROVIDER
(
  ID                            NUMBER(19)      NOT NULL,
  VERSION                       NUMBER(19)      NOT NULL,
  PROVIDERNAME_AUFNEHMEND       VARCHAR2(160)   NOT NULL,
  UEBERNAHME_DATUM_GEPLANT      DATE,
  ANTWORT_FRIST                 DATE,
  UEBERNAHME_DATUM_VERBINDLICH  DATE
)
LOGGING
NOCOMPRESS
NOCACHE
NOPARALLEL
NOMONITORING;


ALTER TABLE T_MWF_AUFNEHMENDER_PROVIDER ADD (
  CONSTRAINT T_MWF_AUFNEHMENDER_PROVIDER_PK
  PRIMARY KEY
  (ID));

CREATE SEQUENCE S_T_MWF_AUFNEHMENDER_PROVIDER
START WITH 0
INCREMENT BY 1
MINVALUE 0
NOCACHE
NOCYCLE
NOORDER;

ALTER TABLE T_MWF_MELDUNG ADD (AUFNEHMENDER_PROVIDER_ID  NUMBER(19));
