--
-- SQL-Script, um die Tabellen fuer eine dynamische Tree-Definition
-- zu erzeugen.
--

drop table T_TREE_DEFINITION;
CREATE TABLE T_TREE_DEFINITION (
       ID NUMBER(10) NOT  NULL
     , TREE_NAME VARCHAR2(25) NOT NULL
     , MODEL_CLASS VARCHAR2(255) NOT NULL
     , CHILD_CLASS VARCHAR2(255) NOT NULL
     , CHILD_ICON_URL VARCHAR2(255)
     , SELECT_CHILDREN_SQL VARCHAR2(4000) NOT NULL
     , USE_SQL_PARAMETER CHAR(1)
);

COMMENT ON TABLE T_TREE_DEFINITION IS
   'Tabelle definiert die Struktur beliebiger Trees inkl. der notwendigen SQL-Scripts';
COMMENT ON COLUMN T_TREE_DEFINITION.TREE_NAME IS
   'Name fuer den Tree.';
COMMENT ON COLUMN T_TREE_DEFINITION.MODEL_CLASS IS
   'Definiert eine Modell-Klasse in einem Tree. Zu dieser Modell-Klasse koennen per SQL Children (Sub-Nodes) geladen werden.';
COMMENT ON COLUMN T_TREE_DEFINITION.CHILD_CLASS IS
   'Definiert die Modell-Klasse der gelieferten Children. Bsp.: de...HVTStandort, auch wenn Select eine View aus HVTGruppe und HVTStandort darstellt.';
COMMENT ON COLUMN T_TREE_DEFINITION.SELECT_CHILDREN_SQL IS
   'SQL-Statement fuer die Ermittlung der Tree-Children. SQL liefert immer folgende Parameter in dieser Reihenfolge: MODEL_ID, DISPLAY_NAME';

ALTER TABLE T_TREE_DEFINITION ADD CONSTRAINT PK_T_TREE_DEFINITION PRIMARY KEY (ID);

grant select, insert, update, delete on T_TREE_DEFINITION to R_HURRICAN_USER;
commit;


alter table T_TREE_DEFINITION add ORDER_NO NUMBER(10);
commit;


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



CREATE TABLE T_TREE_GUI_CONFIG (
       ID NUMBER(10) NOT NULL
     , TREE_NAME VARCHAR2(25) NOT NULL
     , MODEL_CLASS VARCHAR2(255) NOT NULL
     , GUI_TYPE VARCHAR2(20) NOT NULL
     , IS_DEFAULT CHAR(1)
     , TITLE VARCHAR2(25)
     , GUI_CLASS VARCHAR2(255) NOT NULL
     , ICON_URL VARCHAR2(255)
);

COMMENT ON TABLE T_TREE_GUI_CONFIG IS
   'Konfig-Tabelle fuer Modelle eines Trees. Hierueber koennen GUI-Klassen pro Modell (und Tree) angegeben werden, die zu einem TreeNode geoeffnet werden koennen.';
COMMENT ON COLUMN T_TREE_GUI_CONFIG.GUI_TYPE
   is 'Angabe der Art der GUI-Klasse. Moegliche Werte: PANEL, DIALOG';
COMMENT ON COLUMN T_TREE_GUI_CONFIG.TREE_NAME
   is 'Optional. Name des Trees, fuer den die Konfiguration gueltig ist.';
COMMENT ON COLUMN T_TREE_GUI_CONFIG.IS_DEFAULT
   is 'Angabe, ob die GUI-Klasse die Default-Anzeigeklasse fuer das Modell innerhalb eines Trees ist.';
COMMENT ON COLUMN T_TREE_GUI_CONFIG.TITLE
   is 'Titel fuer die GUI-Klasse; entweder als Frame/Dialog-Title oder als TabbedPane-Title verwendet.';
COMMENT ON COLUMN T_TREE_GUI_CONFIG.GUI_CLASS
   is 'zu verwendende GUI-Klasse. GUI-Klasse muss vom Typ AKSimpleModelOwner sein und einen parameterlosen Konstruktor besitzen!';

ALTER TABLE T_TREE_GUI_CONFIG ADD CONSTRAINT PK_T_TREE_MODEL_CONFIG PRIMARY KEY (ID);
ALTER TABLE T_TREE_GUI_CONFIG ADD CONSTRAINT UK_T_TREE_MODEL_CONFIG UNIQUE (TREE_NAME, MODEL_CLASS, GUI_CLASS);
ALTER TABLE T_TREE_GUI_CONFIG
  add CONSTRAINT CK_T_TREEGUICFG_TYPE
     CHECK (GUI_TYPE IN ('PANEL', 'DIALOG'));

grant select, insert, update, delete on T_TREE_GUI_CONFIG to R_HURRICAN_USER;
commit;

-- Beispiel-Definition
insert into T_TREE_GUI_CONFIG (ID, TREE_NAME, MODEL_CLASS, GUI_TYPE, IS_DEFAULT, TITLE, GUI_CLASS, ICON_URL)
  values (1, 'HW_TREE', 'de.augustakom.hurrican.model.cc.HVTStandort', 'PANEL', '1', 'HVT-Standort',
  'de.augustakom.hurrican.gui.base.tree.TestPanel', 'de/augustakom/hurrican/gui/images/hvt.gif');


