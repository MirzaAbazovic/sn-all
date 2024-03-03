-- Script erstellt die Konfigurationen fuer Produkt/Standorttyp

-- Standardprodukte / KVZ
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11001,
    1,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID<470 and tmp.PRODUKTGRUPPE_ID<>22;

-- Standardprodukte / Gewofag
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11009,
    2,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1'
    and tmp.PROD_ID>300               -- nur Maxi/Premium Produkt, keine AK Alt-Produkte
    and tmp.PROD_ID<470
    and tmp.PRODUKTGRUPPE_ID<>22;

-- Phone-Produkte / ISIS
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11010,
    3,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1'
    and tmp.PROD_ID in (322,323,336,337,338,340);

-- Standardprodukte / HVT
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11000,
    4,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID<470 and tmp.PRODUKTGRUPPE_ID<>22;

-- ConnectDSL-Produkte / NK
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11004,
    5,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID in (460,461);


-- FTTX-Produkte / FTTH
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11011,
    1,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID>=500;

-- FTTX-Produkte / FTTB
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11002,
    2,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID>=500 and tmp.PROD_ID<520;

-- FTTX-Produkte / FTTC
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11013,
    3,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID in (512,513);

-- TV-Produkte / TV
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW)
  select
    S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal,
    tmp.PROD_ID,
    11007,
    4,
    'IMPORT'
  from T_PRODUKT tmp where tmp.LTGNR_ANLEGEN='1' and tmp.PROD_ID in (500);
