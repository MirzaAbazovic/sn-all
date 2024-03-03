alter table t_mwf_leitungs_bezeichnung add ORDNUNGSNUMMER VARCHAR2(10);
update t_mwf_leitungs_bezeichnung set ordnungsnummer=ordnungs_nummer;
alter table t_mwf_leitungs_bezeichnung drop column ORDNUNGS_NUMMER;