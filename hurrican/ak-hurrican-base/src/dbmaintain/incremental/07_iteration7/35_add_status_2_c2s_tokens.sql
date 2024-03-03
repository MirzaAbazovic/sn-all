--
-- Fuege ein Status Feld in die C2S-Token-Tabelle ein.
--

ALTER TABLE T_IPSEC_C2S_TOKEN
 ADD (STATUS_REF_ID  NUMBER(10));


COMMENT ON COLUMN T_IPSEC_C2S_TOKEN.BATCH IS 'Batch Nummer der eingespielten Tokens';

COMMENT ON COLUMN T_IPSEC_C2S_TOKEN.STATUS_REF_ID IS 'Status des Tokens';

-- Referenz-Daten fuer Token-Status eintragen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (21000, 'IPSEC_TOKEN_STATUS', 'aktiviert', '1', 10, 'aktiviert');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (21001, 'IPSEC_TOKEN_STATUS', 'im Austausch', '1', 20, 'im Austausch');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (21002, 'IPSEC_TOKEN_STATUS', 'abgelaufen', '1', 30, 'abgelaufen');

