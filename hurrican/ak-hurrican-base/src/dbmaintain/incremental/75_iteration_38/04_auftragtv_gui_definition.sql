Insert into T_GUI_DEFINITION (
    ID,
    CLASS,
    TYPE,
    NAME,
    TEXT,
    TOOLTIP,
    ORDER_NO,
    ACTIVE,
    VERSION
 )
 values (
    406,
    'de.augustakom.hurrican.gui.auftrag.tv.AuftragTvPanel',
    'PANEL',
    'auftrag.tv.panel',
    'Versorgte Standorte',
    'Durch den TV-Auftrag mitversorgte Standorte',
    6,
    '1',
    0
);

INSERT INTO T_GUI_MAPPING (
    ID,
    GUI_ID,
    REFERENZ_ID,
    REFERENZ_HERKUNFT,
    VERSION
)
VALUES (
    S_T_GUI_MAPPING_0.nextVal,
    406,
    20,
    'de.augustakom.hurrican.model.cc.ProduktGruppe',
    0
);