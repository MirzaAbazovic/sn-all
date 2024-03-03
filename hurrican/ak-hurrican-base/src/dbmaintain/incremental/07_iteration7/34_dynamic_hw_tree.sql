-- initialisiert den HW-Tree

-- Definitionen fuer HW_TREE
insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS, CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (1, 'HW_TREE', 'ROOT', 'de.augustakom.hurrican.model.cc.HVTStandort', 'de/augustakom/hurrican/gui/images/hvt.gif',
  'select hs.HVT_ID_STANDORT as MODEL_ID, ref.STR_VALUE || '': '' || hg.ORTSTEIL || '' ('' || hg.ONKZ || '' - ''  || hs.ASB || '')'' as DISPLAY_NAME
   from T_HVT_GRUPPE hg
     inner join T_HVT_STANDORT hs on hs.HVT_GRUPPE_ID=hg.HVT_GRUPPE_ID
     inner join T_REFERENCE ref on hs.STANDORT_TYP_REF_ID=ref.ID
     where hs.GUELTIG_BIS>SYSDATE
     order by ref.ID, hs.HVT_ID_STANDORT',
  '0', 10);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (4, 'HW_TREE', 'de.augustakom.hurrican.model.cc.HVTStandort',
  'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hw.ID as MODEL_ID, ''RACK: '' || hw.ANLAGENBEZ || '' ('' || hw.GERAETEBEZ || '')'' as DISPLAY_NAME
  from T_HW_RACK hw where hw.HVT_ID_STANDORT=? order by hw.RACK_TYP',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (5, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de.augustakom.hurrican.model.cc.hardware.HWDslam',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hwd.RACK_ID as MODEL_ID, ''DSLAM: '' || decode(hwr.MANAGEMENTBEZ, NULL, hwd.DHCP82_NAME, hwr.MANAGEMENTBEZ) as DISPLAY_NAME
   from T_HW_RACK_DSLAM hwd
     inner join T_HW_RACK hwr on hwd.RACK_ID=hwr.ID where hwd.RACK_ID=?',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (6, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de.augustakom.hurrican.model.cc.hardware.HWLtg',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hwd.RACK_ID as MODEL_ID, ''LTG: '' || hwd.LTG_NUMBER as DISPLAY_NAME
   from T_HW_RACK_LTG hwd where hwd.RACK_ID=?',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (7, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de.augustakom.hurrican.model.cc.hardware.HWMdu',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hwd.RACK_ID as MODEL_ID, ''MDU: '' || hwd.SERIAL_NO as DISPLAY_NAME
   from T_HW_RACK_MDU hwd where hwd.RACK_ID=?',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (8, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de.augustakom.hurrican.model.cc.hardware.HWDlu',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hwd.RACK_ID as MODEL_ID, ''DLU: '' || hwd.DLU_NUMBER as DISPLAY_NAME
   from T_HW_RACK_DLU hwd where hwd.RACK_ID=?',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (9, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWRack',
  'de.augustakom.hurrican.model.cc.hardware.HWOlt',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select hwd.RACK_ID as MODEL_ID, ''OLT: '' || hwd.SERIAL_NO as DISPLAY_NAME
   from T_HW_RACK_OLT hwd where hwd.RACK_ID=?',
  '1', 20);


insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (10, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWDslam',
  'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select bg.ID as MODEL_ID, ''BG: '' || bgt.NAME || '' ('' || bg.MOD_NUMBER || '')'' as DISPLAY_NAME
   from T_HW_BAUGRUPPE bg, T_HW_BAUGRUPPEN_TYP bgt where bg.RACK_ID=? and bg.HW_BG_TYP_ID=bgt.ID',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (11, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWLtg',
  'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select bg.ID as MODEL_ID, ''BG: '' || bgt.NAME || '' ('' || bg.MOD_NUMBER || '')'' as DISPLAY_NAME
   from T_HW_BAUGRUPPE bg, T_HW_BAUGRUPPEN_TYP bgt where bg.RACK_ID=? and bg.HW_BG_TYP_ID=bgt.ID',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (12, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWDlu',
  'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select bg.ID as MODEL_ID, ''BG: '' || bgt.NAME || '' ('' || bg.MOD_NUMBER || '')'' as DISPLAY_NAME
   from T_HW_BAUGRUPPE bg, T_HW_BAUGRUPPEN_TYP bgt where bg.RACK_ID=? and bg.HW_BG_TYP_ID=bgt.ID',
  '1', 20);

insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (13, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWMdu',
  'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe',
  'de/augustakom/hurrican/gui/images/dslam.gif',
  'select bg.ID as MODEL_ID, ''BG: '' || bgt.NAME || '' ('' || bg.MOD_NUMBER || '')'' as DISPLAY_NAME
   from T_HW_BAUGRUPPE bg, T_HW_BAUGRUPPEN_TYP bgt where bg.RACK_ID=? and bg.HW_BG_TYP_ID=bgt.ID',
  '1', 20);

-- Beispiel-Definition
insert into T_TREE_GUI_CONFIG (ID, TREE_NAME, MODEL_CLASS, GUI_TYPE, IS_DEFAULT, TITLE, GUI_CLASS, ICON_URL)
  values (1, 'HW_TREE', 'de.augustakom.hurrican.model.cc.HVTStandort', 'PANEL', '1', 'HVT-Standort',
  'de.augustakom.hurrican.gui.base.tree.TestPanel', 'de/augustakom/hurrican/gui/images/hvt.gif');
