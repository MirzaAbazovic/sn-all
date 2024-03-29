UPDATE T_HW_BAUGRUPPEN_TYP SET NAME = 'MA5652G_VDSL', PORT_COUNT = 8, DESCRIPTION = 'VDSL-Ports der Huawei MDU MA5652G', HW_SCHNITTSTELLE_NAME = 'VDSL',
	HVT_TECHNIK_ID = (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Huawei') WHERE ID = 37;

INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'MA5652G_POTS', 4, 'POTS-Ports der Huawei MDU MA5652G', 1, 'POTS', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Huawei'), 0);

INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'O-881V-P_VDSL', 8, 'VDSL-Ports der Alcatel MDU O-881V-P', 1, 'VDSL', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Alcatel'), 0);
INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'O-881V-P_POTS', 8, 'POTS-Ports der Alcatel MDU O-881V-P', 1, 'POTS', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Alcatel'), 0);

INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'MA5616_VDTH', 24, '24-Port VDSL2-Karte f�r Huawei MDU MA5616 ', 1, 'VDSL', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Huawei'), 0);
INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'MA5616_VDGE', 16, '16-Port VDSL2-Karte f�r Huawei MDU MA5616 ', 1, 'VDSL', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Huawei'), 0);
INSERT INTO T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION)
	VALUES ( S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'MA5616_ASPB', 64, '64-Port POTS-Karte f�r Huawei MDU MA5616 ', 1, 'POTS', 'MDU', (SELECT ID FROM T_HVT_TECHNIK WHERE HERSTELLER = 'Huawei'), 0);
