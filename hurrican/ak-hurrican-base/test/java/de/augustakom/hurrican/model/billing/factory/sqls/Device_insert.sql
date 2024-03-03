    INSERT INTO DEVICE
       (DEV_NO, SRNUM, DEVTYPE_ID, STORAGE_ID, USERI, DATEI, USERW, DATEW)
    VALUES (
       ${devNo:-NULL},
       '${serialNumber:-S123456789}',
       '${deviceType:-AVM Fritz!Box FON WLAN 7360}',
       'DEFAULT',
       'TEST',
       sysdate,
       'TEST',
       sysdate)