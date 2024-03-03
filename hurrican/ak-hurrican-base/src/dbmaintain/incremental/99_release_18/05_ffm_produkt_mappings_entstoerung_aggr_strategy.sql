delete FROM T_FFM_QUALIFICATION where FFM_PRODUCT_MAPPING_ID in (select id FROM T_FFM_PRODUCT_MAPPING where FFM_ACTIVITY_TYPE = 'RTL_Entstoerung');
delete FROM T_FFM_PRODUCT_MAPPING where FFM_ACTIVITY_TYPE = 'RTL_Entstoerung';

DECLARE
    type NumberArray is varray(5) of NUMBER(19);
    type VarcharArray is varray(5) of VARCHAR2(100);
    standortTypRefIds NumberArray := NumberArray(11000,11002,11011,11013,11017);
    ffmQualifications VarcharArray := VarcharArray('Endkundenservice HVT', 'Endkundenservice FTTB', 'Endkundenservice FTTH', 'Endkundenservice FTTC', 'Endkundenservice FTTB_H');
    ffmDurations NumberArray := NumberArray(90,45,90,90,45);
    aggregationStrategies VarcharArray := VarcharArray('TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT');
BEGIN
    FOR baAnlassId IN 75 .. 79 -- BA_ANLASS_IDs aus der T_BA_VERL_ANLASS-Tabelle
    LOOP
        FOR i IN 1 .. standortTypRefIds.count
        LOOP
            INSERT INTO T_FFM_PRODUCT_MAPPING (ID, BA_ANLASS_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY)
                values (S_T_FFM_PRODUCT_MAPPING_0.nextVal, baAnlassId, standortTypRefIds(i), 'RTL_Entstoerung', ffmDurations(i), aggregationStrategies(i));
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
