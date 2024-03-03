--
-- Update-Script fuer die Aenderung fuer Metro-Ethernet
-- (VPLS-ID auf VPN-Konfiguration)
--

alter table T_VPN_KONF add column VPLS_ID VARCHAR(32) after PHYS_AUFTRAG_ID;


alter table T_VPN drop column VPN_TIMESTAMP;


