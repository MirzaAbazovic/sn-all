CREATE OR REPLACE TRIGGER TRGA_T_EQ_CROSS_CONNECTION
   AFTER INSERT OR UPDATE
   ON T_EQ_CROSS_CONNECTION
DECLARE
   icnt   NUMBER (10);
BEGIN
   DECLARE
      varBRAS_POOL_ID   T_EQ_CROSS_CONNECTION.BRAS_POOL_ID%TYPE;
      varBRAS_OUTER     T_EQ_CROSS_CONNECTION.BRAS_OUTER%TYPE;
      varBRAS_INNER     T_EQ_CROSS_CONNECTION.BRAS_INNER%TYPE;
      varvalid_from2    DATE;
      varvalid_to1      DATE;

      CURSOR c1
      IS
         SELECT a.BRAS_POOL_ID,
                a.BRAS_OUTER,
                a.BRAS_INNER
         FROM T_EQ_CROSS_CONNECTION a
         WHERE
         a.BRAS_POOL_ID IS NOT NULL
         GROUP BY a.BRAS_POOL_ID,
                  a.BRAS_OUTER,
                  a.BRAS_INNER
         HAVING COUNT ( * ) > 1;

      r1                c1%ROWTYPE;

      CURSOR c2
      IS
         SELECT a.valid_from,
                a.valid_to
         FROM T_EQ_CROSS_CONNECTION a
         WHERE a.BRAS_POOL_ID = varBRAS_POOL_ID AND
               a.BRAS_OUTER = varBRAS_OUTER AND
               a.BRAS_INNER = varBRAS_INNER
         ORDER BY a.valid_from,
                  a.valid_to;

      r2                c2%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         icnt := 0;
         varBRAS_POOL_ID := r1.BRAS_POOL_ID;
         varBRAS_OUTER := r1.BRAS_OUTER;
         varBRAS_INNER := r1.BRAS_INNER;

         varvalid_from2 := NULL;
         varvalid_to1 := NULL;


         FOR r2 IN c2
         LOOP
            icnt :=
               icnt +
               1;

            varvalid_from2 := r2.valid_from;

            IF icnt > 1
            THEN
               IF varvalid_from2 <= varvalid_to1
               THEN
                  --                  DBMS_OUTPUT.PUT_LINE (icnt);
                  --                  DBMS_OUTPUT.PUT_LINE ('varvalid_from2 = ' ||
                  --                                        varvalid_to1);
                  --                  DBMS_OUTPUT.PUT_LINE ('varvalid_to1 = ' ||
                  --                                        varvalid_to1);
                  raise_application_error (
                     -20000,
                     'duplicates for BRAS_POOL_ID/BRAS_OUTER/BRAS_INNER: ' ||
                     varBRAS_POOL_ID ||
                     '/' ||
                     varBRAS_OUTER ||
                     '/' ||
                     varBRAS_INNER
                  );

               END IF;
            END IF;
            varvalid_to1 := r2.valid_to;
         END LOOP;
      END LOOP;
   END;
END;
/

ALTER TRIGGER TRGA_T_EQ_CROSS_CONNECTION DISABLE;
