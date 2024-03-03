--
-- SQL-Script, um von DML nicht beruecksichtigte Sequences anzulegen.
--

select max(DSLAM)+1 from t_dslam;
drop sequence S_T_DSLAM_0;
create sequence S_T_DSLAM_0 start with ?;

select max(HVT_GRUPPE_ID)+1 from T_HVT_GRUPPE;
drop sequence S_T_HVT_GRUPPE_0;
create sequence S_T_HVT_GRUPPE_0 start with ?;

select max(ID)+1 from T_TXT_BAUSTEIN;
drop sequence S_T_TXT_BAUSTEIN_0;
create sequence S_T_TXT_BAUSTEIN_0 start with ?;

select max(LFDNR)+1 from T_LEISTUNG_DN;
drop sequence S_T_LEISTUNG_DN_0;
create sequence S_T_LEISTUNG_DN_0 start with ?;

select max(ID)+1 from T_LB_2_LEISTUNG;
drop sequence S_T_LB_2_LEISTUNG_0;
create sequence S_T_LB_2_LEISTUNG_0 start with ?;

drop sequence S_T_PORT_GESAMT_0;
create sequence S_T_PORT_GESAMT_0 start with 1;

grant select on S_T_DSLAM_0 to public;
grant select on S_T_HVT_GRUPPE_0 to public;
grant select on S_T_PORT_GESAMT_0 to public;
grant select on S_T_TXT_BAUSTEIN_0 to public;
grant select on S_T_LEISTUNG_DN_0 to public;
grant select on S_T_LB_2_LEISTUNG_0 to public;
	
commit;

 

 