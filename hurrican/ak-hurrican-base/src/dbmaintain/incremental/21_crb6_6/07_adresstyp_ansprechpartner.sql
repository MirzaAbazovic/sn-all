-- merge, da die Einträge evtl. schon existieren aber unvollständig sind

MERGE INTO T_REFERENCE A USING
 (SELECT 205 as ID, 'ADDRESS_TYPE' as TYPE, 'Hotline Service' as STR_VALUE, NULL as INT_VALUE, NULL as FLOAT_VALUE, NULL as UNIT_ID, '1' as GUI_VISIBLE, 130 as ORDER_NO, NULL as DESCRIPTION, 0 as VERSION FROM DUAL) B
ON (A.ID = B.ID)
WHEN NOT MATCHED THEN
INSERT (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
VALUES (B.ID, B.TYPE, B.STR_VALUE, B.INT_VALUE, B.FLOAT_VALUE, B.UNIT_ID, B.GUI_VISIBLE, B.ORDER_NO, B.DESCRIPTION, B.VERSION)
WHEN MATCHED THEN
UPDATE SET A.STR_VALUE = B.STR_VALUE, A.DESCRIPTION = B.DESCRIPTION;

MERGE INTO T_REFERENCE A USING
 (SELECT 206 as ID, 'ADDRESS_TYPE' as TYPE, 'Hotline geplante Arbeiten' as STR_VALUE, NULL as INT_VALUE, NULL as FLOAT_VALUE, NULL as UNIT_ID, '1' as GUI_VISIBLE, 130 as ORDER_NO, NULL as DESCRIPTION, 0 as VERSION FROM DUAL) B
ON (A.ID = B.ID)
WHEN NOT MATCHED THEN
INSERT (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
VALUES (B.ID, B.TYPE, B.STR_VALUE, B.INT_VALUE, B.FLOAT_VALUE, B.UNIT_ID, B.GUI_VISIBLE, B.ORDER_NO, B.DESCRIPTION, B.VERSION)
WHEN MATCHED THEN
UPDATE SET A.STR_VALUE = B.STR_VALUE, A.DESCRIPTION = B.DESCRIPTION;

MERGE INTO T_REFERENCE A USING
 (SELECT 16006 as ID, 'ANSPRECHPARTNER' as TYPE, 'Hotline Service' as STR_VALUE, 205 as INT_VALUE, NULL as FLOAT_VALUE, NULL as UNIT_ID, '1' as GUI_VISIBLE, 70 as ORDER_NO, 'Ansprechpartner: Hotline Service' as DESCRIPTION, 0 as VERSION FROM DUAL) B
ON (A.ID = B.ID)
WHEN NOT MATCHED THEN
INSERT (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
VALUES (B.ID, B.TYPE, B.STR_VALUE, B.INT_VALUE, B.FLOAT_VALUE, B.UNIT_ID, B.GUI_VISIBLE, B.ORDER_NO, B.DESCRIPTION, B.VERSION)
WHEN MATCHED THEN
UPDATE SET A.STR_VALUE = B.STR_VALUE, A.INT_VALUE = B.INT_VALUE, A.DESCRIPTION = B.DESCRIPTION;

MERGE INTO T_REFERENCE A USING
 (SELECT 16010 as ID, 'ANSPRECHPARTNER' as TYPE, 'Hotline geplante Arbeiten' as STR_VALUE, 206 as INT_VALUE, NULL as FLOAT_VALUE, NULL as UNIT_ID, '1' as GUI_VISIBLE, 70 as ORDER_NO, 'Ansprechpartner: Hotline geplante Arbeiten' as DESCRIPTION, 0 as VERSION FROM DUAL) B
ON (A.ID = B.ID)
WHEN NOT MATCHED THEN
INSERT (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
VALUES (B.ID, B.TYPE, B.STR_VALUE, B.INT_VALUE, B.FLOAT_VALUE, B.UNIT_ID, B.GUI_VISIBLE, B.ORDER_NO, B.DESCRIPTION, B.VERSION)
WHEN MATCHED THEN
UPDATE SET A.STR_VALUE = B.STR_VALUE, A.INT_VALUE = B.INT_VALUE, A.DESCRIPTION = B.DESCRIPTION;
