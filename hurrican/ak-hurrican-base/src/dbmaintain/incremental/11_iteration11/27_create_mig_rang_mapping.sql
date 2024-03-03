CREATE TABLE MIG_RANG_MAPPING
(
  VERBINDUNGSBEZEICHNUNG  VARCHAR2(100 BYTE)    NOT NULL,
  RANGIER_ID              NUMBER(20)         NOT NULL,
  TPU_ID_IN               NUMBER(20),
  TPU_ID_OUT              NUMBER(20),
  ENDSTELLE               VARCHAR2(20 BYTE)
)
LOGGING
NOCOMPRESS
NOCACHE
NOPARALLEL
NOMONITORING;