-- KOMBI Leistung 25/5
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
    DISPO, EWSD, SDH, IPS, SCT,
    SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
    AUTO_EXPIRE)
 select
   540, '25/5_VOIP_IPv6', 10225, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 25 MBit, 5 MBit, VOIP IPv6',
    '0', '0', '0', '0', '0',
    '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0','0'
   from dual where not exists (select * from T_TECH_LEISTUNG where ID = 540 and EXTERN_LEISTUNG__NO = 10225) ;

-- KOMBI Leistung 50/10
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
  select
  541, '50/10_VOIP_IPv6', 10226, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 50 MBit, 10 MBit, VOIP IPv6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '0'
  from dual where not exists (select * from T_TECH_LEISTUNG where ID = 541 and EXTERN_LEISTUNG__NO = 10226) ;

-- KOMBI Leistung 100/40    inkl. 100 Mbit Testen
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
  select
  542, '100/40_VOIP_IPv6', 10227, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 100 MBit, 40 MBit, VOIP IPv6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '1'
  from dual where not exists (select * from T_TECH_LEISTUNG where ID = 542 and EXTERN_LEISTUNG__NO = 10227) ;

-- KOMBI 150/50
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
  select
  543, '150/50_VOIP_IPv6', 10228, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 150 MBit, 50 MBit, VOIP IPv6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '0'
  from dual where not exists (select * from T_TECH_LEISTUNG where ID = 543 and EXTERN_LEISTUNG__NO = 10228);

-- KOMBI 300/50
Insert into T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
  select
  544, '300/50_VOIP_IPv6', 10229, 'KOMBI', '.', 'Kombi-Leistung, fuehrt zu 300 MBit, 50 MBit, VOIP IPv6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0','0'
  from dual where not exists (select * from T_TECH_LEISTUNG where ID = 544 and EXTERN_LEISTUNG__NO = 10229) ;

-- create Prod-2-Tech-Leistung Mapping
CREATE OR REPLACE PROCEDURE create_prod_2_techls_mapping(prodId IN NUMBER, techId IN NUMBER, extLeistNo in NUMBER, down IN NUMBER, up IN NUMBER)
IS
  BEGIN
    FOR techLsID IN (SELECT *
                   FROM T_TECH_LEISTUNG
                   WHERE ID = techId
                         AND EXTERN_LEISTUNG__NO = extLeistNo
    )
    LOOP
      Insert into T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
        select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, down, techLsID.ID, '1', 0
        from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = down and TECH_LS_DEPENDENCY = techLsID.ID);

      Insert into T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
        select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, down, null, '0', 0
        from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = down and TECH_LS_DEPENDENCY is null);

      INSERT INTO T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
        select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, up, down, '1', 0
        from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = up and TECH_LS_DEPENDENCY = down);

      INSERT INTO T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
        select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prodId, up, techLsID.ID, '1', 0
        from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where PROD_ID = prodId and TECH_LS_ID = up and TECH_LS_DEPENDENCY = techLsID.ID);
    END LOOP;
  END;
/
-- FFTX_DSL (PROD_ID = 512)
--  Kombi-Leistung 25/5 VOIP IPv6
CALL create_prod_2_techls_mapping(512, 540, 10225, 23, 27);
--  Kombi-Leistung 50/10 VOIP IPv6
CALL create_prod_2_techls_mapping(512, 541, 10226, 24, 28);
--  Kombi-Leistung 100/40 VOIP IPv6
CALL create_prod_2_techls_mapping(512, 542, 10227, 25, 74);
--  Kombi-Leistung 150/50 VOIP IPv6
CALL create_prod_2_techls_mapping(512, 543, 10228, 72, 53);
--  Kombi-Leistung 300/50 VOIP IPv6
CALL create_prod_2_techls_mapping(512, 544, 10229, 66, 53);

-- FFTX_DSL + FON(PROD_ID = 513)
--  Kombi-Leistung 25/5 VOIP IPv6
CALL create_prod_2_techls_mapping(513, 540, 10225, 23, 27);
--  Kombi-Leistung 50/10 VOIP IPv6
CALL create_prod_2_techls_mapping(513, 541, 10226, 24, 28);
--  Kombi-Leistung 100/40 VOIP IPv6
CALL create_prod_2_techls_mapping(513, 542, 10227, 25, 74);
--  Kombi-Leistung 150/50 VOIP IPv6
CALL create_prod_2_techls_mapping(513, 543, 10228, 72, 53);
--  Kombi-Leistung 300/50 VOIP IPv6
CALL create_prod_2_techls_mapping(513, 544, 10229, 66, 53);


-- TAL VDSL + FON (PROD_ID = 514)
--  Kombi-Leistung 25/5 VOIP IPv6
CALL create_prod_2_techls_mapping(514, 540, 10225, 23, 27);
--  Kombi-Leistung 50/10 VOIP IPv6
CALL create_prod_2_techls_mapping(514, 541, 10226, 24, 28);

-- TAL VDSL (PROD_ID = 515)
--  Kombi-Leistung 25/5 VOIP IPv6
CALL create_prod_2_techls_mapping(515, 540, 10225, 23, 27);
--  Kombi-Leistung 50/10 VOIP IPv6
CALL create_prod_2_techls_mapping(515, 541, 10226, 24, 28);

DROP PROCEDURE create_prod_2_techls_mapping;









