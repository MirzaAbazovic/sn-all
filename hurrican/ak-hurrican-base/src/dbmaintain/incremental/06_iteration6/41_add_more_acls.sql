--
-- Fuegt die restlichen Endgeraet ACL Daten in Hurrican ein
-- 6.11.2009

Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP, VERSION)
 Values
   (315, 'LRW119 (10.240.17.0/24)', 'ACL_LRW119', 1);
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP, VERSION)
 Values
   (316, 'AAB001 (192.168.124.0/24)', 'ACL_AAB001', 1);
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP, VERSION)
 Values
   (317, 'EBG001 (10.240.6.0/24)', 'ACL_EBG001', 1);
COMMIT;
