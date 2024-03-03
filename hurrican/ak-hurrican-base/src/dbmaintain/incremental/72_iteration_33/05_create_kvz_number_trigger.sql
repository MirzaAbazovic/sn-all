-- Prueft, ob ein FTTC KVZ Standort (StandortTypId = 11013) immer eine KVZ Nummer gesetzt hat
CREATE OR REPLACE TRIGGER TRBIU_G2TL_KVZ_NUMBER
   BEFORE INSERT OR UPDATE OF KVZ_NUMBER
   ON T_GEO_ID_2_TECH_LOCATION
   FOR EACH ROW
DECLARE
   standortTyp     T_HVT_STANDORT.STANDORT_TYP_REF_ID%TYPE;
BEGIN
   IF (:new.KVZ_NUMBER IS NULL)
   THEN
      SELECT STANDORT_TYP_REF_ID
        INTO standortTyp
        FROM T_HVT_STANDORT
       WHERE HVT_ID_STANDORT = :new.HVT_ID_STANDORT;

      -- Standorttyp 11013 = FTTC KVZ
      IF (standortTyp = 11013)
      THEN
         raise_application_error (
            -20000,
            'Fuer FTTC KVZ Standorte muss eine KVZ Nummer gesetzt sein!');
      END IF;
   END IF;
END;
/