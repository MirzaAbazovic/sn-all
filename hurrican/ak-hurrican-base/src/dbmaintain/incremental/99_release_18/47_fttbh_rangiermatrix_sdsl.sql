-- Produkt zu Physiktyp Mapping anlegen
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, PRIORITY, USE_IN_RANG_MATRIX) VALUES (
  S_T_PRODUKT_2_PHYSIKTYP_0.nextVal,
  541, -- Produkt Glasfaser SDSL
  809, -- Physiktyp fuer DPO "FTTB_DPO_VDSL2"
  NULL,
  '1');


-- Rangierungsmatrix fuer Physiktyp DPO und Standorttyp FTTB anlegen

CREATE OR REPLACE PROCEDURE rm_sdsl_physik_4_fttb_h
IS
  BEGIN
    FOR uevtid IN (SELECT
                     UEVT.UEVT_ID,
                     hvt.HVT_ID_STANDORT
                   FROM T_UEVT uevt
                     JOIN T_HVT_STANDORT hvt ON (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
                     JOIN T_REFERENCE refer ON (REFER.ID = hvt.STANDORT_TYP_REF_ID)
                   WHERE REFER.STR_VALUE IN ('FTTB', 'FTTB_H')) LOOP

      INSERT INTO T_RANGIERUNGSMATRIX matrix
      (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
      VALUES
        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL,
         541, -- Produkt Glasfaser SDSL
         uevtid.UEVT_ID,
         (SELECT prod_2_physik.ID
          FROM T_PRODUKT_2_PHYSIKTYP prod_2_physik
          WHERE PROD_2_PHYSIK.PROD_ID = 541
                AND PROD_2_PHYSIK.PHYSIKTYP = 809 AND PARENTPHYSIKTYP_ID IS NULL),
         NULL,
         uevtid.hvt_id_standort,
         NULL,
         to_date('01/03/2015', 'DD/MM/YYYY'),
         to_date('01/01/2200', 'DD/MM/YYYY'),
         'dbmaintain');

    END LOOP;
  END;
/
CALL rm_sdsl_physik_4_fttb_h();
DROP PROCEDURE rm_sdsl_physik_4_fttb_h;
