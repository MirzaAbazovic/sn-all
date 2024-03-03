Insert into T_FFM_QUALIFICATION
   (ID, VERSION, QUALIFICATION)
 Values
   (S_T_FFM_QUALIFICATION_0.nextval, 0, 'Wholesale Endkundenservice');

Insert into T_FFM_QUALIFICATION_MAPPING
   (ID, QUALIFICATION_ID, PRODUCT_ID, VERSION)
 Values
   (S_T_FFM_QUAL_MAPPING_0.nextval, (select id from T_FFM_QUALIFICATION where QUALIFICATION = 'Wholesale Endkundenservice'), 600, 0);