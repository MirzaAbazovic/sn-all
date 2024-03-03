-- Produktgruppe Connect
--    IP-Pool: DirectAccess (2)
--    Verwendungszweck: offen und editierbar
--    Größe der Netzmaske V4: 32
--    Größe der Netzmaske V6: 48
--    Größe der Netzmaske: editierbar

update T_PRODUKT set IP_POOL=2, IP_PURPOSE_V4_EDITABLE='1', IP_NETMASK_SIZE_V4=32,
        IP_NETMASK_SIZE_V6=48, IP_NETMASK_SIZE_EDITABLE='1'
    where PRODUKTGRUPPE_ID=2;

-- Produktgruppen ADSL, ADSLPlus, SDSL, Maxi, PremiumCall
--    IP-Pool: XDSL (1)
--    Verwendungszweck: Transfernetz und nicht editierbar
--    Größe der Netzmaske V4: 32
--    Größe der Netzmaske V6: 48
--    Größe der Netzmaske: editierbar

update T_PRODUKT set IP_POOL=1, IP_PURPOSE_V4=22371, IP_PURPOSE_V4_EDITABLE='0', IP_NETMASK_SIZE_V4=32,
        IP_NETMASK_SIZE_V6=48, IP_NETMASK_SIZE_EDITABLE='1'
    where PRODUKTGRUPPE_ID in (3,4,16,17,18);