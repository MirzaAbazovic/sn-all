-- add regular expressions needed for Cross Connection calculation for MUC

ALTER TABLE T_CFG_REG_EXP
 DROP CONSTRAINT UK_T_CFG_REG_EXP;

DROP INDEX UK_T_CFG_REG_EXP;

ALTER TABLE T_CFG_REG_EXP
 ADD CONSTRAINT UK_T_CFG_REG_EXP
 UNIQUE (REF_ID, REF_NAME, REF_CLASS, REQUESTED_INFO)
    DEFERRABLE INITIALLY IMMEDIATE;


INSERT INTO T_CFG_REG_EXP (
   ID, REF_ID, REF_NAME,
   REF_CLASS, REQUESTED_INFO,
   REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
VALUES ( 20, NULL, 'XDSL_MUC',
    'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp', 'PORT_NUM_FROM_HW_EQN',
    '^\d+-\d+-\d+-(\d+)$', 1, 'Get Port number of MUC XDSL Hardware');

INSERT INTO T_CFG_REG_EXP (
   ID, REF_ID, REF_NAME,
   REF_CLASS, REQUESTED_INFO,
   REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
VALUES ( 21, NULL, 'XDSL_MUC',
    'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp', 'CARD_NUM_FROM_HW_EQN',
    '^\d+-\d+-(\d+)-\d+$', 1, 'Get Baugruppen number of MUC XDSL Hardware');