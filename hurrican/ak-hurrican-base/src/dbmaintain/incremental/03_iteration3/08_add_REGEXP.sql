-- Add hardware type name and regexps

UPDATE T_HW_BAUGRUPPEN_TYP
SET    HW_TYPE_NAME = 'EWSD_DLU_V52'
WHERE  ID IN (13);

INSERT INTO T_CFG_REG_EXP (
   ID, REF_ID, REF_NAME,
   REF_CLASS, REQUESTED_INFO,
   REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
VALUES ( 1, NULL, 'XDSL_AGB',
    'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp', 'CPS_DSLAM_PORT',
    '^[UÜ][0-9X]{2}[-/][0-9X]-(\d+-\d+)$', 1, 'Get Slot/Port of AGB XDSL Hardware');

INSERT INTO T_CFG_REG_EXP (
   ID, REF_ID, REF_NAME,
   REF_CLASS, REQUESTED_INFO,
   REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
VALUES ( 2, NULL, 'XDSL_MUC',
    'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp', 'CPS_DSLAM_PORT',
    '^\d+-\d+-(\d+-\d+)$', 1, 'Get Slot/Port of MUC XDSL Hardware');

INSERT INTO T_CFG_REG_EXP (
   ID, REF_ID, REF_NAME,
   REF_CLASS, REQUESTED_INFO,
   REGULAR_EXP, MATCH_GROUP, DESCRIPTION)
VALUES ( 3, NULL, 'EWSD_DLU',
    'de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp', 'CPS_DSLAM_PORT',
    '^\d+-\d+-(\d+-\d+)$', 1, 'Get Slot/Port of AGB DLUs');
