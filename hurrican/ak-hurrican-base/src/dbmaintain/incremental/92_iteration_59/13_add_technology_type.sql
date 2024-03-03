alter table t_reference add WBCI_CODE VARCHAR2(25);
update t_reference set WBCI_CODE='017 Kupfer' where ID in (11000, 11001, 11003, 11009, 11010, 11013);
update t_reference set WBCI_CODE='013 FTTB' where ID in (11002);
update t_reference set WBCI_CODE='014 FTTH' where ID in (11011, 11008, 11012);
update t_reference set WBCI_CODE='016 Koax' where ID in (11007);
update t_reference set WBCI_CODE='021 Sonstiges' where ID in (11014, 11015, 11016, 11004, 11005, 11006);


