--
--
--

INSERT INTO T_GUI_DEFINITION (ID,
                              CLASS,
                              TYPE,
                              NAME,
                              TEXT,
                              TOOLTIP,
                              ICON,
                              ORDER_NO,
                              ADD_SEPARATOR,
                              ACTIVE)
  VALUES   (217,
            'de.augustakom.hurrican.gui.auftrag.Client2SitePanel',
            'PANEL',
            'auftrag.client2site.panel',
            'Client 2 Site',
            'Panel fuer die Anzeige der Client2Site Daten',
            NULL,
            18,
            NULL,
            '1');

INSERT INTO T_GUI_MAPPING (ID,
                           GUI_ID,
                           REFERENZ_ID,
                           REFERENZ_HERKUNFT)
   SELECT   S_T_GUI_MAPPING_0.NEXTVAL,
            gd.id,
            PD.PROD_ID,
            'de.augustakom.hurrican.model.cc.Produkt'
     FROM   t_gui_definition gd, t_produkt pd
    WHERE   gd.class = 'de.augustakom.hurrican.gui.auftrag.Client2SitePanel'
            AND PD.ANSCHLUSSART = 'VPN IPSec Client-to-Site';