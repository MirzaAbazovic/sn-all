/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 18:21:04
 */
package de.augustakom.hurrican.gui.base.ip;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.SwingFactoryXML;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;

/**
 * Erweitert die {@link SwingFactoryXML} um die Moeglichkeit komplexe Komponenten zu liefern. In diesem Fall ein {@link
 * AKJIPAddressComponent}.
 *
 *
 *
 * @since Release 10
 */
public final class AKJIPAddressComponentFactory extends SwingFactoryXML {

    /**
     * XPath, um auf die Definition einer bestimmten IPAddressComponent zuzugreifen.
     */
    private static final String XPATH_IP_ADDRESS_COMPONENT = "//IPAddressComponent[@name='%s']";
    /**
     * XPath, um auf die Definition erlaubten kontextabhaengigen IP Typen zuzugreifen
     */
    private static final String XPATH_IP_TYPE_ALLOWED = "/IPTypesAllowed[IPType = '%s']";

    private JPopupMenu copyPopup = null;

    private AKJIPAddressComponentFactory(String resource) {
        super(resource);
    }

    public static AKJIPAddressComponentFactory create(String resource) {
        return new AKJIPAddressComponentFactory(resource);
    }

    /**
     * Sucht fuer alle IP Adresstypen ueber die ID einen Eintrag in dem Dokument. Der Rueckgabewert ist niemals {@code
     * null}!
     */
    private List<AddressTypeEnum> getAllowedIpTypes(String xpathBase) {
        List<AddressTypeEnum> allowedIpTypes = new ArrayList<AddressTypeEnum>();
        AddressTypeEnum[] addressTypes = AddressTypeEnum.values();
        for (AddressTypeEnum addressType : addressTypes) {
            String extension = StringUtils.replace(XPATH_IP_TYPE_ALLOWED, PLACEHOLDER,
                    addressType.typeId.toString());
            String allowedIpTypeId = getValue(xpathBase + extension);
            if (StringUtils.isNotBlank(allowedIpTypeId)) {
                allowedIpTypes.add(addressType);
            }
        }
        return allowedIpTypes;
    }

    /**
     * Erzeugt eine IPAddress-Komponente bestehend aus einem TextField und einem Button.
     */
    public AKJIPAddressComponent createIPAddressComponent(String name) {
        String xpathBase = StringUtils.replace(XPATH_IP_ADDRESS_COMPONENT, PLACEHOLDER, name);

        AKJTextField tfIPAddress = new AKJTextField();
        tfIPAddress.setEditable(false);
        AKJButton btnEdit = new AKJButton();
        tfIPAddress.setComponentPopupMenu(createCopyPopupMenu());

        AKJIPAddressComponent ipAdrC = new AKJIPAddressComponent(tfIPAddress, btnEdit, getAllowedIpTypes(xpathBase));
        ipAdrC.setName(name);
        ipAdrC.getAccessibleContext().setAccessibleName(name);
        ipAdrC.addMouseListener(getAdminMouseListener());

        String tooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP);
        if (StringUtils.isNotEmpty(tooltip)) {
            ipAdrC.setToolTipText(tooltip);
        }

        String btnText = getValue(xpathBase + XPATH_ELEMENT_TEXT + ".button");
        if (StringUtils.isNotEmpty(btnText)) {
            ipAdrC.setButtonText(btnText);
        }
        String btnTooltip = getValue(xpathBase + XPATH_ELEMENT_TOOLTIP + ".button");
        if (StringUtils.isNotEmpty(btnTooltip)) {
            ipAdrC.setButtonToolTipText(btnTooltip);
        }
        String btnIcon = getValue(xpathBase + XPATH_ELEMENT_ICON + ".button");
        if (StringUtils.isNotEmpty(btnIcon)) {
            ipAdrC.setButtonIcon(btnIcon);
        }

        String dlgTitle = getValue(xpathBase + XPATH_ELEMENT_TEXT + ".dialog");
        if (StringUtils.isNotEmpty(dlgTitle)) {
            ipAdrC.setDialogTitle(dlgTitle);
        }

        ipAdrC.setActiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_ACTIVE), DEFAULT_BG_COLOR_ACTIVE));
        ipAdrC.setInactiveColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_INACTIVE), DEFAULT_BG_COLOR_INACTIVE));
        ipAdrC.setSelectionColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION), DEFAULT_SELECTION_COLOR));
        ipAdrC.setSelectedTextColor(createColor(getValue(xpathBase + XPATH_ELEMENT_COLOR_SELECTION_TEXT), DEFAULT_SELECTION_TEXT_COLOR));
        int columns = getIntValue(xpathBase + XPATH_ELEMENT_COLUMNS);
        if (columns > 0) {
            ipAdrC.setColumns(columns);
        }

        return ipAdrC;
    }

    public AKJIPAddressComponent createIPAddressComponent(String name, KeyListener keyListener) {
        AKJIPAddressComponent ipAdrC = createIPAddressComponent(name);
        if (keyListener instanceof AKSearchKeyListener) {
            ipAdrC.addSearchKeyListener((AKSearchKeyListener) keyListener);
        }
        else {
            ipAdrC.addKeyListener(keyListener);
        }
        return ipAdrC;
    }

    public AKJIPAddressComponent createIPAddressComponent(String name, boolean usable) {
        AKJIPAddressComponent ipAdrC = createIPAddressComponent(name);
        ipAdrC.setUsable(usable);
        return ipAdrC;
    }

    /**
     * Erzeugt ein PopupMenu mit nur einem Eintrag (Kopieren).
     */
    private JPopupMenu createCopyPopupMenu() {
        if (copyPopup == null) {
            copyPopup = new JPopupMenu();
            AKJMenuItem mi = new AKJMenuItem();
            mi.setAction(new CopyAction());
            mi.setText("Kopieren");
            copyPopup.add(mi);
        }

        return copyPopup;
    }

} // end

