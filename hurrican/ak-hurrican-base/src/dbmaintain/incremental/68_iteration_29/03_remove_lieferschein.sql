
alter table t_eg_2_auftrag drop column PRODAK_ORDER__NO;
alter table t_eg_2_auftrag drop column PRODAK_ADRESSE_NO;
alter table t_eg_2_auftrag drop column PAKET_ID;
alter table t_eg_2_auftrag drop column SERIENNUMMER;
alter table t_eg_2_auftrag drop column MAC_ADRESSE;
alter table t_eg_2_auftrag drop column IP_ADRESSE;
alter table t_eg_2_auftrag drop column SELBSTABHOLUNG;
alter table t_eg_2_auftrag drop column AUSGABEDATUM;
alter table t_eg_2_auftrag drop column EXPORTDATUM;
alter table t_eg_2_auftrag drop column TRACKING_NO;
alter table t_eg_2_auftrag drop column EG_HERKUNFT;
alter table t_eg_2_auftrag drop column VERSANDART;
alter table t_eg_2_auftrag drop column LIEFERADRESSE_ID;
alter table t_eg_2_auftrag drop column REF_ID_VERSAND_STATUS;
alter table t_eg_2_auftrag drop column LIEFERSCHEIN_ID;
alter table t_eg_2_auftrag drop column PAKET_NO;

delete from t_reference where type in (
'REF_TYPE_VERSAND_GRUND',
'REF_TYPE_VERSAND_STATUS',
'REF_TYPE_LIEFERSCHEIN_STATUS',
'REF_TYPE_EG_VERSANDART'
);

drop table t_lieferschein;
drop table t_eg_iad;

drop table t_eg_2_paket;
drop table T_EG_HERKUNFT;

delete from t_service_commands where class in (
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusAbgeschlossenCommand',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusGedrucktCommand',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusRetoureCommand',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusStornoCommand',
'de.augustakom.hurrican.service.reporting.impl.command.GetLieferscheinDatenCommand',
'de.augustakom.hurrican.service.reporting.impl.command.GetLsStatistikDatenCommand'
);






