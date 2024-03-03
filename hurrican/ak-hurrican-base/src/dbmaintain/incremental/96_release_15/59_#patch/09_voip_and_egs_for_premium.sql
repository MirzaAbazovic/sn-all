CREATE OR REPLACE PROCEDURE voip_for_prem_glasf(buendel_ls_id IN NUMBER, voip_ls_id IN NUMBER, produkt_id IN NUMBER)
IS
  row_exists NUMBER;
  BEGIN
    -- VOIP
    SELECT
      count(*)
    INTO row_exists
    FROM T_PROD_2_TECH_LEISTUNG
    WHERE TECH_LS_ID = voip_ls_id AND TECH_LS_DEPENDENCY = buendel_ls_id and prod_id = produkt_id;

    IF (row_exists = 0)
    THEN
      Insert into T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
      Values
        (S_T_PROD_2_TECH_LEISTUNG_0.nextval, produkt_id, voip_ls_id, buendel_ls_id, '1', 0)
      ;
    END IF;

    -- EG PORTS
    SELECT
      count(*)
    INTO row_exists
    FROM T_PROD_2_TECH_LEISTUNG
    WHERE TECH_LS_ID = 350 AND TECH_LS_DEPENDENCY = buendel_ls_id and prod_id = produkt_id;

    IF (row_exists = 0)
    THEN
      Insert into T_PROD_2_TECH_LEISTUNG
      (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
      Values
        (S_T_PROD_2_TECH_LEISTUNG_0.nextval, produkt_id, 350, buendel_ls_id, '1', 1)
      ;
    END IF;
  END;
/

-- FTTX_GF_100/10_MGA + VOIP_MGA + Endgeraeteport
call voip_for_prem_glasf(404, 300, 540);
-- FTTX_GF_100/10_TK + VOIP_TK + Endgeraeteport
call voip_for_prem_glasf(405, 301, 540);
-- FTTX_GF_300/30_MGA + VOIP_MGA + Endgeraeteport
call voip_for_prem_glasf(406, 300, 540);
-- FTTX_GF_300/30_TK + VOIP_TK + Endgeraeteport
call voip_for_prem_glasf(407, 301, 540);

DROP PROCEDURE voip_for_prem_glasf;
