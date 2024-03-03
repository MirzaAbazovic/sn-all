-- Produkt2PhysikTyp fuer 'ADSL2+ VoIP (H)' anlegen (analog zu 'ADSL2+ (H)')
CREATE OR REPLACE PROCEDURE mig_p2pt_4_adbf2
IS
BEGIN
   DECLARE
      physiktyp_adbf1   CONSTANT NUMBER (19) := 513;
      physiktyp_adbf2   CONSTANT NUMBER (19) := 516;
      prod2pt_adbf1              t_produkt_2_physiktyp%ROWTYPE;
      physiktyp                  T_PRODUKT_2_PHYSIKTYP.PHYSIKTYP%TYPE;
      physiktyp_add              T_PRODUKT_2_PHYSIKTYP.PHYSIKTYP_ADDITIONAL%TYPE;
      parent_physiktyp           T_PRODUKT_2_PHYSIKTYP.PARENTPHYSIKTYP_ID%TYPE;
   BEGIN
      FOR prod2pt_adbf1
         IN (SELECT *
               FROM t_produkt_2_physiktyp p2pt
              WHERE    P2PT.PHYSIKTYP = physiktyp_adbf1
                    OR P2PT.PHYSIKTYP_ADDITIONAL = physiktyp_adbf1
                    OR P2PT.PARENTPHYSIKTYP_ID = physiktyp_adbf1)
      LOOP
         physiktyp := prod2pt_adbf1.PHYSIKTYP;
         physiktyp_add := prod2pt_adbf1.PHYSIKTYP_ADDITIONAL;
         parent_physiktyp := prod2pt_adbf1.PARENTPHYSIKTYP_ID;

         IF physiktyp = physiktyp_adbf1
         THEN
            physiktyp := physiktyp_adbf2;
         END IF;

         IF physiktyp_add IS NOT NULL AND physiktyp_add = physiktyp_adbf1
         THEN
            physiktyp_add := physiktyp_adbf2;
         END IF;

         IF     parent_physiktyp IS NOT NULL
            AND parent_physiktyp = physiktyp_adbf1
         THEN
            parent_physiktyp := physiktyp_adbf2;
         END IF;

         INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID,
                                            PROD_ID,
                                            PHYSIKTYP,
                                            PHYSIKTYP_ADDITIONAL,
                                            VIRTUELL,
                                            PARENTPHYSIKTYP_ID,
                                            VERSION)
              VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.NEXTVAL,
                      prod2pt_adbf1.PROD_ID,
                      physiktyp,
                      physiktyp_add,
                      prod2pt_adbf1.VIRTUELL,
                      parent_physiktyp,
                      0);
      END LOOP;
   END;
END;
/

CALL mig_p2pt_4_adbf2 ();

DROP PROCEDURE mig_p2pt_4_adbf2;