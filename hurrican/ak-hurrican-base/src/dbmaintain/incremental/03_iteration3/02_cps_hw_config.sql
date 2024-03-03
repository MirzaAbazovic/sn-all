--
-- SQL Script definiert die RegExp fuer die Datenermittlung EQN
--


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
