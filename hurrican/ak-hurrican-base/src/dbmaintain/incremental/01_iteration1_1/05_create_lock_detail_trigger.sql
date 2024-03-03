   --------------------------------------------------------------------------------
   --  Update auf T_LOCK_DETAIL
   --- Spalte EXECUTED_AT ist gesetzt (also NOT NULL)
   --- Trigger soll prüfen, ob alle Einträge in T_LOCK_DETAIL zu der LOCK_ID das Feld EXECUTED_AT gesetzt haben
   --- falls ja, soll der Status (LOCK_STATE_REF_ID) von T_LOCK auf den Wert 1511 gesetzt werden.
   --- Ausfuehrung allerdings nur, wenn es sich um den Abschluss eines Sperr-Vorgangs
   --- vom Typ 'entsperren' (== 1503) handelt.
   --------------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRAIU_LOCK_DETAIL
   AFTER INSERT OR UPDATE
   ON T_LOCK_DETAIL
DECLARE
   cannot_change_counter EXCEPTION;

   CURSOR c1
   IS
        SELECT   B.ID, 1511 AS LOCK_STATE_REF_ID_NEU
          FROM   T_LOCK B, T_LOCK_DETAIL A
         WHERE   NVL (B.LOCK_STATE_REF_ID, 0) <> 1511 AND
                 B.LOCK_MODE_REF_ID = 1503 AND
                 A.LOCK_ID = B.ID AND
                 NOT EXISTS (SELECT   C.ID
                               FROM   T_LOCK_DETAIL C
                              WHERE   C.LOCK_ID = A.LOCK_ID AND
                                      C.EXECUTED_AT IS NULL)
      GROUP BY   B.ID;

   r1   c1%ROWTYPE;
BEGIN
   FOR r1 IN c1
   LOOP
      UPDATE   T_LOCK a
         SET   a.LOCK_STATE_REF_ID = r1.LOCK_STATE_REF_ID_NEU
       WHERE   A.ID = r1.ID;
   END LOOP;
EXCEPTION
   WHEN cannot_change_counter
   THEN
      raise_application_error (
         -20000,
         'TRAIU_LOCK_DETAIL: ' || r1.ID || '-' || SQLERRM
      );
END;
/

