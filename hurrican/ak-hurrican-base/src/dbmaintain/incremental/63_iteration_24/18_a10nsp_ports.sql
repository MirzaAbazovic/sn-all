
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 1, 'WSMNET1');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 2, 'WSDTE1');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 3, 'WSDTE2');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 31, 'WSDTE3');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 4, 'WSTEF1');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 5, 'WSUAT1');
insert into T_FTTX_A10_NSP (ID, VERSION, A10_NSP_NUMMER, A10_NSP_NAME)
  values (S_T_FTTX_A10_NSP_0.nextVal, 0, 6, 'WSCON1');

insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000001');  
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000002');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000003');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000004');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000005');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000006');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000007');
insert into T_TDN (ID, TDN) values (S_T_TDN_0.nextVal, 'FE000000008');

insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSMNET1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000001'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='MNET-001'),
      1
    );

insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSDTE1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000002'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='DTAG1-001'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSDTE1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000002'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='DTAG1-002'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSDTE1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000002'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='DTAG1-003'),
      1
    );

insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSDTE2'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000003'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='DTAG2-001'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSDTE3'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000008'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='DTAG2-001'),
      0
    );
insert into T_FTTX_A10_PORT_2_OLT (A10_NSP_PORT_ID, HW_RACK_OLT_ID)
  values (
    (select tmpa10.ID from T_FTTX_A10_NSP_PORT tmpa10 where tmpa10.VBZ_ID=(select t.ID from T_TDN t where t.TDN='FE000000008')),
    950
  );
insert into T_FTTX_A10_PORT_2_OLT (A10_NSP_PORT_ID, HW_RACK_OLT_ID)
  values (
    (select tmpa10.ID from T_FTTX_A10_NSP_PORT tmpa10 where tmpa10.VBZ_ID=(select t.ID from T_TDN t where t.TDN='FE000000008')),
    951
  );
insert into T_FTTX_A10_PORT_2_OLT (A10_NSP_PORT_ID, HW_RACK_OLT_ID)
  values (
    (select tmpa10.ID from T_FTTX_A10_NSP_PORT tmpa10 where tmpa10.VBZ_ID=(select t.ID from T_TDN t where t.TDN='FE000000008')),
    952
  );
 
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSTEF1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000004'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='TELEF-001'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSTEF1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000004'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='TELEF-002'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSTEF1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000004'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='TELEF-003'),
      1
    );
        
    
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSUAT1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000005'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='UATP1-001'),
      1
    );
insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSUAT1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000006'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='UATP2-001'),
      1
    );

insert into T_FTTX_A10_NSP_PORT (ID, VERSION, A10_NSP_ID, VBZ_ID, EKP_FRAME_CONTRACT_ID, IS_DEFAULT_4_EKP)
  values (S_T_FTTX_A10_NSP_PORT_0.nextVal, 0,
      (select tmpa10.ID from T_FTTX_A10_NSP tmpa10 where tmpa10.A10_NSP_NAME='WSCON1'),
      (select tmptdn.ID from T_TDN tmptdn where tmptdn.TDN='FE000000007'),
      (select tmpekp.ID from T_FTTX_EKP_FRAME_CONTRACT tmpekp where tmpekp.FRAME_CONTRACT_ID='CONG-001'),
      1
    );

