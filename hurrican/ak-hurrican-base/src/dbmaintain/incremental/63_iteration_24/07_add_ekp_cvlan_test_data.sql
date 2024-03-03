-- Rahmenvertraege
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'DTAG1', 'DTAG1-001')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'DTAG1', 'DTAG1-002')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'DTAG1', 'DTAG1-003')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'DTAG2', 'DTAG2-001')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'EKP001', '1.0')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'TELEF', 'TELEF-001')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'TELEF', 'TELEF-002')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'TELEF', 'TELEF-003')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'UATP1', 'UATP1-001')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'UATP2', 'UATP2-001')
;
insert into T_FTTX_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'CONG', 'CONG-001')
;
-- CVLAN-Daten (DTAG)
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-001'),
            'HSI',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-002'),
            'HSI',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-003'),
            'HSI',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG2-001'),
            'HSI',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-001'),
            'VOIP',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-002'),
            'VOIP',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-003'),
            'VOIP',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG2-001'),
            'VOIP',
            '7',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-001'),
            'TV',
            '8',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-002'),
            'TV',
            '8',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG1-003'),
            'TV',
            '8',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'DTAG2-001'),
            'TV',
            '8',
            null,
            'IPoE')
;
-- CVLAN-Daten (Telefonica)
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-001'),
            'TV',
            '10',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-002'),
            'TV',
            '10',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-003'),
            'TV',
            '10',
            null,
            'IPoE')
; 
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-001'),
            'HSI',
            '11',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-002'),
            'HSI',
            '11',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-003'),
            'HSI',
            '11',
            null,
            'PPPoE')
;            
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-001'),
            'VOIP',
            '12',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-002'),
            'VOIP',
            '12',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-003'),
            'VOIP',
            '12',
            null,
            'PPPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-001'),
            'VOD',
            '13',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-002'),
            'VOD',
            '13',
            null,
            'IPoE')
;
insert into T_FTTX_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT_LIMIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_FTTX_EKP_FRAME_CONTRACT where CONTRACT_ID = 'TELEF-003'),
            'VOD',
            '13',
            null,
            'IPoE')
;