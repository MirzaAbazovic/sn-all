CREATE OR REPLACE PROCEDURE rangmatrix_wholesale_fttb
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE = 'FTTB') LOOP

      insert into T_RANGIERUNGSMATRIX matrix
        (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
        VALUES
        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL ,600, uevtid.UEVT_ID,
        (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
        where PROD_2_PHYSIK.PROD_ID = 600
        AND PROD_2_PHYSIK.PHYSIKTYP = (select id from T_PHYSIKTYP where name = 'FTTB_VDSL2')
        AND PARENTPHYSIKTYP_ID IS NULL),
        null, uevtid.hvt_id_standort, null, to_date('13/03/2012','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Trenker')
      ;
    END LOOP;
   END;
/

-- Aufruf
delete T_RANGIERUNGSMATRIX ma
where ma.prod_id = 600;

call rangmatrix_wholesale_fttb();