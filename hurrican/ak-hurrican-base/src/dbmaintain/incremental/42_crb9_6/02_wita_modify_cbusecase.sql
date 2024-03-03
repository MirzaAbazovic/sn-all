alter table T_CB_USECASE modify (EXM_TBV_ID NULL);
alter table T_CB_USECASE add WITA_GESCHAEFTSFALL VARCHAR2(50);

Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (100, 8000, 'Bereitstellung', '0', 0);
Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (101, 8001, 'Kündigung', '0', 0);
Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (102, 8002, 'Storno', '0', 0);
Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (103, 8003, 'Leistungsänderung (Produktvariante)', '0', 0);
Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (104, 8003, 'Leistungsmermaländerung (Ütv)', '0', 0);
