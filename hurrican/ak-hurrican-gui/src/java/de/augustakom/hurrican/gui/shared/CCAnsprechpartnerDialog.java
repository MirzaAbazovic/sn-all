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
import org.springframework.validation.ObjectError;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog zur Anzeige und zum Editieren von (CC-)Adressen. <br> Das generierte (oder aktualisierte) Adress-Objekt wird
 * ueber die Methode <code>setValue</code> gespeichert bzw. an den Caller uebergeben.
 *
 *
 */
public class CCAnsprechpartnerDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = 1850858722149112699L;

    private static final Logger LOGGER = Logger.getLogger(CCAnsprechpartnerDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/CCAnsprechpartnerDialog.xml";

    // GUI-Elemete
    private CCAddressPanel addressPnl = null;

    // Modelle
    private Ansprechpartner ansprechpartner;
    private Long kundeNo;

    // Services
    private AnsprechpartnerService ansprechpartnerService;
    private CCAuftragService auftragService;
    private ReferenceService referenceService;

    public CCAnsprechpartnerDialog(Ansprechpartner ansprechpartner) {
        super(RESOURCE);
        this.ansprechpartner = ansprechpartner;
        initServices();
        loadData();
        createGUI();
    }

    /* Initialisiert die notwendigen Services */
    private void initServices() {
        try {
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            auftragService = getCCService(CCAuftragService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(StringTools.formatString(getSwingFactory().getText("title"),
                new Object[] { ((ansprechpartner != null) && (ansprechpartner.getId() != null)) ? "" + ansprechpartner.getId() : "neu" }));

        addressPnl = new CCAddressPanel(ansprechpartner, kundeNo);
        addressPnl.changeAddress();

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(addressPnl, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            Auftrag auftrag = auftragService.findAuftragById(ansprechpartner.getAuftragId());
            kundeNo = (auftrag != null) ? auftrag.getKundeNo() : null;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            ansprechpartner = addressPnl.getAnsprechpartner();
            if (ansprechpartner != null) {
                validatePreferredAnsprechpartner();

                CCAddress address = addressPnl.getAddress();
                if (address == null) {
                    return;
                }

                if(addressPnl.getAddress().getEmail() != null){
                    validateMailAddress(address);
                }

                Reference reference = referenceService.findReference(ansprechpartner.getTypeRefId());
                if ((reference.getIntValue() != null)
                        && !address.getAddressType().equals(Long.valueOf(reference.getIntValue()))) {
                    ValidationException validationException = new ValidationException(address, CCAddress.class.getSimpleName());
                    validationException.addError(new ObjectError("address", "Der Typ der Adresse stimmt nicht mit dem Typen des Ansprechpartners überein"));
                    throw validationException;
                }

                ansprechpartner.setAddress(address);
                ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
            }

            prepare4Close();
            setValue(ansprechpartner);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void validateMailAddress(CCAddress address) throws ValidationException {
        final String separator = ";";
        String[] emails = addressPnl.getAddress().getEmail().split(separator);
        for (String emailRaw : emails) {
            final String email = emailRaw.trim(); //remove trailing and leading whitespaces for validation
            boolean isValidEmail = EMailValidator.getInstance().isValid(email);
            if (!isValidEmail) {
                ValidationException validationException = new ValidationException(address, CCAddress.class.getSimpleName());
                validationException.addError(new ObjectError("address", "Die EMail-Adresse " + email + " ist ungueltig!"));
                throw validationException;
            }
        }
    }

    private void validatePreferredAnsprechpartner() throws ValidationException {
        if (Boolean.TRUE.equals(ansprechpartner.getPreferred())) {
            Ansprechpartner existing;
            ValidationException valEx = new ValidationException(ansprechpartner, Ansprechpartner.class.getSimpleName());
            try {
                existing = ansprechpartnerService.findPreferredAnsprechpartner(
                        Ansprechpartner.Typ.forRefId(ansprechpartner.getTypeRefId()), ansprechpartner.getAuftragId());
            }
            catch (FindException e) {
                valEx.addError(new ObjectError("ansprechpartner", "Fehler beim Versuch zu ermitteln," +
                        "ob bereits ein bevorzugter Ansprechpartner diesen Typs für diesen Vertrag existiert."));
                throw valEx;
            }
            if ((existing != null) && !existing.getId().equals(ansprechpartner.getId())) {
                valEx.addError(new ObjectError("ansprechpartner", "Es existiert bereits ein bevorzugter " +
                        "Ansprechpartner diesen Typs für diesen Vertrag."));
                throw valEx;
            }
        }
    }

    @Override
    protected void cancel() {
        try {
            if ((ansprechpartner != null) && (ansprechpartner.getId() != null)) {
                ansprechpartner = ansprechpartnerService.findAnsprechpartner(ansprechpartner.getId());
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        super.cancel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // not used
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}
