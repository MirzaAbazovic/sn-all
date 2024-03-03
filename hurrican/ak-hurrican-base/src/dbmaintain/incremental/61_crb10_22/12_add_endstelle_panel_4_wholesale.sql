-- Endstellen-Panel fuer Wholesale Produkt

INSERT INTO   T_GUI_MAPPING (ID,
                             GUI_ID,
                             REFERENZ_ID,
                             REFERENZ_HERKUNFT,
                             VERSION)
   SELECT   S_T_GUI_MAPPING_0.NEXTVAL,
            DEF.ID,
            32,
            'de.augustakom.hurrican.model.cc.ProduktGruppe',
            0
     FROM   T_GUI_DEFINITION DEF
    WHERE   DEF.CLASS =
               'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel';

