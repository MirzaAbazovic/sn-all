create table T_FFM_FEEDBACK_MATERIAL(
  id                    NUMBER(19, 0) NOT NULL,
  VERSION NUMBER(19, 0) DEFAULT 0 NOT NULL,
  WORKFORCE_ORDER_ID    VARCHAR2(100) NOT NULL,
  MATERIAL_ID           VARCHAR2(100) NOT NULL,
  SERIAL_NUMBER         VARCHAR2(100) NOT NULL,
  SUMMARY               VARCHAR2(255),
  DESCRIPTION           VARCHAR2(255) NOT NULL,
  QUANTITY              NUMBER(19, 0) NOT NULL,
  PROCESSED             CHAR(1) DEFAULT '0' NOT NULL,
  PRIMARY KEY (id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON T_FFM_FEEDBACK_MATERIAL TO R_HURRICAN_USER;
GRANT SELECT ON T_FFM_FEEDBACK_MATERIAL TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_FFM_FEEDBACK_MATERIAL_0;
GRANT SELECT ON S_T_FFM_FEEDBACK_MATERIAL_0 TO PUBLIC;
