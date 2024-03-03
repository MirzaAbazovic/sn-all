-- Korrektur 100/40 VOIP_IPv6
UPDATE T_TECH_LEISTUNG set NAME = '100/40_VOIP_IPv6', AUTO_EXPIRE = '0' where ID = '542' and EXTERN_LEISTUNG__NO = 10227;

-- KOMBI Leistung 100/40    inkl. 100 Mbit Testen
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
select
  545, '100/40_VOIP_IPv6 testen', 10230, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 100 MBit, 40 MBit, VOIP IPv6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '1'
from dual where not exists (select * from T_TECH_LEISTUNG where ID = 545 and EXTERN_LEISTUNG__NO = 10230) ;


-- Zusätzliches Mapping für Kombi-Leistung 100/40 VOIP IPv6 testen
-- FTTX_DSL (PROD_ID = 512)
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 25, 545, '1', 0
from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = 512 and TECH_LS_ID = 25
            and TECH_LS_DEPENDENCY = 545 );
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 74, 545, '1', 0
from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = 512 and TECH_LS_ID = 74
            and TECH_LS_DEPENDENCY = 545 );

 -- FTTX_DSL + FON(PROD_ID = 513)
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 25, 545, '1', 0
from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = 513 and TECH_LS_ID = 25
            and TECH_LS_DEPENDENCY = 545 );
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 74, 545, '1', 0
from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = 513 and TECH_LS_ID = 74
            and TECH_LS_DEPENDENCY = 545 );

-- T_PROD_2_TECH_LEISTUNG Mapping für  dynamische IPv6
-- create Prod-2-Tech-Leistung Mapping
CREATE OR REPLACE PROCEDURE create_prod_2_techls_mapping(prodId IN NUMBER, kombiLeistung IN NUMBER, leistung IN NUMBER)
IS
  BEGIN
     -- Insert  dynamische IPV6 für KOMBI-Leistung
    Insert into T_PROD_2_TECH_LEISTUNG
    (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
      select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, leistung, kombiLeistung, '1', 0
      from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = leistung and TECH_LS_DEPENDENCY = kombiLeistung);

    -- Insert  Mapping Produkt auf KOMBI-Leistung
    Insert into T_PROD_2_TECH_LEISTUNG
    (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
      select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, kombiLeistung, null, '0', 0
      from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = kombiLeistung and TECH_LS_DEPENDENCY is null);
  END;
/
-- FTTX_DSL (PROD_ID = 512)
--  dynamische IPV6 + Mapping Produkt auf Kombileistung
CALL create_prod_2_techls_mapping(512, 540, 57);
CALL create_prod_2_techls_mapping(512, 541, 57);
CALL create_prod_2_techls_mapping(512, 542, 57);
CALL create_prod_2_techls_mapping(512, 543, 57);
CALL create_prod_2_techls_mapping(512, 544, 57);
CALL create_prod_2_techls_mapping(512, 545, 57);

-- FTTX_DSL + FON(PROD_ID = 513)
--  dynamische IPV6 + Mapping Produkt auf Kombileistung
CALL create_prod_2_techls_mapping(513, 540, 57);
CALL create_prod_2_techls_mapping(513, 541, 57);
CALL create_prod_2_techls_mapping(513, 542, 57);
CALL create_prod_2_techls_mapping(513, 543, 57);
CALL create_prod_2_techls_mapping(513, 544, 57);
CALL create_prod_2_techls_mapping(513, 545, 57);

-- TAL VDSL + FON (PROD_ID = 514)
--  dynamische IPV6 + Mapping Produkt auf Kombileistung
CALL create_prod_2_techls_mapping(514, 540, 57);
CALL create_prod_2_techls_mapping(514, 541, 57);

-- TAL VDSL (PROD_ID = 515)
--  dynamische IPV6 + Mapping Produkt auf Kombileistung
CALL create_prod_2_techls_mapping(515, 540, 57);
CALL create_prod_2_techls_mapping(515, 541, 57);

DROP PROCEDURE create_prod_2_techls_mapping;

UPDATE T_PROD_2_TECH_LEISTUNG SET PRIORITY = 10  where TECH_LS_ID = 545;
