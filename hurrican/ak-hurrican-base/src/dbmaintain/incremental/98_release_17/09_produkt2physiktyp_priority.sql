alter table T_PRODUKT_2_PHYSIKTYP add PRIORITY NUMBER(19) DEFAULT NULL;

-- FTTB_POTS fuer Produkt FTTX Telefon priorisieren!
-- prod_id: 511
-- physiktypen: 801 (FTTB_POTS), 807 (FTTH_POTS)
update T_PRODUKT_2_PHYSIKTYP set PRIORITY=100 where PROD_ID=511 and PHYSIKTYP in (801, 807);

