-- Neue technische Leistung anlegen
insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_MISC__NO, EXTERN_LEISTUNG__NO, TYP, 
    LONG_VALUE, STR_VALUE, PARAMETER, PROD_NAME_STR, DESCRIPTION, 
    DISPO, EWSD, SDH, IPS, SCT, 
    CHECK_QUANTITY, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION)
 Values
   (55, 'DHCPv6-PD', NULL, NULL, 'ONL_IP_OPTION', 
    NULL, NULL, NULL, ' ', NULL, 
    '0', '0', '0', '0', '0', 
    NULL, '1', TO_DATE('12/19/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0);

-- Command registrieren
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES (1012, 'add.ipv6dhcp-pd',
      'de.augustakom.hurrican.service.cc.impl.command.leistung.IpV6PrefixReservierenCommand',
      'LS_ZUGANG', 'Reserviert eine IPv6 DHCP-PD Adresse falls noch keine existiert.', 0);

-- Command an Tech-Leistung binden
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1012, 55, 'de.augustakom.hurrican.model.cc.TechLeistung', null, 0);
