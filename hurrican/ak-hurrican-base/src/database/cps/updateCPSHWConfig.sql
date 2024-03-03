--
-- Script, um eine allgemeine Config-Tabelle fuer RegularExpressions
-- zu generieren.
--

CREATE TABLE T_CFG_REG_EXP (
       ID NUMBER(10) NOT NULL
     , REF_ID NUMBER(10) NOT NULL
     , REF_CLASS VARCHAR2(255) NOT NULL
     , REQUESTED_INFO VARCHAR2(20)
     , REGULAR_EXP VARCHAR2(255) NOT NULL
     , DESCRIPTION VARCHAR2(255)
);

ALTER TABLE T_CFG_REG_EXP
  ADD CONSTRAINT PK_T_CFG_REG_EXP
      PRIMARY KEY (ID);
      
-- UniqueKey: REF_ID, REF_CLASS, REQUESTED_INFO
ALTER TABLE T_CFG_REG_EXP ADD CONSTRAINT UK_T_CFG_REG_EXP UNIQUE (REF_ID, REF_CLASS, REQUESTED_INFO);
      
GRANT SELECT ON T_CFG_REG_EXP TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON T_CFG_REG_EXP TO R_HURRICAN_USER;
commit;


insert into T_CFG_REG_EXP (ID, REF_ID, REF_CLASS, REQUESTED_INFO, REGULAR_EXP, DESCRIPTION) 
  values (1, 14, 'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp',
  'CPS_DSLAM_PORT', '[U|Ü]\d{2}[-|/]\d{1}-', 
  'String.reqlace(regExp, "") liefert die zu verwendende DSLAM Port-Information');
  
insert into T_CFG_REG_EXP (ID, REF_ID, REF_CLASS, REQUESTED_INFO, REGULAR_EXP, DESCRIPTION) 
  values (2, 16, 'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp',
  'CPS_DSLAM_PORT', '[U|Ü]\d{2}[-|/]\d{1}-', 
  'String.reqlace(regExp, "") liefert die zu verwendende DSLAM Port-Information');
  
insert into T_CFG_REG_EXP (ID, REF_ID, REF_CLASS, REQUESTED_INFO, REGULAR_EXP, DESCRIPTION) 
  values (3, 17, 'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp',
  'CPS_DSLAM_PORT', '[U|Ü]\d{2}[-|/]\d{1}-', 
  'String.reqlace(regExp, "") liefert die zu verwendende DSLAM Port-Information');

commit;
