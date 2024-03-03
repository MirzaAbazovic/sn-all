-- HUR-24136 HUR-24346 1ter Teil: Einführung einer neuen Vorgangsart

--SET SERVEROUTPUT ON ;
declare
  activityType varchar2(50) := 'RTL_NEU_MK_Premium';
  durationValue number := 45;
begin
    for r_product IN (
          select p.* from T_PRODUKT p
          where p.ANSCHLUSSART in ('AK-SDSL Line 2300',
          'AK-SDSLoffice1200',
          'AK-SDSLoffice2300',
          'Glasfaser SDSL',
          'SDSL 10000',
          'SDSL 15000',
          'SDSL 20000',
          'SDSL 2300',
          'SDSL n-Draht Option',
          'SDSL 4600',
          'SDSL 6800')
          )
    loop
      -- NEU
      insert into T_FFM_PRODUCT_MAPPING(ID, VERSION, PROD_ID, STANDORT_TYP,
              FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
      select S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, r_product.prod_id , null,
              activityType, durationValue, 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'NEU'
      from dual where not exists
            (select 1 from T_FFM_PRODUCT_MAPPING t
              where t.prod_id = r_product.prod_id and t.FFM_ACTIVITY_TYPE = activityType and t.BA_FFM_TYP = 'NEU');
      --DBMS_OUTPUT.put_line('Mapping for product[' || r_product.prod_id || '-' || r_product.ANSCHLUSSART || '] inserted [' || sql%rowcount || '] rows');

      -- ENTSTOERUNG
      insert into T_FFM_PRODUCT_MAPPING(ID, VERSION, PROD_ID, STANDORT_TYP,
              FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
      select S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, r_product.prod_id , null,
              activityType, durationValue, 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'ENTSTOERUNG'
      from dual where not exists
            (select 1 from T_FFM_PRODUCT_MAPPING t
              where t.prod_id = r_product.prod_id and t.FFM_ACTIVITY_TYPE = activityType and t.BA_FFM_TYP = 'ENTSTOERUNG');
      --DBMS_OUTPUT.put_line('Mapping for product[' || r_product.prod_id || '-' || r_product.ANSCHLUSSART || '] inserted [' || sql%rowcount || '] rows');

      -- KUENDIGUNG
      insert into T_FFM_PRODUCT_MAPPING(ID, VERSION, PROD_ID, STANDORT_TYP,
              FFM_ACTIVITY_TYPE, FFM_PLANNED_DURATION, AGGREGATION_STRATEGY, BA_FFM_TYP)
      select S_T_FFM_PRODUCT_MAPPING_0.nextVal, 0, r_product.prod_id , null,
              'RTL_Kuendigung', 30, 'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT', 'KUENDIGUNG'
      from dual where not exists
            (select 1 from T_FFM_PRODUCT_MAPPING t
              where t.prod_id = r_product.prod_id and t.FFM_ACTIVITY_TYPE = 'RTL_Kuendigung' and t.BA_FFM_TYP = 'KUENDIGUNG');
      --DBMS_OUTPUT.put_line('Mapping for product[' || r_product.prod_id || '-' || r_product.ANSCHLUSSART || '] inserted [' || sql%rowcount || '] rows');
    end loop;
end;
/
