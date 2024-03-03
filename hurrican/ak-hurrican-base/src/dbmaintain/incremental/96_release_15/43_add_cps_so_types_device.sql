UPDATE T_REFERENCE set STR_VALUE='createDevice', DESCRIPTION='Wert definiert den ServiceOrder-Typ, um ein Device zu initialisieren'
    where id = 14010;

INSERT INTO T_REFERENCE (ID,TYPE,STR_VALUE,GUI_VISIBLE,DESCRIPTION,VERSION,CHANGED_AT)
    VALUES (14011,'CPS_SERVICE_ORDER_TYPE','modifyDevice','0',
    'Wert definiert den ServiceOrder-Typ, um ein Device zu aendern',0,TO_DATE('05/16/2014 14:00:00', 'MM/DD/YYYY HH24:MI:SS'));

INSERT INTO T_REFERENCE (ID,TYPE,STR_VALUE,GUI_VISIBLE,DESCRIPTION,VERSION,CHANGED_AT)
    VALUES (14012,'CPS_SERVICE_ORDER_TYPE','deleteDevice','0',
    'Wert definiert den ServiceOrder-Typ, um ein Device zu loeschen',0,TO_DATE('05/16/2014 14:00:00', 'MM/DD/YYYY HH24:MI:SS'));

