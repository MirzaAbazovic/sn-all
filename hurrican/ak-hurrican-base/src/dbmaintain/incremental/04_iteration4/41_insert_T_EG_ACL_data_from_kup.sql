--
-- Fuegt die Endgeraet ACL Daten in Hurrican ein
-- 7.10.2009

ALTER TABLE T_EG_ACL
MODIFY(VERSION  NULL);

Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (184, 'LAMV5', 'Cisco 876/851');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (185, 'CCTV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (186, 'CCTV2', 'Thompson STP 610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (187, 'CCTV3', 'Thompson STP 605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (188, 'RAWcisco', 'Cisco SB101/828');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (189, 'RAWV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (190, 'MVIV1', 'Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (191, 'MVIV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (192, 'MVIV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (193, 'MVIV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (194, 'MVIcisco', 'Cisco Router');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (195, 'BEVV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (196, 'BEVV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (197, 'BEVcisco', 'Cisco Router');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (198, 'TRUV1', 'Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (199, 'TRUV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (200, 'TRUV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (201, 'TRUV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (202, 'TRUcisco', 'Cisco Router');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (203, 'IQG002 (10.48.254.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (204, 'CCTcisco', 'Cisco Router');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (205, 'TEW001 (212.29.0.0/23)', 'gilt für alle gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (206, 'ZOP001 (10.192.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (207, 'VON001 (194.126.196.0/24, 80.244.245.0/26)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (208, 'LRF650 (192.168.10.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (209, 'SND100 (10.193.0.0/24)', 'gilt für alle gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (210, 'BEG001 (10.194.0.0/24, 192.168.0.0/16)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (214, 'RUD565 (192.168.253.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (215, 'IPM191 (213.252.0.0/16)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (217, 'DOD001 (10.197.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (221, 'HYP001 (10.252.11.0/24, 15.1.1.0/24, 82.135.16.10/32, 82.135.16.11/32, 82.135.16.168/29)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (222, 'NTP001 (213.183.2.26/32, 213.183.4.37/32, 213.183.0.2/32, 213.183.7.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (224, 'EPV957 (10.198.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (230, 'EKD808 (10.199.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (231, 'LAM189 (10.185.0.0/24, 62.245.226.228/30)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (233, 'IGH001 (62.245.129.0/24, 62.245.191.42/32)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (235, 'SPN001 (Bridgekonfig)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (237, 'CBX001 (62.245.156.96/28, 62.245.235.36/30, 212.18.0.160/30, 10.140.0.0/16, 10.150.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (264, 'KKG002 (10.206.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (266, 'TRA001 (195.24.104.0/24) MTU=1452 !!', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (267, 'EWT904 (10.207.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (268, 'DGE252 (10.209.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (275, 'AIM123 (10.218.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (276, 'KTJ247 (10.217.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (278, 'KDW001 (10.220.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (279, 'KDO001 (10.221.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (280, 'DPO237 (10.216.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (281, 'CAD001 (10.222.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (285, 'BOP029 (10.225.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (288, 'MVV002 (10.229.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (291, 'DKL100 (10.232.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (292, 'ABX001 (10.231.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (293, 'BZD100 (10.233.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (294, 'JSG188 (10.234.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (297, 'HYP002 (10.237.0.0/23)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (298, 'PMH001 (10.238.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (300, 'RHF569 (10.240.1.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (304, 'SPK087 (172.30.0.0/21)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (307, 'SCF069 (10.240.10.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (311, 'DMW159 (10.240.13.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (314, 'BAR002 (Bridgekonfig)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (211, 'WAG001 (62.245.132.0/24, 62.245.191.244/30, 82.135.20.128/28, 192.168.0.0/16)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (216, 'ATA053 (10.196.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (218, 'ISS001 (88.217.209.193/32, 88.217.208.20/32, 10.254.10.0/24, 172.20.0.254/32, 1.1.1.1/32)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (226, 'TRU409 (10.190.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (227, 'INW001 (213.155.0.0 /16)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (229, 'NXO343 (192.168.241.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (236, 'LRZ146 (192.168.0.0/16; 129.187.10.0/24; 129.187.12.0/24; 129.187.15.0/24; 129.187.48.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (238, 'MVV001 (10.201.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (239, 'GBW001 (10.202.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (240, 'OWP001 (10.203.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (261, 'MVZ090 (10.204.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (263, 'ABP248 (10.205.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (269, 'APB323 (10.210.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (271, 'DDG987 (10.214.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (272, 'BYS552 (10.212.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (273, 'ANA246 (10.213.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (277, 'DSP488 (10.219.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (283, 'MUB169 (192.168.245.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (284, 'IKI669 (10.224.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (287, 'LTM001 (10.226.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (299, 'BSB100 (10.240.2.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (301, 'HAG001 (10.239.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (306, 'HUG067 (10.240.9.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (308, 'DSD297 (10.240.12.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (219, 'RBS001 (62.245.196.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (228, 'BMW463 (160.50.192.176/29; 160.50.194.64/26; 10.0.1.0/24; 10.0.4.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (234, 'GTT100 (10.175.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (241, 'BAR001 (80.89.64.0 /24, 194.120.55.0/25)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (242, 'AUM001 (62.245.131.224/27, 62.245.225.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (243, 'BEV001 (10.189.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (244, 'BFZ489 (10.20.1.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (245, 'CCT667 (10.186.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (246, 'CYW142 (212.125.96.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (247, 'DBG001 (10.10.10.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (248, 'FAG100 (212.18.0.172/30, 213.166.226.0/24, 213.166.249.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (249, 'GBN100 (10.181.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (250, 'ICG001 (83.136.130.84/30)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (251, 'IQG003 (10.200.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (252, 'MVI447 (10.170.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (253, 'MXI001 (81.24.66.0/24, 81.24.67.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (254, 'PPL037 (192.168.240.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (255, 'PPR001 (10.165.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (256, 'QSC001 (Bridgekonfig)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (257, 'RAW370 (10.187.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (258, 'SHD001 (10.145.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (259, 'SKI100 (10.160.0.0/24', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (260, 'SSG001 (10.189.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (262, 'AUGUSTA_Personal (172.16.103.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (265, 'AUR222 (172.31.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (270, 'HUA001 (10.211.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (282, 'CIG953(10.223.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (286, 'CSH001 (10.227.0.0 /24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (289, 'GHS100 (10.228.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (290, 'OHG002 (10.230.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (295, 'TMS001 (10.235.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (302, 'LRD001 (172.29.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (303, 'WAL001 (10.240.5.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (305, 'SKF969 (10.240.8.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (310, 'TRU409 (Bridgekonfig)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (312, 'EGN249 (10.240.18.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (212, 'ARA057 (195.69.112.217/32, 195.69.112.224/27, 195.69.113.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (213, 'GNM001 (192.168.1.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (220, 'GIT001 (88.217.209.193; 88.217.208.20; 88.217.210.145; 88.217.210.146)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (223, 'CBY001 (10.140.0.0/24, 212.18.28.96/28)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (225, 'MLC373 (10.155.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (232, 'XAM001 (62.245.191.144/29; 62.245.218.0/25)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (274, 'FCP888 (10.215.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (296, 'IGS050 (10.236.0.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (309, 'ACP002 (10.240.30.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (313, 'SKM001 (10.240.3.0/24)', 'gilt für alle Gerätetypen');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (100, 'ACLSV1', 'Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (1, 'IPM', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (2, 'INWV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (3, 'TEWV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (4, 'BMWV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (5, 'LRZV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (6, 'NTPV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (7, 'GITv1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (8, 'CYWV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (9, 'MXIV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (11, 'ACLS_V2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (12, 'IPMV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (13, 'AUMV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (15, 'ARAV1  (195.69.112.217/32, 195.69.112.224/27) MTU=1452 !!', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (14, 'XAMV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (16, 'NTPV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (17, 'IGHV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (18, 'IGHV2', 'Thomson STP610s ?');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (19, 'QSCV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (20, 'QSCV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (21, 'CBXV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (22, 'BARV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (23, 'BARV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (24, 'ICGV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (25, 'ICGV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (26, 'XAMV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (27, 'CBXV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (28, 'WAGV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (29, 'WAGV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (30, 'IQGV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (31, 'IQGV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (32, 'BMWV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (33, 'BMWcisco', 'Cisco SB / 8xx');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (34, 'INWV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (35, 'AUMV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (36, 'LRZV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (37, 'ARAV2  (195.69.112.217/32, 195.69.112.224/27) MTU=1452 !!', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (38, 'TEWV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (39, 'HYPV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (40, 'MXIV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (41, 'SHDV1', 'Alcatel / Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (42, 'SHDV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (43, 'MLCV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (44, 'GTTcisco', 'Cisco SB101/828');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (45, 'GTTV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (150, 'ACLS_V3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (151, 'ARAV3  (195.69.112.217/32, 195.69.112.224/27) MTU=1452 !!', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (153, 'BARV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (154, 'BMWV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (155, 'CBXV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (156, 'CYWV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (157, 'GITV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (158, 'GTTV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (159, 'HYPV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (160, 'ICGV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (161, 'IGHV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (162, 'ISSV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (163, 'INWV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (164, 'IPMV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (165, 'IQGV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (166, 'LRZV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (167, 'MLCV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (168, 'MXIV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (169, 'NTPV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (170, 'QSCV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (171, 'SHPV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (172, 'TEWV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (176, 'SSGV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (174, 'XAMV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (175, 'ISSV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (177, 'SSGcisco', 'Cisco SB101/878/828');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (178, 'GBNV1', 'Thomson STP520s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (179, 'GBNV2', 'Thomson STP610s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (180, 'GBNV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (181, 'GBNV4', 'Thomson STP530i');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (182, 'LAMV3', 'Thomson STP605s');
Insert into T_EG_ACL
   (ID, NAME, ROUTERTYP)
 Values
   (183, 'LAMV4', 'Thomson STP530i');
COMMIT;

update t_eg_acl
set version =1;

ALTER TABLE T_EG_ACL
MODIFY(VERSION  NOT NULL);
COMMIT;
