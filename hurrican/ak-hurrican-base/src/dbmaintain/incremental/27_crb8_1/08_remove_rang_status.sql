
alter table T_RANGIERUNG drop constraint FK_RANG_2_REF;
alter table T_RANGIERUNG drop column RANG_STATUS_REF_ID;
delete from t_reference where TYPE='RANGIERUNG_STATUS';
