alter table T_AUFTRAG_IMPORT modify KUNDE__NO number(19);

alter table T_CB_USECASE modify REFERENCE_ID number(19);
alter table T_CB_USECASE modify EXM_TBV_ID number(19);

alter table T_CB_VORGANG modify EXM_ID number(19);
alter table T_CB_VORGANG modify EXM_RET_FEHLERTYP number(19);
alter table T_CB_VORGANG modify USER_ID number(19);

alter table T_CPS_TX modify SO_STACK_ID number(19);
alter table T_CPS_TX modify SO_STACK_SEQ number(19);
alter table T_CPS_TX modify TAIFUN_ORDER__NO number(19);

alter table T_CPS_TX_SUB_ORDERS modify AUFTRAG_ID number(19);
alter table T_CPS_TX_SUB_ORDERS modify VERLAUF_ID number(19);

alter table T_LOCK_DETAIL modify ABTEILUNG_ID number(19);

alter table T_RANGIERUNG modify HISTORY_FROM_RID number(19);

alter table T_VPN modify VPN_TYPE number(19);
alter table T_VPN modify VPN_KUNDEN__NO number(19);

