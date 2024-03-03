--
-- SQL-Script fuer div. Erweiterungen 
--

alter table T_PHYSIKTYP add CPS_TRANSFER_METHOD VARCHAR2(10);
comment on column T_PHYSIKTYP.CPS_TRANSFER_METHOD IS 'CPS Name fuer den Physiktyp';
update T_PHYSIKTYP set CPS_TRANSFER_METHOD='ADSL' where ID in (7, 507);
update T_PHYSIKTYP set CPS_TRANSFER_METHOD='ADSL2+' where ID in (513,514);
commit;


alter table T_HVT_STANDORT add CPS_PROVISIONING CHAR(1);


-- Action im Auftrags-Menu, um eine CPS-Tx anzulegen bzw. einen Dialog zu oeffnen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE, ADD_SEPARATOR)
	VALUES (321, 'de.augustakom.hurrican.gui.auftrag.actions.OpenCPSTxCreationDlgAction', 'ACTION', 
	'open.cps.tx.creation.dlg.action', 'CPS-Tx erzeugen...', 
	'Oeffnet einen Dialog, um eine CPS-Transaction fuer den Auftrag anzulegen', 
	null, 35, 1, 1);
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (1, 321, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');	

	
	