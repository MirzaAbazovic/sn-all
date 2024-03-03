-- Glasfaser SDSL (541) (FTTB und FTTC)
Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 541, 11002, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=541 and p.STANDORT_TYP=11002),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 541, 11013, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=541 and p.STANDORT_TYP=11013),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );


-- SDSL...     63, 64, 66, 67, 68, 69, 99
Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 63, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=63 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 64, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=64 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 66, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=66 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 67, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=67 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 68, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=68 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 69, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=69 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );

Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 99, 11001, 'RTL_NEU_IK', 45, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=99 and p.STANDORT_TYP=11001),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SDSL')
 );


-- SIP-Trunk (580)
Insert into T_FFM_PRODUCT_MAPPING
   (ID, VERSION, PROD_ID, STANDORT_TYP, FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
 Values
   (S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, 580, NULL, 'RTL_sonstige', 60, 'TECHNICAL_PARAMS_INCLUDED_WITH_FIXED_TIMESLOT', 'NEU');
insert into T_FFM_PRODMAP_2_QUALIFICATION (FFM_PRODUCT_MAPPING_ID, FFM_QUALIFICATION_ID)
 values (
   (select p.ID from T_FFM_PRODUCT_MAPPING p where p.PROD_ID=580),
   (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SIP Trunk')
 );


-- TechLeistung VOIP_READY_66
update T_TECH_LEISTUNG set FFM_QUALIFICATION_ID=(select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SIP Trunk Access');


-- B/Montage
-- aktuell keine techn. Leistung dafuer vorhanden! In Taifun mit EXT_SONSTIGES__NO=110000 konfiguriert
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
    DESCRIPTION, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION,
    PREVENT_AUTO_DISPATCH, FFM_QUALIFICATION_ID)
 Values
   (650, 'Montage', 31500, 'MONTAGE', ' ',
    'Montage durch M-net', '1', TO_DATE('01/01/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0,
    '0', (select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='Installationsservice'));
-- ==> Mapping-Eintrag in Taifun an Billing-Team uebergeben!

insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 542);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 541);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 540);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 536);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 535);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 521);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 522);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 520);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 515);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 514);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 513);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 512);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 511);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 500);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 481);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 480);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 446);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 445);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 440);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 431);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 430);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 421);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 420);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 411);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 410);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 400);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 352);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 351);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 350);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 340);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 338);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 337);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 336);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 69);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 68);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 67);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 66);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 64);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 63);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 62);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 61);
insert into T_PROD_2_TECH_LEISTUNG (ID, TECH_LS_ID, PROD_ID) values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 650, 60);