-- nicht benoetigte Spalte rausloeschen
alter table T_EKP_FRAME_CONTRACT drop column CVLAN_ID;

-- Rahmenvertrag fuer M-net anlegen
insert into T_EKP_FRAME_CONTRACT (ID, EKP_ID, CONTRACT_ID) VALUES (S_T_EKP_FRAME_CONTRACT_0.nextVal, 'MNET', 'MNET-001');

-- M-net CVLAN-Daten aufbauen
insert  into T_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_EKP_FRAME_CONTRACT where EKP_ID = 'MNET'),
            'HSI',
            '40',
            null,
            'PPPoE');

insert  into T_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_EKP_FRAME_CONTRACT where EKP_ID = 'MNET'),
            'HSIPLUS',
            '40',
            null,
            'PPPoE');

insert  into T_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_EKP_FRAME_CONTRACT where EKP_ID = 'MNET'),
            'VOIP',
            '200',
            null,
            'PPPoE');
            
insert  into T_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_EKP_FRAME_CONTRACT where EKP_ID = 'MNET'),
            'POTS',
            '500',
            null,
            'PPPoE');
            
insert  into T_CVLAN (ID, EKP_FRAME_CONTRACT_ID, TYP, VLAN_VALUE, PBIT, PROTOCOLL) 
        VALUES(
            S_T_CVLAN_0.nextVal, 
            (select ID from T_EKP_FRAME_CONTRACT where EKP_ID = 'MNET'),
            'IAD',
            '3',
            null,
            'IPoE');
            