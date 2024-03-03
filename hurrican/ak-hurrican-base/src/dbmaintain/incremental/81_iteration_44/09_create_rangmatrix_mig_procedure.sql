CREATE OR REPLACE PROCEDURE mig_rangmatrix_4_adbf2 (
   physiktyp_adbf1    NUMBER,
   physiktyp_adbf2    NUMBER)
IS
BEGIN
   DECLARE
      matrix                 t_rangierungsmatrix%ROWTYPE;
      prod2pt_adbf1          t_produkt_2_physiktyp%ROWTYPE;
      prod2pt_adbf2          t_produkt_2_physiktyp%ROWTYPE;
      prod_id                T_PRODUKT_2_PHYSIKTYP.PROD_ID%TYPE;
      physiktyp              T_PRODUKT_2_PHYSIKTYP.PHYSIKTYP%TYPE;
      physiktyp_add          T_PRODUKT_2_PHYSIKTYP.PHYSIKTYP_ADDITIONAL%TYPE;
      parent_physiktyp       T_PRODUKT_2_PHYSIKTYP.PARENTPHYSIKTYP_ID%TYPE;
      p2pt_query             VARCHAR2 (512);
      p2pt_query_head        VARCHAR2 (512)
         := 'SELECT * FROM t_produkt_2_physiktyp p2pt WHERE';
   BEGIN
      FOR matrix
         IN (SELECT rm.*
               FROM t_rangierungsmatrix rm
              WHERE     RM.PRODUKT2PHYSIKTYP_ID IN
                           (SELECT P2PT.ID
                              FROM t_produkt_2_physiktyp p2pt
                             WHERE    P2PT.PHYSIKTYP = physiktyp_adbf1
                                   OR P2PT.PHYSIKTYP_ADDITIONAL =
                                         physiktyp_adbf1
                                   OR P2PT.PARENTPHYSIKTYP_ID =
                                         physiktyp_adbf1)
                    AND RM.GUELTIG_VON <= SYSDATE
                    AND (RM.GUELTIG_BIS IS NULL OR RM.GUELTIG_BIS > SYSDATE))
      LOOP
         BEGIN
            SELECT *
              INTO prod2pt_adbf1
              FROM t_produkt_2_physiktyp p2pt
             WHERE p2pt.ID = matrix.PRODUKT2PHYSIKTYP_ID;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               raise_application_error (-20000, 'PROD2PT fuer ADBF1 fehlt!');
         END;

         prod_id := prod2pt_adbf1.PROD_ID;
         physiktyp := prod2pt_adbf1.PHYSIKTYP;
         physiktyp_add := prod2pt_adbf1.PHYSIKTYP_ADDITIONAL;
         parent_physiktyp := prod2pt_adbf1.PARENTPHYSIKTYP_ID;

         IF physiktyp = physiktyp_adbf1
         THEN
            physiktyp := physiktyp_adbf2;
         END IF;

         p2pt_query := p2pt_query_head || ' P2PT.PROD_ID = ' || prod_id;
         p2pt_query := p2pt_query || ' AND P2PT.PHYSIKTYP = ' || physiktyp;

         IF physiktyp_add IS NOT NULL
         THEN
            IF physiktyp_add = physiktyp_adbf1
            THEN
               physiktyp_add := physiktyp_adbf2;
            END IF;

            p2pt_query :=
                  p2pt_query
               || ' AND P2PT.PHYSIKTYP_ADDITIONAL = '
               || physiktyp_add;
         ELSE
            p2pt_query :=
               p2pt_query || ' AND P2PT.PHYSIKTYP_ADDITIONAL IS NULL';
         END IF;

         IF parent_physiktyp IS NOT NULL
         THEN
            IF parent_physiktyp = physiktyp_adbf1
            THEN
               parent_physiktyp := physiktyp_adbf2;
            END IF;

            p2pt_query :=
                  p2pt_query
               || ' AND P2PT.PARENTPHYSIKTYP_ID = '
               || parent_physiktyp;
         ELSE
            p2pt_query := p2pt_query || ' AND P2PT.PARENTPHYSIKTYP_ID IS NULL';
         END IF;

         BEGIN
            EXECUTE IMMEDIATE p2pt_query INTO prod2pt_adbf2;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               prod2pt_adbf2 := NULL;
         END;

         IF prod2pt_adbf2.ID IS NOT NULL
         THEN
            INSERT INTO T_RANGIERUNGSMATRIX (ID,
                                             PROD_ID,
                                             UEVT_ID,
                                             PRODUKT2PHYSIKTYP_ID,
                                             PRIORITY,
                                             HVT_STANDORT_ID_ZIEL,
                                             PROJEKTIERUNG,
                                             GUELTIG_VON,
                                             GUELTIG_BIS,
                                             BEARBEITER)
                 VALUES (S_T_RANGIERUNGSMATRIX_0.NEXTVAL,
                         matrix.PROD_ID,
                         matrix.UEVT_ID,
                         prod2pt_adbf2.ID,
                         matrix.PRIORITY,
                         matrix.HVT_STANDORT_ID_ZIEL,
                         matrix.PROJEKTIERUNG,
                         TRUNC(SYSDATE),
                         TO_DATE ('01/01/2200', 'DD/MM/YYYY'),
                         'ADBF2_MIG');
         END IF;
      END LOOP;
   END;
END;
/
