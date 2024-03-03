-- Rangierungsmatrix 
CREATE OR REPLACE PROCEDURE rangmatrix_dslvoip_adsl2plus_h (physiktyp_id IN NUMBER, prio IN NUMBER)
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE IN ('HVT', 'KVZ', 'NK', 'GEWOFAG')) LOOP
	      INSERT INTO T_RANGIERUNGSMATRIX matrix
	        (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
	        VALUES
	        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL, 480, uevtid.UEVT_ID,
	        (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
	        where PROD_2_PHYSIK.PROD_ID = 480
	        AND PROD_2_PHYSIK.PHYSIKTYP = physiktyp_id
	        AND PARENTPHYSIKTYP_ID IS NULL),
	        prio, uevtid.hvt_id_standort, null, to_date('30/01/2013','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Import')
	      ;
    END LOOP;
   END;
/
call rangmatrix_dslvoip_adsl2plus_h(516, 1);
call rangmatrix_dslvoip_adsl2plus_h(517, 2);
call rangmatrix_dslvoip_adsl2plus_h(102, 1);
call rangmatrix_dslvoip_adsl2plus_h(105, 2);

DROP PROCEDURE rangmatrix_dslvoip_adsl2plus_h;
