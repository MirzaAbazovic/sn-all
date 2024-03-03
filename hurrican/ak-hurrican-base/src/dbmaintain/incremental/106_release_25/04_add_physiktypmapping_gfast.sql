CREATE OR REPLACE PROCEDURE produkt_2_physikmapping(prodID IN NUMBER)
IS
   BEGIN
     Insert into T_PRODUKT_2_PHYSIKTYP
     (ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL, PARENTPHYSIKTYP_ID, VERSION, PRIORITY, USE_IN_RANG_MATRIX)
     SELECT
        S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, prodID, 810, NULL, '0', NULL, 1, 200, 1
     from dual where not exists (select * from T_PRODUKT_2_PHYSIKTYP where PROD_ID = prodID and PHYSIKTYP = 810 );
    END;
/

-- Aufruf
-- Physiktypzuordnung FTTX Telefon -> FTTB_GFAST
call produkt_2_physikmapping(511);
-- Physiktypzuordnung FTTX DSL -> FTTB_GFAST
call produkt_2_physikmapping(512);
-- Physiktypzuordnung FTTX DSL + Fon -> FTTB_GFAST
call produkt_2_physikmapping(513);
-- Physiktypzuordnung Premium Glasfaser-DSL Doppel-Flat -> FTTB_GFAST
call produkt_2_physikmapping(540);
 -- Physiktypzuordnung Glasfaser SDSL -> FTTB_GFAST
call produkt_2_physikmapping(541);
  -- Physiktypzuordnung Glasfaser ADSL -> FTTB_GFAST
call produkt_2_physikmapping(542);

-- Wegwerfen
drop procedure produkt_2_physikmapping;
