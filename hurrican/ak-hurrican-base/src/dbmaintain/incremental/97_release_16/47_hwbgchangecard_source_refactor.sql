drop sequence S_T_HW_BG_CHANGE_CARD_SOURCE_0;

alter table T_HW_BG_CHANGE_CARD_SOURCE rename column HW_BAUGRUPPEN_ID_TO_CHANGE to HW_BAUGRUPPE_ID;
alter table T_HW_BG_CHANGE_CARD_SOURCE drop column ID;
alter table T_HW_BG_CHANGE_CARD_SOURCE drop column ORDER_NO;
alter table T_HW_BG_CHANGE_CARD_SOURCE drop column VERSION;