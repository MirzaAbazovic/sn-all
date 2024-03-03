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
-- Physiktypzuordnung Premium DSL + VoIP -> FTTB_GFAST
call produkt_2_physikmapping(543);
-- Physiktypzuordnung Premium DSL only (IPv6) -> FTTB_GFAST
call produkt_2_physikmapping(544);


-- Wegwerfen
drop procedure produkt_2_physikmapping;