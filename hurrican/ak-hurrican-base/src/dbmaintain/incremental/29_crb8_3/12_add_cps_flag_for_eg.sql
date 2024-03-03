ALTER TABLE T_EG ADD CPS_PROVISIONING CHAR(1);
comment on column T_EG.CPS_PROVISIONING
  is 'Flag definiert, ob das Endgeraet bzw. dessen Konfiguration an den CPS uebermittelt werden soll';


Insert into T_EG
   (ID,
   INTERNE__ID,
   NAME,
   BESCHREIBUNG,
   LS_TEXT,
   VERFUEGBAR_VON,
   VERFUEGBAR_BIS,
   CONFIGURABLE,
   CONF_PORTFORWARDING,
   CONF_S0BACKUP,
   TYPE,
   CPS_PROVISIONING)
 Values
   (5,
   5,
   'Business CPE',
   'Standard Endgeraet (mit Konfiguration) fuer FTTX Produkte',
   'Business CPE',
    TO_DATE('12/01/2010 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    '1',
    '1',
    '0',
    4,
    '1');
