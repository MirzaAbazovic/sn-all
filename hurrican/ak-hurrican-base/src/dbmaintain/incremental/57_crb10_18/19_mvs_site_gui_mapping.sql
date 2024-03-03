-- Mapping welche Reiter erscheinen sollen fuer ein Produkt aus der Gruppe MVS Site

-- Endstelle
INSERT INTO T_GUI_MAPPING (
    ID,
    GUI_ID,
    REFERENZ_ID,
    REFERENZ_HERKUNFT,
    VERSION
)
VALUES (
    S_T_GUI_MAPPING_0.nextVal,
    203,
    28,
    'de.augustakom.hurrican.model.cc.ProduktGruppe',
    0
);

-- MVS Site
INSERT INTO T_GUI_MAPPING (
    ID,
    GUI_ID,
    REFERENZ_ID,
    REFERENZ_HERKUNFT,
    VERSION
)
VALUES (
    S_T_GUI_MAPPING_0.nextVal,
    405,
    28,
    'de.augustakom.hurrican.model.cc.ProduktGruppe',
    0
);
