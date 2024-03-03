--
-- Endstellen-Panel auch für Client2Site
--

INSERT INTO T_GUI_MAPPING (ID,
                           GUI_ID,
                           REFERENZ_ID,
                           REFERENZ_HERKUNFT)
   SELECT   S_T_GUI_MAPPING_0.NEXTVAL,
            gd.id,
            PD.PROD_ID,
            'de.augustakom.hurrican.model.cc.Produkt'
     FROM   t_gui_definition gd, t_produkt pd
    WHERE   gd.class = 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel'
            AND PD.ANSCHLUSSART = 'VPN IPSec Client-to-Site';