-- Standorttyp NK (11004) für alle Produkte freischalten, die auch per HVT realisierbar sind.
-- Priorität: niedrig setzen
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
    select S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, SQ.PROD_ID, 11004, SQ.PRIORITY, 'IMPORT'
        from (select PROD_ID, MAX(PRIORITY) + 1 as PRIORITY from T_PRODUKT_2_TECH_LOCATION_TYPE P2TL
        where P2TL.PROD_ID in (select PROD_ID from T_PRODUKT_2_TECH_LOCATION_TYPE P2TL where P2TL.TECH_LOCATION_TYPE_REF_ID=11000)
        and P2TL.PROD_ID not in (select PROD_ID from T_PRODUKT_2_TECH_LOCATION_TYPE P2TL where P2TL.TECH_LOCATION_TYPE_REF_ID=11004)
        group by PROD_ID) SQ;