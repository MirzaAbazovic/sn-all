DECLARE
    type NumberArray is varray(5) of NUMBER(19);
    standortTypRefIds NumberArray := NumberArray(11000,11002,11011,11013,11017);
    type VarcharArray is varray(5) of VARCHAR2(100);
    ffmQualifications VarcharArray := VarcharArray('Endkundenservice HVT', 'Endkundenservice FTTB', 'Endkundenservice FTTH', 'Endkundenservice FTTC', 'Endkundenservice FTTB_H');
BEGIN
    FOR baAnlassId IN 75 .. 79 -- BA_ANLASS_IDs aus der T_BA_VERL_ANLASS-Tabelle
    LOOP
        FOR i IN 1 .. standortTypRefIds.count
        LOOP
            INSERT INTO T_FFM_PRODUCT_MAPPING (ID, BA_ANLASS_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY)
                values (S_T_FFM_PRODUCT_MAPPING_0.nextVal, baAnlassId, standortTypRefIds(i), 'RTL_Entstoerung', 90, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT');
            INSERT INTO T_FFM_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, ID, FFM_QUALIFICATION)
                SELECT
                  ffm.ID,
                  S_T_FFM_QUALIFICATION_0.nextVal,
                  ffmQualifications(i)
                FROM T_FFM_PRODUCT_MAPPING ffm
                WHERE ffm.STANDORT_TYP = standortTypRefIds(i) AND FFM_ACTIVITY_TYPE = 'RTL_Entstoerung' AND BA_ANLASS_ID = baAnlassId;
        END LOOP;
    END LOOP;
END;
/
