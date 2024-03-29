CREATE TABLE T_ANBIETERWECHSELCONFIG
(
  ID                            NUMBER(19)      NOT NULL,
  VERSION                       NUMBER(19)      NOT NULL,
  CARRIERABGEBEND               VARCHAR2(10)    NOT NULL,
  ALTPRODUKT                    VARCHAR2(10)    NOT NULL,
  NEUPRODUKT                    VARCHAR2(10)    NOT NULL,
  GESCHAEFTSFALLTYP             VARCHAR2(40)    NOT NULL,
  primary key(id)
)
LOGGING
NOCOMPRESS
NOCACHE
NOPARALLEL
NOMONITORING;

CREATE SEQUENCE S_T_ANBIETERWECHSELCONFIG_0
START WITH 0
INCREMENT BY 1
MINVALUE 0
NOCACHE
NOCYCLE
NOORDER;