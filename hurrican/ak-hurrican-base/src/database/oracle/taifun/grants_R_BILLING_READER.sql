--
-- GRANTs fuer die DB-Rolle 'R_BILLING_READER' fuer die Taifun-DB.
-- Rolle wurde fuer den Zugriff auf die monatl. Rechnungstabellen 
-- BIE_BILL_xxx und BIE_BILL_ITEM_xxx angelegt. Grants muessen monatlich 
-- nach Erzeugen der Rechnungstabelle ausgefuehrt werden.
-- Z.B.
-- GRANT SELECT ON BIE_BILL_200809 TO "R_BILLING_READER";
-- GRANT SELECT ON BIE_BILL_ITEM_200809 TO "R_BILLING_READER";
--

-- Read-only Role anlegen
CREATE ROLE "R_BILLING_READER" NOT IDENTIFIED;

GRANT select, insert, update ON ACCOUNT TO "R_BILLING_READER"; 

commit;


