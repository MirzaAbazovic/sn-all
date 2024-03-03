INSERT INTO AUFTRAGPOS (
       ITEM_NO,ITEM__NO,ORDER__NO,CREATE_ORDER_NO,TERMINATE_ORDER_NO,
       IS_PLANNED,SERVICE_ELEM__NO,CHARGE_FROM,CHARGE_TO,CHARGED_UNTIL,
       QUANTITY,PRICE,LIST_PRICE,CURRENCY_ID,USERW,DATEW,DEV_NO,TIMESTAMP,
       PURCHASE_ORDER_NO,REPLACING__NO, DEVICE_VALID_FROM, DEVICE_VALID_TO)
    VALUES (
       ${itemNo:-NULL},
       ${itemNoOrig:-NULL},
       ${auftragNoOrig:-NULL},
       ${createAuftragNo:-NULL},
       ${terminateAuftragNo:-NULL},
      '${isPlanned:-0}',
      ${leistungNoOrig:-NULL},
      TO_DATE('${chargeFrom:-01/01/2014 00:00:00}', 'MM/DD/YYYY HH24:MI:SS'),
      null,
      null,
      1,
      25.13,
      25.13,
      'EUR',
      'TEST',
      sysdate,
      ${deviceNo:-NULL},
      ${timestamp:-NULL},
      null,
      0,
      TO_DATE('01/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
      TO_DATE('01/01/2099 00:00:00', 'MM/DD/YYYY HH24:MI:SS')
    )
