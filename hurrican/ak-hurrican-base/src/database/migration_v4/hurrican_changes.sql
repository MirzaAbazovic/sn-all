--
-- SQL-Script, um notwendige Aenderungen an Hurrican-Tabellen
-- fuer Taifun V4.3 vorzunehmen.
--

alter table T_ENDSTELLE add STREET_SECTION_NO NUMBER(10);

alter table T_PRODUKT add CREATE_AP_ADDRESS CHAR(1);
update T_PRODUKT set CREATE_AP_ADDRESS=0 where ENDSTELLEN_TYP in (1,2);
update T_PRODUKT set CREATE_AP_ADDRESS=1 where PRODUKTGRUPPE_ID=2 AND ENDSTELLEN_TYP in (1,2);


commit;
