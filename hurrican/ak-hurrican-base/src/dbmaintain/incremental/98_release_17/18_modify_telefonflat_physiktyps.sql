
-- Physiktyp FTTB_VDSL2 fuer Telefon Flat
insert into t_produkt_2_physiktyp (id, prod_id, physiktyp, priority)
  values (s_t_produkt_2_physiktyp_0.nextVal, 511, 800, 100);
-- Physiktyp FTTH_POTS fuer Telefon Flat
insert into t_produkt_2_physiktyp (id, prod_id, physiktyp, priority)
  values (s_t_produkt_2_physiktyp_0.nextVal, 511, 807, 200);

-- Prio von FTTH_ETH zurueck setzen
update t_produkt_2_physiktyp set priority=100 where prod_id=511 and physiktyp=808;


-- Rangierungsmatrix fuer TelefonFlat und Physiktypen 800, 807, 808 anlegen
CREATE OR REPLACE PROCEDURE rangmatrix_telefonflat_fttb
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE in ('FTTB')) LOOP

      insert into T_RANGIERUNGSMATRIX matrix
        (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
        VALUES
        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL, 511, uevtid.UEVT_ID,
        (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
        where PROD_2_PHYSIK.PROD_ID = 511
        AND PROD_2_PHYSIK.PHYSIKTYP = (select id from T_PHYSIKTYP where name in ('FTTB_VDSL2'))
        AND PARENTPHYSIKTYP_ID IS NULL),
        null, uevtid.hvt_id_standort, null, to_date('01/11/2014','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Glink')
      ;
    END LOOP;
   END;
/

CREATE OR REPLACE PROCEDURE rangmatrix_telefonflat_ftth
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE in ('FTTH')) LOOP

      insert into T_RANGIERUNGSMATRIX matrix
        (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
        VALUES
        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL, 511, uevtid.UEVT_ID,
        (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
        where PROD_2_PHYSIK.PROD_ID = 511
        AND PROD_2_PHYSIK.PHYSIKTYP = (select id from T_PHYSIKTYP where name in ('FTTH_POTS'))
        AND PARENTPHYSIKTYP_ID IS NULL),
        null, uevtid.hvt_id_standort, null, to_date('01/11/2014','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Glink')
      ;
    END LOOP;
   END;
/

CREATE OR REPLACE PROCEDURE rangmatrix_telefonflat_fttb_h
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE in ('FTTB_H')) LOOP

      insert into T_RANGIERUNGSMATRIX matrix
        (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
        VALUES
        (S_T_RANGIERUNGSMATRIX_0.NEXTVAL, 511, uevtid.UEVT_ID,
        (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
        where PROD_2_PHYSIK.PROD_ID = 511
        AND PROD_2_PHYSIK.PHYSIKTYP = (select id from T_PHYSIKTYP where name in ('FTTH_ETH'))
        AND PARENTPHYSIKTYP_ID IS NULL),
        null, uevtid.hvt_id_standort, null, to_date('01/11/2014','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Glink')
      ;
    END LOOP;
   END;
/

-- Aufruf
call rangmatrix_telefonflat_fttb();
call rangmatrix_telefonflat_ftth();
call rangmatrix_telefonflat_fttb_h();

-- Wegwerfen
drop procedure rangmatrix_telefonflat_fttb;
drop procedure rangmatrix_telefonflat_ftth;
drop procedure rangmatrix_telefonflat_fttb_h;

