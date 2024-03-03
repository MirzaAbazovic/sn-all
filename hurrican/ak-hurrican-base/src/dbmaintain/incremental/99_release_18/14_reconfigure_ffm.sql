alter table T_BA_VERL_ANLASS add FFM_TYP VARCHAR2(20);

ALTER TABLE T_BA_VERL_ANLASS ADD (
  CONSTRAINT CK_BAVERLA_FFMTYP
  CHECK (FFM_TYP IN (
    'NEU',
    'KUENDIGUNG',
    'ENTSTOERUNG'
  )));

update T_BA_VERL_ANLASS set FFM_TYP='ENTSTOERUNG' where ID in (79,78,77,76,75);
update T_BA_VERL_ANLASS set FFM_TYP='NEU' where ID in (43,72,70,27);
update T_BA_VERL_ANLASS set FFM_TYP='KUENDIGUNG' where ID in (13);


alter table T_FFM_PRODUCT_MAPPING add BA_FFM_TYP VARCHAR2(25);
ALTER TABLE T_FFM_PRODUCT_MAPPING ADD (
  CONSTRAINT CK_FFMPM_FFMTYP
  CHECK (BA_FFM_TYP IN (
    'NEU',
    'KUENDIGUNG',
    'ENTSTOERUNG'
  )));


update T_FFM_PRODUCT_MAPPING set BA_FFM_TYP='ENTSTOERUNG' where BA_ANLASS_ID in (79,78,77,76,75);
update T_FFM_PRODUCT_MAPPING set BA_FFM_TYP='NEU' where BA_ANLASS_ID in (43,72,70,27);
update T_FFM_PRODUCT_MAPPING set BA_FFM_TYP='NEU' where BA_ANLASS_ID is null;
update T_FFM_PRODUCT_MAPPING set BA_FFM_TYP='KUENDIGUNG' where BA_ANLASS_ID in (13);

delete from T_FFM_QUALIFICATION q where Q.FFM_PRODUCT_MAPPING_ID in (select pm.id from T_FFM_PRODUCT_MAPPING pm where PM.BA_ANLASS_ID in (76,77,78,79));
delete from T_FFM_PRODUCT_MAPPING where BA_ANLASS_ID in (76,77,78,79);

alter table T_FFM_PRODUCT_MAPPING modify BA_FFM_TYP VARCHAR2(20) NOT NULL;
alter table T_FFM_PRODUCT_MAPPING drop column BA_ANLASS_ID;