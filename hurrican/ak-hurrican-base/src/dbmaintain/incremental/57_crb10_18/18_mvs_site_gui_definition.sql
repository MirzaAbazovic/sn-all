-- MVS Enterprise Panel definieren
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
    405,
    'de.augustakom.hurrican.gui.auftrag.mvs.AuftragMVSSitePanel',
    'PANEL',
    'auftrag.mvs.site.panel',
    'MVS Site',
    'TOOLTIP',
    7,
    '1',
    0
 );
