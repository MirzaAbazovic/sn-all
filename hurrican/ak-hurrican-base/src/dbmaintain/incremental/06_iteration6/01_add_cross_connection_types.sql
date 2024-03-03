-- add lhm cc types

Insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, 
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
 Values
   (20010, 'XCONN_TYPES', 'QSC_HSI', NULL, NULL, 
    NULL, '1', 80, 'ATM-Handover-PVC to QSC');
    
Insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE, 
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
 Values
   (20011, 'XCONN_TYPES', 'QSC_MGMT', NULL, NULL, 
    NULL, '1', 90, 'ATM-Handover-PVC to QSC - Management');
