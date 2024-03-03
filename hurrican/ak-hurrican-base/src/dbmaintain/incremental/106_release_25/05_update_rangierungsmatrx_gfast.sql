-- Rangierungsmatrix fuer Physiktyp FTTB_GFAST (810) anlegen
CREATE OR REPLACE PROCEDURE rangmatrix_fttb_dpu(prodID IN NUMBER,physikId NUMBER)
IS
    prod2physik T_PRODUKT_2_PHYSIKTYP%ROWTYPE;
  BEGIN
      SELECT * INTO prod2physik FROM T_PRODUKT_2_PHYSIKTYP p2p WHERE p2p.PROD_ID = prodId AND p2p.PHYSIKTYP = physikId;

      FOR uevtid IN
        (SELECT UEVT.UEVT_ID, hvt.HVT_ID_STANDORT FROM T_UEVT uevt
          JOIN T_HVT_STANDORT hvt ON (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
          JOIN T_REFERENCE refer ON (REFER.ID = hvt.STANDORT_TYP_REF_ID)
          where REFER.STR_VALUE in ('FTTB'))
        LOOP
            INSERT INTO T_RANGIERUNGSMATRIX matrix
               (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
            SELECT
                S_T_RANGIERUNGSMATRIX_0.NEXTVAL,
                prodID,
                uevtid.UEVT_ID,
                prod2physik.id,
                prod2physik.priority,
                uevtid.hvt_id_standort,
                null,
                to_date('01/01/2017','DD/MM/YYYY'),
                to_date('01/01/2200','DD/MM/YYYY'),
                'Import'
            FROM dual WHERE NOT EXISTS
              (SELECT * FROM T_RANGIERUNGSMATRIX rang
                        WHERE rang.prod_ID = prodID
                        AND rang.UEVT_ID = uevtid.UEVT_ID
                        AND rang.HVT_STANDORT_ID_ZIEL = uevtid.hvt_id_standort
                        AND rang.PRODUKT2PHYSIKTYP_ID = prod2physik.id)
            ;
        END LOOP;
  END;
/

-- Aufruf
-- Rangierungsmatrix FTTX Telefon, FTTB_GFAST
call rangmatrix_fttb_dpu(511, 810);
-- Rangierungsmatrix FTTX DSL, FTTB_GFAST
call rangmatrix_fttb_dpu(512, 810);
-- Rangierungsmatrix FTTX DSL + Fon, FTTB_GFAST
call rangmatrix_fttb_dpu(513, 810);
-- Rangierungsmatrix Premium Glasfaser-DSL Doppel-Flat, FTTB_GFAST
call rangmatrix_fttb_dpu(540, 810);
-- Rangierungsmatrix Glasfaser SDSL, FTTB_GFAST
call rangmatrix_fttb_dpu(541, 810);
-- Rangierungsmatrix  Glasfaser ADSL, FTTB_GFAST
call rangmatrix_fttb_dpu(542, 810);

-- Wegwerfen
drop procedure rangmatrix_fttb_dpu;
