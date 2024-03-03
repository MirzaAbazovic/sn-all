-- neue Icons fuer Dynamic Tree

update T_TREE_DEFINITION set CHILD_ICON_URL='de/augustakom/hurrican/gui/images/baugruppe.gif'
  where CHILD_CLASS = 'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe';


-- Ports zur Baugruppe ermitteln (EQ-IN)
delete from T_TREE_DEFINITION where id=14;
insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (14, 'HW_TREE', 'de.augustakom.hurrican.model.cc.hardware.HWBaugruppe',
  'de.augustakom.hurrican.model.cc.Equipment',
  'de/augustakom/hurrican/gui/images/port.gif',
  'select eq.EQ_ID as MODEL_ID, ''EQ: '' || eq.HW_EQN || '' ('' || eq.STATUS || '')'' as DISPLAY_NAME
   from T_EQUIPMENT eq
   inner join T_RANGIERUNG r on eq.EQ_ID=r.EQ_IN_ID
   where eq.HW_BAUGRUPPEN_ID=? and eq.HW_EQN is not null
   and (r.GUELTIG_BIS is null or r.GUELTIG_BIS > SYSDATE)
   order by eq.HW_EQN',
  '1', 20);

-- Auftrag zum Port laden
delete from T_TREE_DEFINITION where id=15;
insert into T_TREE_DEFINITION (ID, TREE_NAME, MODEL_CLASS, CHILD_CLASS,
  CHILD_ICON_URL, SELECT_CHILDREN_SQL, USE_SQL_PARAMETER, ORDER_NO)
  values (15, 'HW_TREE', 'de.augustakom.hurrican.model.cc.Equipment',
  'de.augustakom.hurrican.model.cc.AuftragTechnik',
  'de/augustakom/hurrican/gui/images/auftrag.gif',
  'select atech.AUFTRAG_ID as MODEL_ID, ''Auftrag: '' || atech.AUFTRAG_ID || '' ('' || tdn.TDN || '')'' as DISPLAY_NAME
   from T_RANGIERUNG r
   left join T_ENDSTELLE es on (r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL)
   left join T_AUFTRAG_TECHNIK atech on es.ES_GRUPPE=atech.AT_2_ES_ID
   left join T_TDN tdn on atech.TDN_ID=tdn.ID
   where r.EQ_IN_ID=?
   and (r.GUELTIG_BIS is null or r.GUELTIG_BIS > SYSDATE)
   and (atech.GUELTIG_BIS is null or atech.GUELTIG_BIS > SYSDATE)',
  '1', 20);


