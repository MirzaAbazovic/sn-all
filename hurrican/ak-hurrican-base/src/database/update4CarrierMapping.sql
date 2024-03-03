-- 
-- SQL-Script, um Carrier-Mappings einzufuehren.
--

CREATE TABLE T_CARRIER_MAPPING (
       ID NUMBER(10) NOT NULL
     , CARRIER_ID NUMBER(10) NOT NULL
     , CARRIER_CONTACT_ID NUMBER(10)
     , CARRIER_KENNUNG_ID NUMBER(10)
);

ALTER TABLE T_CARRIER_MAPPING
  ADD CONSTRAINT PK_T_CARRIER_MAPPING
      PRIMARY KEY (ID);

ALTER TABLE T_CARRIER_MAPPING ADD CONSTRAINT UK_T_CARRIER_MAPPING UNIQUE (CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID);
      
CREATE INDEX IX_FK_CRMAPPING_2_CRCONTACT
	ON T_CARRIER_MAPPING (CARRIER_CONTACT_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CARRIER_MAPPING
  ADD CONSTRAINT FK_CRMAPPING_2_CRCONTACT
      FOREIGN KEY (CARRIER_CONTACT_ID)
      REFERENCES T_CARRIER_CONTACT (ID);

CREATE INDEX IX_FK_CRMAPPING_2_CRKENNUNG
	ON T_CARRIER_MAPPING (CARRIER_KENNUNG_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CARRIER_MAPPING
  ADD CONSTRAINT FK_CRMAPPING_2_CRKENNUNG
      FOREIGN KEY (CARRIER_KENNUNG_ID)
      REFERENCES T_CARRIER_KENNUNG (ID);

CREATE INDEX IX_FK_CRMAPPING_2_CARRIER
	ON T_CARRIER_MAPPING (CARRIER_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CARRIER_MAPPING
  ADD CONSTRAINT FK_CRMAPPING_2_CARRIER
      FOREIGN KEY (CARRIER_ID)
      REFERENCES T_CARRIER (ID);


delete from t_carrier_mapping;

-- neue Carrier anlegen
update T_CARRIERBESTELLUNG set CARRIER_ID=22 where CARRIER_ID=21;
delete from T_CARRIER where ID=21;
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (29, 'Colt', 3, 1);
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (30, 'QSC/Plusnet/Celox', 3, 1);
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (31, 'Versatel', 3, 1);
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (32, 'O2', 3, 1);
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (33, 'Telefonica', 3, 1);
update T_CARRIER set TEXT='BT' where id=10;
commit;

-- weitere Carrier-Contacts anlegen
--insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
--	values (3, 12, 'TK NL Weilheim', 'MSA1', '0941/707-5510', '0941/707-5519');
--insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
--	values (4, 12, 'TK NL Bamberg', 'NKZ', '0951/8858-78', '0951/8858-96');
--insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
--	values (5, 12, 'TK NL Regensburg', '???', '0941/???????', '0941/???????');

-- BT Contacts
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (10, 10, 'Hotline', '', '0800/1009882-5210', '069/9999-60967');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (11, 10, 'Hotline Sprachdienste', '', '0800/1009882-5023', '069/9999-6023');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (12, 10, 'Hotline Datendienste', '', '0800/1009882-5212', '069/9999-6212');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (13, 10, 'Hotline Internetdienste', '', '0800/1009882-5313', '069/9999-6313');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (14, 10, 'Hotline Bandbreitendienste', '', '0800/1009882-5412', '069/9999-6412');	
-- Colt Contacts
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (15, 29, 'Hotline', '', '0800/1822233', '');	
-- EWE-Reutte
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (16, 13, 'Netzleitstelle', '', '+43-5672607-236', '');
-- Lamdanet
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (17, 3, 'NMC', '', '0511/84881-444', '0511/8488-1449');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (18, 3, 'Zugang', '', '0511/84881-440', '0511/8488-1449');
-- LEW Telnet
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (19, 16, 'Hotline', '', '0821/328-1444', '0821/328-1450');
-- O2
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (20, 32, 'NMC N�rnberg', '', '0911/6896-3322', '');
-- QSC
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (21, 30, 'Hotline', '', '0221/6698-880', '0221/6698-477');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (22, 30, 'Hotline Celox', '', '0228/32968-289', '0228/32968-290');
-- STAWA
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (23, 20, 'Hotline', '', '0821/324-117', '0821/324-8405');
-- SWU
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (24, 22, 'Hotline', '', '0731/60000', '0731/1666-1709');
-- Telefonica
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (25, 33, 'Hotline', '', '0800/1237718', '');
-- Versatel
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (26, 31, 'NBZ Netzbetriebsraum', '', '0201/86338-012', '0201/8633-900');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (27, 31, 'NMC', '', '0800/8230823', '');
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (28, 31, 'NBZ', '', '0201/86338-152', '');
			
	
-- weitere Carrier-Kennungen anlegen
insert into T_CARRIER_KENNUNG (ID, CARRIER_ID, BEZEICHNUNG, KUNDE_NR, NAME, STRASSE, PLZ, ORT) 
	values (3, 12, 'NefKom', 5920312440, 'NefKom Telekommunikations GmbH', 'Spittlertorgraben 13', '90429', 'N�rnberg');
insert into T_CARRIER_KENNUNG (ID, CARRIER_ID, BEZEICHNUNG, KUNDE_NR, NAME, STRASSE, PLZ, ORT) 
	values (4, 12, 'M-net M�nchen', 5920312290, 'M-net Telekommunikations GmbH', 'Emmy-Noether-Str. 2', '80992', 'M�nchen');
insert into T_CARRIER_KENNUNG (ID, CARRIER_ID, BEZEICHNUNG, KUNDE_NR, NAME, STRASSE, PLZ, ORT) 
	values (5, 12, 'M-net N�rnberg', 5920312290, 'NefKom Telekommunikations GmbH NL N�rnberg', 'Spittlertorgraben 13', '90429', 'N�rnberg');
update T_CARRIER_KENNUNG set BEZEICHNUNG='M-net Augsburg' where ID=2;
update T_CARRIER_KENNUNG set BEZEICHNUNG='AugustaKom' where ID=1;

-- Mappings eintragen
-- DTAG - TNL S�d MSA 3 - M-net 
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (1, 12, 1, 4);
-- DTAG - NL S�d MSA 3 - AKOM
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (2, 12, 1, 1);
-- DTAG - NL S�d MSA 3 - M-net NL AGB
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (3, 12, 1, 2);	
-- DTAG - TNL Stuttgart QNM-MNS - AKOM
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (4, 12, 2, 1);
-- DTAG - TNL Stuttgart QNM-MNS - M-net NL AGB
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (5, 12, 2, 2);	
-- Colt
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (15, 29, 15, null);	

-- BT (Varianten)
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (16, 10, 10, null);	
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (17, 10, 11, null);	
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (18, 10, 12, null);	
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (19, 10, 13, null);	
-- Colt
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (20, 10, 14, null);	
-- EW Reutte
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (21, 13, 16, null);		
-- Lamdanet
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (22, 3, 17, null);		
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (23, 3, 18, null);				
-- LEWTelnet
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (24, 16, 19, null);
-- O2
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (25, 32, 20, null);
-- QSC
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (26, 30, 21, null);
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (27, 30, 22, null);
-- STAWA
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (28, 20, 23, null);	
-- SWU
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (29, 22, 24, null);
-- Telefonica
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (30, 33, 25, null);
-- Versatel
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (31, 31, 26, null);	
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (32, 31, 27, null);	
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (33, 31, 28, null);	

	
	
--
-- Contacts aus MUC	
--
	
-- ARCOR
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (29, 6, 'St�rungsannahme 24h', '', '06196/522866', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (34, 6, 29, null);		
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (30, 6, 'NOC', '', '06196/587-440', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (35, 6, 30, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (31, 6, 'TAL / Endkundenser.', '', '', '06196/523-933');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (36, 6, 31, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (32, 6, 'Portierung', '', '0800/1072013', '0800/5052155');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (37, 6, 32, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (33, 6, 'ICAs / NMC Hotline', '', '06196/523-932', '06196/522-866');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (38, 6, 33, null);	
	

-- Vodafone
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (34, 'Vodafone', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (34, 34, 'Portierung', '', '0172/12228', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (39, 34, 34, null);	


-- BT
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (35, 10, 'TAL / Endkundenser.', '', '069/9999 650 60', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (40, 10, 35, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (36, 10, 'Portierung', '', '03633845040', '03633845041');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (41, 10, 36, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (37, 10, 'ICAs / NMC Hotline', '', '069/9999 650 60', '0180/3329662');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (42, 10, 37, null);	


-- O2
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (38, 32, 'TAL / Endkundenser.', '', '0911/6891-1212', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (43, 32, 38, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (39, 32, 'Portierung', '', '', '089/954579008');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (44, 32, 39, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (40, 32, 'ICAs / NMC Hotline', '', '089/2442-8400', '089/2442-8490');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (45, 32, 40, null);	

	
-- Colt
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (41, 29, 'TAL / Endkundenser.', '', '069/95958-541', '069/95958-330');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (46, 29, 41, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (42, 29, 'ICAs / NMC Hotline', '', '069/95958-541', '069/56606-6740');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (47, 29, 42, null);	


-- eplus
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (35, 'eplus', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (43, 35, 'Portierung', '', '', '02102/5161990');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (48, 35, 43, null);	


-- Hansenet
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (36, 'Hansenet', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (44, 36, 'TAL / Endkundenser.', '', '', '040/23726-3396');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (49, 36, 44, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (45, 36, 'Portierung', '', '', '040/23726-3333');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (50, 36, 45, null);	
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (46, 36, 'Portierung (2)', '', '', '040/23726-3852');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (51, 36, 46, null);


-- Verizon
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (47, 27, 'ICAs / NMC Hotline', '', '0800/880-1088', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (52, 27, 47, null);


-- Kabeldeutschland
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (37, 'Kabeldeutschland', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (48, 37, 'TAL / Endkundenser.', '', '0800/5566099', '0461/50551529');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (53, 37, 48, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (49, 37, 'Portierung', '', '0346054412425', '0346054412424');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (54, 37, 49, null);


-- MrNet
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (38, 'MrNet', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (50, 38, 'Portierung', '', '', '0461/66280-509');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (55, 38, 50, null);


-- QSC
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (51, 30, 'TAL / Endkundenser.', '', '0221/2922-292', '0221/2922-291');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (56, 30, 51, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (52, 30, 'Portierung', '', '', '0221/2922-291');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (57, 30, 52, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (53, 30, 'ICAs / NMC Hotline', '', '0221/2922-292', '0221/2922-291');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (58, 30, 53, null);


-- Telefonica
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (54, 33, 'ICAs / NMC Hotline', '', '05246/801701', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (59, 33, 54, null);


-- TeliaSonera
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (39, 'TeliaSonera', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (55, 39, 'ICAs / NMC Hotline', '', '+46 771/191170', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (60, 39, 55, null);


-- TelekomAustria
insert into T_CARRIER (ID, TEXT, ORDER_NO, CB_NOTWENDIG) values (40, 'Telekom Austria', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (56, 40, 'ICAs / NMC Hotline', '', '+43 1799/4000', '');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (61, 40, 56, null);


-- Versatel
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (57, 31, 'TAL / Endkundenser.', '', '', '0201/8633669');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (62, 31, 57, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (58, 31, 'Portierung', '', '0201/4269366', '0201/4269329');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (63, 31, 58, null);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (59, 31, 'ICAs / NMC Hotline', '', '0201/86338152', '0201/4269329');
insert into T_CARRIER_MAPPING (ID, CARRIER_ID, CARRIER_CONTACT_ID, CARRIER_KENNUNG_ID) 
	values (64, 31, 59, null);


-- Com-In
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (41, 'Com-In', 'COM-IN Telekommunikations GmbH', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (65, 41, 'Hotline', '', '0841�8046-00', '0841�8046-19');

-- Interoute
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (42, 'Interoute', 'Interoute Deutschland GmbH', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (66, 42, 'Hotline', '', '00800 4683 7681', '+420 225 352 699');

-- KPN
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (43, 'KPN', 'KPN Qwest Assets Germany GmbH', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (67, 43, 'Hotline', '', '+31-30-2386-777', '+31-30-2386-530');

-- Level 3
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (44, 'Level 3', 'Level 3 Communications GmbH', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (68, 44, 'Hotline', '', '069-5060-8785', '069-5060-8201');

-- R-Kom
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (45, 'R-Kom', 'R-KOM Telekommunikations GmbH \& Co. KG', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (69, 45, 'Hotline', '', '0941-60822-66', '0941-60822-60');

-- Telepark Passau
insert into T_CARRIER (ID, TEXT, COMPANY_NAME, ORDER_NO, CB_NOTWENDIG) values (46, 'Telepark Passau', 'Telepark Passau GmbH', 3, 1);
insert into T_CARRIER_CONTACT (ID, CARRIER_ID, BRANCH_OFFICE, RESSORT, FAULT_CLEARING_PHONE, FAULT_CLEARING_FAX) 
	values (70, 46, 'Hotline', '', '0851/560-225', '0851/560-243');




--
-- Erweiterung um offiziellen Carrier-Namen (Firmenname)
--

alter table T_CARRIER add COMPANY_NAME VARCHAR2(50);

set escape \
update T_CARRIER set COMPANY_NAME='Arcor AG \& Co.' where ID=6;
update T_CARRIER set COMPANY_NAME='BT (Germany) GmbH \& Co. OHG' where ID=10;
update T_CARRIER set COMPANY_NAME='COLT TELEKOM GmbH' where ID=29;
update T_CARRIER set COMPANY_NAME='Deutsche Telekom AG' where ID=12;
update T_CARRIER set COMPANY_NAME='E-Plus Service GmbH \& Co.KG' where ID=35;
update T_CARRIER set COMPANY_NAME='Elektrizit�tswerke Reutte GmbH und Co KG' where ID=13;
update T_CARRIER set COMPANY_NAME='HanseNet Telekommunikations GmbH' where ID=36;
update T_CARRIER set COMPANY_NAME='Kabel Deutschland GmbH' where ID=37;
update T_CARRIER set COMPANY_NAME='LambdaNet Communications Deutschland AG' where ID=3;
update T_CARRIER set COMPANY_NAME='LEWTelNet GmbH' where ID=16;
update T_CARRIER set COMPANY_NAME='M-net Telekommunikations GmbH' where ID=17;
update T_CARRIER set COMPANY_NAME='MR.NET services GmbH \& Co. KG' where ID=38;
update T_CARRIER set COMPANY_NAME='O2 (Germany) GmbH \& Co. OHG' where ID=32;
update T_CARRIER set COMPANY_NAME='QSC AG' where ID=30;
update T_CARRIER set COMPANY_NAME='Stadtwerke Augsburg' where ID=20;
update T_CARRIER set COMPANY_NAME='SWU TeleNet GmbH' where ID=22;
update T_CARRIER set COMPANY_NAME='Telefonica Deutschland GmbH' where ID=33;
update T_CARRIER set COMPANY_NAME='Telekom Austria TA AG' where ID=40;
update T_CARRIER set COMPANY_NAME='teliasonera' where ID=39;
update T_CARRIER set COMPANY_NAME='Verizon Deutschalnd GmbH' where ID=27;
update T_CARRIER set COMPANY_NAME='Versatel S�d GmbH' where ID=31;
update T_CARRIER set COMPANY_NAME='Vodafone D2 GmbH' where ID=34;

commit;

