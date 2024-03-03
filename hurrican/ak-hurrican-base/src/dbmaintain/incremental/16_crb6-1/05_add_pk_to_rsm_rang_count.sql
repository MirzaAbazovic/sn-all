delete from T_RSM_RANG_COUNT;
delete from T_RSM_PORT_USAGE;

alter table T_RSM_RANG_COUNT add ID NUMBER(18) NOT NULL;
ALTER TABLE T_RSM_RANG_COUNT ADD CONSTRAINT PK_T_RSM_RANG_COUNT PRIMARY KEY (ID);
alter table T_RSM_RANG_COUNT add VERSION NUMBER(18) DEFAULT 0 NOT NULL;

create SEQUENCE S_T_RSM_RANG_COUNT_0 start with 1;
grant select on S_T_RSM_RANG_COUNT_0 to public;

alter table T_RSM_PORT_USAGE modify ID NUMBER(18);



CREATE INDEX IX_T_RANGIERUNG_LTGGESLFD ON T_RANGIERUNG
(LEITUNG_LFD_NR)
LOGGING
TABLESPACE T_HURRICAN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

