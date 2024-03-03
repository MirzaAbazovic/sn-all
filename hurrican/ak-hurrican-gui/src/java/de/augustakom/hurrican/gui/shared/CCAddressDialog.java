/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:48:18
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * Dialog zur Anzeige und zum Editieren von (CC-)Adressen. <br> Das generierte (oder aktualisierte) Adress-Objekt wird
 * ueber die Methode <code>setValue</code> gespeichert bzw. an den Caller uebergeben.
 *
 *
 */
public class CCAddressDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(CCAddressDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/CCAddressDialog.xml";

    private Long addressId = null;
    private Long kundeNoOrig = null;
    private GeoId geoId = null;
    private Long addressType = null;
    private boolean editMode = true;

    private CCAddressPanel addressPnl = null;


    /**
     * Konstruktor fuer den Dialog.
     *
     * @param addressId ID der anzuzeigenden Adresse
     * @param geoId     (optional) kann angegeben werden, um die Strasse einer bestehenden Adresse zu ueberschreiben
     * @param editMode  Flag, ob der Dialog im Edit-Mode betrieben werden soll
     */
    public CCAddressDialog(Long addressId, GeoId geoId, boolean editMode) {
        super(RESOURCE);
        this.addressId = addressId;
        this.geoId = geoId;
        this.editMode = editMode;
        createGUI();
    }

    /**
     * Konstruktor mit Angabe der Kundennummer (optional), um eine neue Adresse anzulegen.
     *
     * @param kundeNoOrig (optional) Kundennummer, der die neu zu erstellende Adresse zugeordnet werden soll.
     * @param geoId       (optional) GeoId mit den Vorgabe-Daten.
     * @param addressType (optional) Angabe des anzulegenden Adress-Typs
     */
    public CCAddressDialog(Long kundeNoOrig, GeoId geoId, Long addressType) {
        super(RESOURCE);
        this.kundeNoOrig = kundeNoOrig;
        this.geoId = geoId;
        this.addressType = addressType;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(StringTools.formatString(getSwingFactory().getText("title"),
                new Object[] { (addressId != null) ? "" + addressId : "neu" }));

        if (addressId != null) {
            addressPnl = new CCAddressPanel(addressId);
        }
        else {
            addressPnl = new CCAddressPanel(kundeNoOrig, geoId, addressType);
        }

        // Gebe GUI-Elemente in AddressPanel frei
        if (editMode) {
            addressPnl.changeAddress();
        }

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(addressPnl, BorderLayout.CENTER);
    }

    @Override
    protected void doSave() {
        try {
            CCAddress address = addressPnl.getAddress();
            if (address != null) {
                // Falls keine neue Adresse generiert werden soll, die ID aber
                // nicht mehr vorhanden ist (durch CCAddressPanel), diese wieder setzen!
                if (address.getId() == null) {
                    address.setId(addressId);
                }

                CCKundenService ks = getCCService(CCKundenService.class);
                ks.saveCCAddress(address);

                prepare4Close();
                setValue(address);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
