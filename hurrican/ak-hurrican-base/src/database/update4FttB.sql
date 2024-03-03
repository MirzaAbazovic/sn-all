ALTER TABLE t_eg_2_auftrag ADD COLUMN MAC_ADRESSE VARCHAR(20) AFTER SERIENNUMMER;
ALTER TABLE t_eg_2_auftrag ADD COLUMN IP_ADRESSE VARCHAR(15) AFTER MAC_ADRESSE;


CREATE TABLE T_EG_IAD (
	ID INTEGER(9) NOT NULL AUTO_INCREMENT,
	EG_ID INTEGER(9) NOT NULL,
	SERIENNUMMER VARCHAR(30),
	MAC_ADRESSE VARCHAR(20),
	IP_ADRESSE VARCHAR(15),
	AUFTRAG_ID INTEGER(9),
	VERLAUF_ID INTEGER(9),
	PRIMARY KEY (ID)
)ENGINE=InnoDB;

ALTER TABLE t_eg_iad
  ADD CONSTRAINT FK_EG_IAD_2_AUFTRAG
      FOREIGN KEY (AUFTRAG_ID)
      REFERENCES T_AUFTRAG (ID);
   
ALTER TABLE t_eg_iad
  ADD CONSTRAINT FK_EG_IAD_2_VERLAUF
      FOREIGN KEY (VERLAUF_ID)
      REFERENCES T_VERLAUF (ID);
      
ALTER TABLE t_eg_iad
  ADD CONSTRAINT FK_EG_IAD_2_EG
      FOREIGN KEY (EG_ID)
      REFERENCES T_EG (ID);		
      
      
ALTER TABLE t_endstelle ADD COLUMN EXPORT_TAIFUN BIT;

insert into t_anschlussart (ID, ANSCHLUSSART) 
values ( 15, 'FttB');
insert into t_anbindungsart (ID, ANBINDUNGSART) 
values ( 12, 'FttB');

insert into t_abteilung (ID, TEXT, NIEDERLASSUNG_ID, RELEVANT_4_PROJ, RELEVANT_4_BA) 
values ( 12, 'VOIPMuc', 3, false, true);
insert into t_abteilung (ID, TEXT, NIEDERLASSUNG_ID, RELEVANT_4_PROJ, RELEVANT_4_BA) 
values ( 13, 'DatenMuc', 3, false, true);
insert into t_abteilung (ID, TEXT, NIEDERLASSUNG_ID, RELEVANT_4_PROJ, RELEVANT_4_BA) 
values ( 14, 'SCTMuc', 3, false, true);

insert into t_ba_abt_config (ID,NAME) 
values ( 15, 'VOIPMuc - DatenMuc - SCTMuc');

insert into t_ba_abtconfig_2_abt (ID, CONFIG_ID, ABT_ID) 
values ( 28, 15, 12);
insert into t_ba_abtconfig_2_abt (ID, CONFIG_ID, ABT_ID) 
values ( 29, 15, 13);
insert into t_ba_abtconfig_2_abt (ID, CONFIG_ID, ABT_ID) 
values ( 30, 15, 14);


-- Test-Endgeräte
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer1', 'Mac-Adresse1', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer2', 'Mac-Adresse2', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer3', 'Mac-Adresse3', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer4', 'Mac-Adresse4', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer5', 'Mac-Adresse5', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer6', 'Mac-Adresse6', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer7', 'Mac-Adresse7', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer8', 'Mac-Adresse8', 'IP-Adresse');
insert into t_eg_iad (EG_ID, SERIENNUMMER, MAC_ADRESSE, IP_ADRESSE) 
values (29, 'Seriennummer9', 'Mac-Adresse9', 'IP-Adresse');



      