-- auf TAIFUN-DB ausfuehren, vor Script!
-- update auftragpos set price=0 where service_elem__no in (20112,20116,20282,20286,22042,22043) 
-- 	and (charge_to is null or charge_to>='2007-05-01') and price<>0;

--
-- Preis-Korrekturen fuer aktuell falsche Positionen
--

-- Leistung 20294 - G/Maxi DSL 6000 Herbst-Aktion 2006
-- select ap.order__no, ap.price from auftragpos ap
--   inner join auftrag a on ap.order__no=a.auftrag__no
--   where a.hist_status<>'UNG' and a.hist_last='1'
--   and (ap.charge_to is null or ap.charge_to>='2007-05-01')
--   and ap.service_elem__no=20294 and ap.price<-6.72;
-- update auftragpos set price=-6.72 where item_no in (
--   select ap.item_no from auftragpos ap
--   inner join auftrag a on ap.order__no=a.auftrag__no
--   where a.hist_status<>'UNG' and a.hist_last='1'
--   and (ap.charge_to is null or ap.charge_to>='2007-05-01')
--   and ap.service_elem__no=20294 and ap.price<-6.72);





--
-- Leistungsaenderungen fuer die Maxi-Aktion 2007-05
-- (Preisreduzierung MaxiKomplett, Abschaffung FunFlat etc.)
--

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (10, 'Preisreduzierung <1662001: Maxi Kom 20>', '2007-04-30', '2007-05-01', 
    37650, -36.89, -33.52, '-33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (11, 'Preisreduzierung <1661011: Maxi Kom 10>', '2007-04-30', '2007-05-01', 
    37651, -33.52, -31.84, '-31,84', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (12, 'Preisreduzierung <1666001: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37652, -40.25, -36.05, '-36,05', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (13, 'Preisreduzierung <1661010: Company Plu>', '2007-04-30', '2007-05-01', 
    37653, -1.0, -0.96, '-0,96', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (14, 'Preisreduzierung <1661609: Company Plu>', '2007-04-30', '2007-05-01', 
    37654, -1.26, -1.18, '-1,18', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (15, 'Preisreduzierung <1662010: Company Plu>', '2007-04-30', '2007-05-01', 
    37655, -1.1, -1.01, '-1,01', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (16, 'Preisreduzierung <1663010: Company Plu>', '2007-04-30', '2007-05-01', 
    37656, -1.10, -1.01, '-1,01', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (17, 'Preisreduzierung <1666010: Company Plu>', '2007-04-30', '2007-05-01', 
    37657, -1.21, -1.08, '-1,08', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (18, 'Preisreduzierung <G/Maxi DSL 16000 Herbst-Aktion 2006>', '2007-04-30', '2007-05-01', 
    37660, -8.4, -5.89, '-5,89', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (19, 'Preisreduzierung <G/Maxi DSL 3000 Herbst-Aktion 2006>', '2007-04-30', '2007-05-01', 
    37661, -3.36, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (20, 'Preisreduzierung <G/Maxi DSL 6000 Herbst-Aktion 2006>', '2007-04-30', '2007-05-01', 
    37662, -6.72, -2.53, '-2,53', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (21, 'Preisreduzierung <G/Maxi DSL 16000 Vorteils-Aktion>', '2007-04-30', '2007-05-01', 
    37663, -8.40, -5.89, '-5,89', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (22, 'Preisreduzierung <G/Maxi DSL 3000 Vorteils-Aktion>', '2007-04-30', '2007-05-01', 
    37664, -3.36, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (23, 'Preisreduzierung <G/Maxi DSL 6000 Vorteils-Aktion>', '2007-04-30', '2007-05-01', 
    37665, -6.72, -2.53, '-2,53', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (24, 'Preisreduzierung <1651130: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37666, 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (25, 'Preisreduzierung <1651131: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37667, -8.53, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (26, 'Preisreduzierung <1651041: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37669, -7.71, -7.52, '-7,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (27, 'Preisreduzierung <1651135: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37670, 8.06, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (28, 'Preisreduzierung <1651140: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37671, 11.68, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (29, 'Preisreduzierung <1651142: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37672, -11.68, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (30, 'Preisreduzierung <1651145: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37673, 11.32, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (31, 'Preisreduzierung <1651030: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37674, 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (32, 'Preisreduzierung <1661605: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37675, -8.4, -5.89, '-5,89', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (33, 'Preisreduzierung <1651640: Maxi MAX Fu>', '2007-04-30', '2007-05-01', 
    37676, 14.2, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (34, 'Preisreduzierung <1640640: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37677, 16.72, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (35, 'Preisreduzierung <1640630: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37678, 12.52, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (36, 'Preisreduzierung <1640670: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    37679, 20.92, 14.2, '14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (37, 'Preisreduzierung <1652010: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37681, 12.52, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (38, 'Preisreduzierung <1652020: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37682, 11.68, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (39, 'Preisreduzierung <1652030: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37683, 9.15, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (40, 'Preisreduzierung <1652000: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37684, 16.72, 13.36, '13,36', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (41, 'Preisreduzierung <Ü/Maxi DSL 1500 .>', '2007-04-30', '2007-05-01', 
    37685, '.', 33.52, 31.84, '31,84', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (42, 'Preisreduzierung <Ü/Maxi DSL 16000>', '2007-04-30', '2007-05-01', 
    37686, '.', 41.93, 39.41, '39,41', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (43, 'Preisreduzierung <Ü/Maxi DSL 16000 AKW>', '2007-04-30', '2007-05-01', 
    37686, 'AKW', 8.40, 7.57, '7,57', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (44, 'Preisreduzierung <Ü/Maxi DSL 16000 AÜW>', '2007-04-30', '2007-05-01', 
    37686, 'AÜW', 8.40, 7.57, '7,57', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (45, 'Preisreduzierung <Ü/Maxi DSL 16000 EVO>', '2007-04-30', '2007-05-01', 
    37686, 'EVO', 8.40, 7.57, '7,57', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (46, 'Preisreduzierung <Ü/Maxi DSL 16000 LEW>', '2007-04-30', '2007-05-01', 
    37686, 'LEW', 8.40, 7.57, '7,57', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (47, 'Preisreduzierung <Ü/Maxi DSL 16000 STAWA>', '2007-04-30', '2007-05-01', 
    37686, 'STAWA', 8.40, 7.57, '7,57', 0);


insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (48, 'Preisreduzierung <Ü/Maxi DSL 3000 .>', '2007-04-30', '2007-05-01', 
    37687, '.', 36.89, 33.52, '33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (49, 'Preisreduzierung <Ü/Maxi DSL 3000 AKW>', '2007-04-30', '2007-05-01', 
    37687, 'AKW', 3.36, 1.68, '1,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (147, 'Preisreduzierung <Ü/Maxi DSL 3000 AÜW>', '2007-04-30', '2007-05-01', 
    37687, 'AÜW', 3.36, 1.68, '1,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (148, 'Preisreduzierung <Ü/Maxi DSL 3000 EVO>', '2007-04-30', '2007-05-01', 
    37687, 'EVO', 3.36, 1.68, '1,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (149, 'Preisreduzierung <Ü/Maxi DSL 3000 LEW>', '2007-04-30', '2007-05-01', 
    37687, 'LEW', 3.36, 1.68, '1,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (150, 'Preisreduzierung <Ü/Maxi DSL 3000 STAWA>', '2007-04-30', '2007-05-01', 
    37687, 'STAWA', 3.36, 1.68, '1,68', 0);


insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (50, 'Preisreduzierung <Ü/Maxi DSL 6000 .>', '2007-04-30', '2007-05-01', 
    37688, '.', 40.25, 36.05, '36,05', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (51, 'Preisreduzierung <Ü/Maxi DSL 6000 AKW>', '2007-04-30', '2007-05-01', 
    37688, 'AKW', 6.72, 4.21, '4,21', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (52, 'Preisreduzierung <Ü/Maxi DSL 6000 AÜW>', '2007-04-30', '2007-05-01', 
    37688, 'AÜW', 6.72, 4.21, '4,21', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (53, 'Preisreduzierung <Ü/Maxi DSL 6000 EVO>', '2007-04-30', '2007-05-01', 
    37688, 'EVO', 6.72, 4.21, '4,21', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (54, 'Preisreduzierung <Ü/Maxi DSL 6000 LEW>', '2007-04-30', '2007-05-01', 
    37688, 'LEW', 6.72, 4.21, '4,21', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (55, 'Preisreduzierung <Ü/Maxi DSL 6000 STAWA>', '2007-04-30', '2007-05-01', 
    37688, 'STAWA', 6.72, 4.21, '4,21', 0);


insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (56, 'Preisreduzierung <1661600: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37698, null, 41.93, 39.41, '39,41', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (57, 'Preisreduzierung <1666000: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37699, null, 41.09, 36.05, '36,05', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (58, 'Preisreduzierung <1666010: Company Plu>', '2007-04-30', '2007-05-01', 
    37700, null, -1.23, -1.08, '-1,08', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (59, 'Preisreduzierung <1651132: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37701, null, -8.31, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (60, 'Preisreduzierung <1651033: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37703, null, -13.35, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (61, 'Preisreduzierung <1651032: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37704, null, -13.35, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (62, 'Preisreduzierung <1651142: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37705, null, -11.68, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (63, 'Preisreduzierung <1651031: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37706, null, 12.95, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (64, 'Preisreduzierung <1651145: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37707, null, 11.33, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (65, 'Preisreduzierung <1651135: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37708, null, 8.06, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (66, 'Preisreduzierung <1651642: Maxi MAX Fl>', '2007-04-30', '2007-05-01', 
    37709, null, -14.2, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (67, 'Preisreduzierung <1651141: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37710, null, -11.68, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (68, 'Preisreduzierung <1651641: Maxi MAX Fl>', '2007-04-30', '2007-05-01', 
    37711, null, 13.76, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (69, 'Preisreduzierung <1651131: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37712, null, -8.31, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (70, 'Preisreduzierung <1651411: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37713, null, -8.06, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (71, 'Preisreduzierung <Ü/Maxi 2000 Tarif Flatrate>', '2007-04-30', '2007-05-01', 
    37716, 'Flatrate', 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (72, 'Aenderung von <2000 FunFlat auf 2000 Flatrate>', '2007-04-30', '2007-05-01', 
    37716, 'Fun Flat', 20277, 'Flatrate', 6.63, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (73, 'Preisreduzierung <Ü/Maxi 3000 Tarif Flatrate>', '2007-04-30', '2007-05-01', 
    37717, 'Flatrate', 11.68, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (74, 'Aenderung von <3000 FunFlat auf 3000 Flatrate>', '2007-04-30', '2007-05-01', 
    37717, 'Fun Flat', 20278, 'Flatrate', 9.15, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (75, 'Preisreduzierung <Ü/Maxi 6000 Tarif Flatrate>', '2007-04-30', '2007-05-01', 
    37718, 'Flatrate', 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (76, 'Aenderung von <6000 FunFlat auf 6000 Flatrate>', '2007-04-30', '2007-05-01', 
    37718, 'Fun Flat', 20279, 'Flatrate', 10.84, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (77, 'Preisreduzierung <Ü/Maxi MAX Tarif Flatrate>', '2007-04-30', '2007-05-01', 
    37719, 'Flatrate', 14.2, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (78, 'Aenderung von <MAX FunFlat auf MAX Flatrate>', '2007-04-30', '2007-05-01', 
    37719, 'Fun Flat', 20280, 'Flatrate', 11.68, 6.63, '6,63', 0);



insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (79, 'Preisreduzierung <Ü/TopCall Flatrate analog>', '2007-04-30', '2007-05-01', 
    37720, 'analog', 16.72, 13.36, '13,36', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (80, 'Preisreduzierung <Ü/TopCall Flatrate DSL 2000>', '2007-04-30', '2007-05-01', 
    37720, 'DSL 2000', 12.52, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (81, 'Preisreduzierung <Ü/TopCall Flatrate DSL 3000>', '2007-04-30', '2007-05-01', 
    37720, 'DSL 3000', 11.68, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (82, 'Preisreduzierung <Ü/TopCall Flatrate DSL 6000>', '2007-04-30', '2007-05-01', 
    37720, 'DSL 6000', 9.15, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (83, 'Preisreduzierung <Ü/TopCall Flatrate ISDN>', '2007-04-30', '2007-05-01', 
    37720, 'ISDN', 16.72, 13.36, '13,36', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (84, 'Preisreduzierung <Ü/TopCall Flatrate Maxi MAX>', '2007-04-30', '2007-05-01', 
    37720, 'Maxi MAX', 5.79, 5.79, '5,79', 0);


insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (85, 'Preisreduzierung <1661609: Company Plu>', '2007-04-30', '2007-05-01', 
    37728, null, -1.25, -1.18, '-1,18', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (86, 'Preisreduzierung <1662010: Company Plu>', '2007-04-30', '2007-05-01', 
    37729, null, -1.1, -1.01, '-1,01', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (87, 'Preisreduzierung <1666010: Company Plu>', '2007-04-30', '2007-05-01', 
    37730, null, -1.22, -1.08, '-1,08', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (88, 'Preisreduzierung <1650150: Maxi DSL 12>', '2007-04-30', '2007-05-01', 
    37731, null, 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (89, 'Preisreduzierung <1650155: Maxi DSL 12>', '2007-04-30', '2007-05-01', 
    37732, null, 8.06, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (90, 'Preisreduzierung <1651130: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37733, null, 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (91, 'Preisreduzierung <1651132: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37734, null, -8.31, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (92, 'Preisreduzierung <1651136: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37735, null, -8.06, -6.43, '-6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (93, 'Preisreduzierung <1651135: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37736, null, 8.06, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (94, 'Preisreduzierung <1650160: Maxi DSL 24>', '2007-04-30', '2007-05-01', 
    37737, null, 11.68, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (95, 'Preisreduzierung <1651140: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37738, null, 11.68, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (96, 'Preisreduzierung <1651142: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37739, null, -11.68, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (97, 'Preisreduzierung <1651145: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37740, null, 11.33, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (98, 'Preisreduzierung <1650170: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37741, null, 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (99, 'Preisreduzierung <1651030: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37742, null, 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (100, 'Preisreduzierung <1651035: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37743, null, -12.95, -6.43, '-6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (101, 'Preisreduzierung <1651033: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37744, null, -13.36, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (102, 'Preisreduzierung <1651031: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37745, null, 12.95, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (103, 'Preisreduzierung <1661000: Maxi Kom 10>', '2007-04-30', '2007-05-01', 
    37746, null, 33.52, 31.84, '31,84', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (104, 'Preisreduzierung <1661600: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37747, null, 41.93, 39.41, '39,41', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (105, 'Preisreduzierung <1661605: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37748, null, -8.4, -5.89, '-5,89', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (106, 'Preisreduzierung <1663000: Maxi Kom 30>', '2007-04-30', '2007-05-01', 
    37749, null, 39.41, 33.52, '33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (107, 'Preisreduzierung <1663005: Maxi Kom 30>', '2007-04-30', '2007-05-01', 
    37750, null, -5.88, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (108, 'Preisreduzierung <1666000: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37751, null, 41.09, 36.05, '36,05', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (109, 'Preisreduzierung <1666005: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37752, null, -7.56, -2.53, '-2,53', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (110, 'Preisreduzierung <1651640: Maxi MAX Fu>', '2007-04-30', '2007-05-01', 
    37753, null, 14.2, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (111, 'Preisreduzierung <1640640: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37754, null, 16.72, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (112, 'Preisreduzierung <1640642: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37755, null, -16.72, -11.68, '-11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (113, 'Preisreduzierung <1640670: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    37756, null, 20.92, 14.2, '14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (114, 'Preisreduzierung <1640671: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    37757, null, -20.92, -14.2, '-14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (115, 'Preisreduzierung <1652010: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37758, null, 12.52, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (116, 'Preisreduzierung <1652020: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37759, null, 11.68, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (117, 'Preisreduzierung <1652030: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37760, null, 9.15, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (118, 'Preisreduzierung <1661609: Company Plu>', '2007-04-30', '2007-05-01', 
    37761, null, -1.26, -1.18, '-1,18', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (119, 'Preisreduzierung <1663010: Company Plu>', '2007-04-30', '2007-05-01', 
    37762, null, -1.17, -1.01, '-1,01', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (120, 'Preisreduzierung <1666010: Company Plu>', '2007-04-30', '2007-05-01', 
    37763, null, -1.22, -1.08, '-1,08', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (121, 'Preisreduzierung <1650150: Maxi DSL 12>', '2007-04-30', '2007-05-01', 
    37764, null, 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (122, 'Preisreduzierung <1651130: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37765, null, 8.31, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (123, 'Preisreduzierung <1651132: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    37766, null, -8.31, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (124, 'Preisreduzierung <1650160: Maxi DSL 24>', '2007-04-30', '2007-05-01', 
    37767, null, 11.68, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (125, 'Preisreduzierung <1651140: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37768, null, 11.68, 6.63, '6,63', 0);


insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (126, 'Preisreduzierung <1651142: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    37769, null, -11.68, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (127, 'Preisreduzierung <1650170: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37770, null, 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (128, 'Preisreduzierung <1651030: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37771, null, 13.36, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (129, 'Preisreduzierung <1651033: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37772, null, -13.36, -6.63, '-6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (130, 'Preisreduzierung <1651031: Maxi DSL 60>', '2007-04-30', '2007-05-01', 
    37773, null, 12.95, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (131, 'Preisreduzierung <1661000: Maxi Kom 10>', '2007-04-30', '2007-05-01', 
    37774, null, 33.52, 31.84, '31,84', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (132, 'Preisreduzierung <1661600: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37775, null, 41.93, 39.41, '39,41', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (133, 'Preisreduzierung <1661605: Maxi Kom 16>', '2007-04-30', '2007-05-01', 
    37776, null, -8.4, -5.89, '-5,89', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (134, 'Preisreduzierung <1663000: Maxi Kom 30>', '2007-04-30', '2007-05-01', 
    37777, null, 39.41, 33.52, '33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (135, 'Preisreduzierung <1663005: Maxi Kom 30>', '2007-04-30', '2007-05-01', 
    37778, null, -5.88, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (136, 'Preisreduzierung <1666000: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37779, null, 41.09, 36.05, '36,05', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (137, 'Preisreduzierung <1666005: Maxi Kom 60>', '2007-04-30', '2007-05-01', 
    37780, null, -7.56, -2.53, '-2,53', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (138, 'Preisreduzierung <1651640: Maxi MAX Fu>', '2007-04-30', '2007-05-01', 
    37781, null, 14.2, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (139, 'Preisreduzierung <1640640: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37782, null, 16.72, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (140, 'Preisreduzierung <1640641: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37783, null, 12.86, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (141, 'Preisreduzierung <1640642: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37784, null, -16.72, -11.68, '-11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (142, 'Preisreduzierung <1640670: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    37785, null, 20.92, 14.2, '14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (143, 'Preisreduzierung <1640671: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    37786, null, -20.92, -14.2, '-14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (144, 'Preisreduzierung <1652010: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37787, null, 12.52, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (145, 'Preisreduzierung <1652020: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37788, null, 11.68, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (146, 'Preisreduzierung <1652030: Telefon-Fla>', '2007-04-30', '2007-05-01', 
    37789, null, 9.15, 8.31, '8,31', 0);

--+++++++++++++++++++++++

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (151, 'Aenderung von <1650120: Maxi DSL 12>', '2007-04-30', '2007-05-01', 
    32533, null, 2120, null, 6.63, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (152, 'Aenderung von <1650130: Maxi DSL 24>', '2007-04-30', '2007-05-01', 
    32384, null, 21811, null, 9.15, 6.63, '6,63', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (153, 'Aenderung von <1662000: Maxi Kom 20>', '2007-04-30', '2007-05-01', 
    32378, null, 21907, null, 36.89, 33.52, '33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (154, 'Aenderung von <1640630: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    32318, null, 21689, null, 12.52, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (155, 'Aenderung von <1640660: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    32457, null, 21717, null, 16.72, 14.2, '14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (156, 'Aenderung von <1662000: Maxi Kom 20>', '2007-04-30', '2007-05-01', 
    32036, null, 21503, null, 36.89, 33.52, '33,52', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (157, 'Aenderung von <1662005: Maxi Kom 20>', '2007-04-30', '2007-05-01', 
    32173, null, 21504, null, -3.36, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (158, 'Aenderung von <1640630: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    32031, null, 21403, null, 12.52, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (159, 'Aenderung von <1640660: MaxiDSL 320>', '2007-04-30', '2007-05-01', 
    32164, null, 21454, null, 16.72, 14.2, '14,2', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (160, 'Aenderung von <1651115: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    36072, null, 23694, null, 8.88, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (161, 'Aenderung von <1651105: Maxi DSL 20>', '2007-04-30', '2007-05-01', 
    36163, null, 23762, null, 6.43, 6.43, '6,43', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (162, 'Aenderung von <1651110: Maxi DSL 30>', '2007-04-30', '2007-05-01', 
    34606, null, 22484, null, 9.15, 6.63, '6,63', 0);


--
-- Volumenleistungen von FunFlat auf Flat umschluesseln
--
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (163, 'Aenderung von <Ü/Vol. Maxi DSL 2000 Fun Flat>', '2007-04-30', '2007-05-01', 
    31186, null, 20111, null, 0, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (164, 'Aenderung von <Ü/Vol. Maxi Max Fun Flat>', '2007-04-30', '2007-05-01', 
    31190, null, 20115, null, 0, 0, '0', 0);
    
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (165, 'Aenderung von <Ü/Vol. Maxi DSL 3000 Fun Flat>', '2007-04-30', '2007-05-01', 
    31306, null, 20281, null, 0, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (166, 'Aenderung von <Ü/Vol. Maxi DSL 6000 Fun Flat>', '2007-04-30', '2007-05-01', 
    31310, null, 20285, null, 0, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (167, 'Aenderung von <Ü/Vol. Maxi Max Fun Flat>', '2007-04-30', '2007-05-01', 
    32550, null, 22044, null, 0, 0, '0', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, LEISTUNG__NO_NEW, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (168, 'Aenderung von <Ü/Vol. Maxi Max Fun Flat>', '2007-04-30', '2007-05-01', 
    32551, null, 22045, null, 0, 0, '0', 0);


-- Korrekturen auf Positionsebene - eigentlich Aenderung zum 01.04.2007 
-- (Berechnung auf Leistungsebene, deshalb nicht so wichtig)
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (169, 'Aenderung von <1640640: MaxiDSL 160>', '2007-04-30', '2007-05-01', 
    37702, null, null, 16.72, 11.68, '11,68', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (170, 'Aenderung von <B/Option M-net flatline>', '2007-04-30', '2007-05-01', 
    37658, null, null, 8.53, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (171, 'Aenderung von <Ü/Option M-net flatline>', '2007-04-30', '2007-05-01', 
    37680, null, null, 17.15, 16.72, '16,72', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, VALUE_NEW, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (172, 'Aenderung von <Ü/Option M-net flatline plus>', '2007-04-30', '2007-05-01', 
    37659, null, null, 4.22, 4.11, '4,11', 0);

--
-- best. Leistungen nochmal modifizieren, da nicht alle Positionen aktuell waren
--
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (173, 'Preisreduzierung <Ü/TopCall Flatrate DSL 2000>', '2007-04-30', '2007-05-01', 
    37720, 'DSL 2000', 12.84, 8.31, '8,31', 0);

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (174, 'Preisreduzierung <Ü/TopCall Flatrate DSL 6000>', '2007-04-30', '2007-05-01', 
    37720, 'DSL 6000', 9.39, 8.31, '8,31', 0);    

insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (175, 'Preisreduzierung <Ü/TopCall Flatrate ISDN>', '2007-04-30', '2007-05-01', 
    37720, 'ISDN', 17.15, 13.36, '13,36', 0);



-- zu beendende Leistungen:
-- LEISTUNG_NO
-- 37072
-- 34216
-- 36165
-- 37669
-- 36327
-- 31186
-- 31190
-- 31306
-- 31310
-- 32550
-- 32551

-- alle "*Fun Flat*" Leistungen (auch aus alten AK-Produkten) beenden - nicht aus MPR, SEK etc.



-- ACHTUNG: Umlaute in Value-Feld müssen noch korrigiert werden!!!

--insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
--	LEISTUNG_NO, VALUE, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
--    values (151, 'Preisreduzierung <>', '2007-04-30', '2007-05-01', 
--    , null, , , '', 0);



    