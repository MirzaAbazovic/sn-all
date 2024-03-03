/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2011 17:41:00
 */
package de.augustakom.hurrican.gui.base.ip;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKCleanableComponent;
import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.IconHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * Diese Komponente besteht aus einem Textfeld und einem Button. Das Textfeld zeigt eine IP-Adresse an. Der Button
 * oeffnet einen {@link AKJIPAddressDialog} zur Auswahl bzw. Definition einer IP-Adresse.
 *
 *
 */
public class AKJIPAddressComponent extends AKJPanel implements AKManageableComponent, ActionListener,
        AKSwingConstants, AKColorChangeableComponent, AKCleanableComponent {

    private static final Logger LOGGER = Logger.getLogger(AKJIPAddressComponent.class);
    private static final long serialVersionUID = 6793493511938292768L;

    private Highlighter highlighter = null;
    private final static Color HIGHLIGHT_COLOR_V4 = Color.YELLOW;
    private final static Color HIGHLIGHT_COLOR_V6 = Color.ORANGE;

    private boolean managementCalled = false;
    private MouseListener adminML = null;
    private Publisher publisher = null;
    private boolean executable = true;
    private boolean visible = true;

    private int tfColumns = -1;
    private Color tfActiveColor = null;
    private Color tfInactiveColor = null;
    private Color tfSelectionColor = null;
    private Color tfSelectedTextColor = null;

    private AKJTextField tfIPAddress = null;
    private AKJButton btnEdit = null;

    private String dialogTitle = null;
    private IPAddress ipAddress = null;

    /**
     * Liste an Pr&auml;fix-Adressen f&uuml;r den akutellen Auftrag.
     */
    private List<IPAddress> prefixAddresses = null;
    /**
     * IP Typen fuer die kontextabhaengige Erfassung
     */
    private List<AddressTypeEnum> allowedIpTypes = null;

    public AKJIPAddressComponent(AKJTextField textField, AKJButton button, List<AddressTypeEnum> allowedIpTypes) {
        super();
        this.allowedIpTypes = allowedIpTypes;
        this.tfIPAddress = textField;
        this.btnEdit = button;
        init();
    }

    /**
     * Laedt ein Icon fuer den Button vor.
     */
    private static final Icon BTN_EDIT_ICON = IconHelper
            .createImageIcon("de/augustakom/common/gui/images/reference.gif");

    /**
     * Erstellte einen neuen Button und setzt das geladene Icon drauf. Falls das nicht geladen werden kann wird ein
     * Ersatztext auf dem Button angezeigt.
     */
    private void initButton() {
        if (BTN_EDIT_ICON != null) {
            btnEdit.setIcon(BTN_EDIT_ICON);
            btnEdit.setBorder(BorderFactory.createEtchedBorder());
        }
        else {
            btnEdit.setText("...");
        }
        btnEdit.setPreferredSize(new Dimension(20, tfIPAddress.getPreferredSize().height));
        btnEdit.addActionListener(this);
    }

    /**
     * Erstellt ein neues Eingabefeld fuer die IP-Addresse, welches nicht editierbar sein soll und den Button via {@link
     * #initButton()}.
     */
    private void init() {
        publisher = new Publisher();

        highlighter = new DefaultHighlighter();
        tfIPAddress.setHighlighter(highlighter);
        initButton();

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(tfIPAddress , GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0)));
        this.add(btnEdit     , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE, new Insets(0,2,0,0)));
        // @formatter:on
    }

    public void setPrefixAddresses(List<IPAddress> prefixAddresses) {
        this.prefixAddresses = prefixAddresses;
    }

    private List<IPAddress> getPrefixAddresses() {
        if (prefixAddresses == null) {
            prefixAddresses = new ArrayList<>();
        }
        return prefixAddresses;
    }

    /**
     * Setzt das Eingabefeld mit Werten aus {@link IPAddress}.
     *
     * @param ipAddress
     */
    public void setIPAddress(IPAddress ipAddress) {
        publisher.notifyObservers(true, ipAddress);
        this.ipAddress = ipAddress;
        GuiTools.cleanFields(this);
        if ((this.ipAddress != null) && (this.ipAddress.getAbsoluteAddress() != null)) {
            tfIPAddress.setText(this.ipAddress.getAbsoluteAddress() + " " + getInetAddressType());
            setToolTipTextWithAddressTypeForInetAddressField();
            highlightIPAddressInTextField();
        }
    }

    /**
     * @return
     */
    public IPAddress getIPAddress() {
        if ((ipAddress != null) && (ipAddress.getAddress() == null)) {
            return null; // Adresse gelöscht
        }
        return ipAddress;
    }

    /**
     * Setzt einen Tooltip auf das Eingabefeld. Dort soll der Typ der IP-Adresse angezeigt werden.
     */
    private void setToolTipTextWithAddressTypeForInetAddressField() {
        tfIPAddress.setToolTipText("IP-Adresse vom Typ: " + ipAddress.getIpType());
    }

    /**
     * Liefert den Text zu dem aktuellen IP-Adresstypen. Text wird aus einem {@link ResourceBundle} gespeist.
     */
    private String getInetAddressType() {
        if (ipAddress.getIpType() == null) {
            return "";
        }
        return ipAddress.getIpType().mnemonic;
    }

    /**
     * Sucht in einem JTextField nach einem String und gibt die Start- und Endposition zurück.
     */
    private Map<Integer, Integer> findStringInTextField(String toFind, JTextField tf) {
        Map<Integer, Integer> map = new HashMap<>();
        if (toFind != null) {
            String text = tf.getText();
            map.put(0, text.indexOf(toFind, 0));
            map.put(1, map.get(0) + toFind.length());
        }
        return map;
    }

    /**
     * Selektiert einen Bereich in einem JTextField
     */
    private void selectTextInTextField(Map<Integer, Integer> position, JTextField tf) {
        tf.requestFocus();
        if (!position.isEmpty()) {
            tf.select(position.get(0), position.get(1));
        }
    }

    /**
     * Markiert den Typ der IP-Adresse im Textfeld
     */
    private void highlightIPAddressInTextField() {
        Color COLOR;
        if (ipAddress.isIPV4()) {
            COLOR = HIGHLIGHT_COLOR_V4;
        }
        else {
            COLOR = HIGHLIGHT_COLOR_V6;
        }
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(COLOR);
        highlighter.removeAllHighlights();
        Map<Integer, Integer> map = findStringInTextField(getInetAddressType(), tfIPAddress);
        try {
            highlighter.addHighlight(map.get(0), map.get(1), painter);
            tfIPAddress.setCaretPosition(map.get(0));
        }
        catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void setTFParams(AKJIPAddressDialog ipAddressDialog) {
        if (tfColumns > -1) {
            ipAddressDialog.setColumns(tfColumns);
        }
        if (tfActiveColor != null) {
            ipAddressDialog.setActiveColor(tfActiveColor);
        }
        if (tfInactiveColor != null) {
            ipAddressDialog.setInactiveColor(tfInactiveColor);
        }
        if (tfSelectionColor != null) {
            ipAddressDialog.setSelectionColor(tfSelectionColor);
        }
        if (tfSelectedTextColor != null) {
            ipAddressDialog.setSelectedTextColor(tfSelectedTextColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEdit) {
            AKJIPAddressDialog ipAddressDialog = new AKJIPAddressDialog(ipAddress, getPrefixAddresses(), dialogTitle,
                    allowedIpTypes);
            setTFParams(ipAddressDialog);
            Object result = DialogHelper.showDialog(this, ipAddressDialog, true, true);
            if (result instanceof IPAddress) {
                IPAddress ipAddress = (IPAddress) result;
                setIPAddress((ipAddress.getAddress() != null) ? ipAddress : null);
            }
        }
    }

    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    @Override
    public void setVisible(boolean b) {
        boolean x = isComponentVisible() && b;
        tfIPAddress.setVisible(x);
        btnEdit.setVisible(x);
    }

    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    @Override
    public boolean isComponentVisible() {
        return this.visible;
    }

    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        btnEdit.setEnabled(executable);
    }

    @Override
    public boolean isComponentExecutable() {
        return this.executable;
    }

    @Override
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    @Override
    public void setActiveColor(Color activeColor) {
        this.tfActiveColor = activeColor;
    }

    @Override
    public Color getActiveColor() {
        return tfActiveColor;
    }

    @Override
    public void setInactiveColor(Color inactiveColor) {
        this.tfInactiveColor = inactiveColor;
    }

    @Override
    public Color getInactiveColor() {
        return tfInactiveColor;
    }

    public void setSelectionColor(Color selectionColor) {
        this.tfSelectionColor = selectionColor;
    }

    public void setSelectedTextColor(Color selectedTextColor) {
        this.tfSelectedTextColor = selectedTextColor;
    }

    public void setColumns(int tfColumns) {
        this.tfColumns = tfColumns;
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean x = isComponentExecutable() && enabled;
        btnEdit.setEnabled(x);
    }

    /**
     * Setzt den Button auf enabled=usable.
     *
     * @param usable
     */
    public void setUsable(boolean usable) {
        boolean x = isComponentExecutable() && usable;
        btnEdit.setEnabled(x);
    }

    /**
     * Setzt den Tooltip-Text fuer den Edit Button
     */
    public void setButtonToolTipText(String text) {
        btnEdit.setToolTipText(text);
    }

    /**
     * Setzt den Text fuer den Edit Button.
     */
    public void setButtonText(String text) {
        btnEdit.setText(text);
    }

    /**
     * Versucht das Icon mit der URL <code>iconURL</code> zu laden und uebergibt dieses an den Edit Button.
     */
    public void setButtonIcon(String iconURL) {
        IconHelper helper = new IconHelper();
        ImageIcon icon = helper.getIcon(iconURL);
        if (icon != null) {
            btnEdit.setIcon(icon);
            btnEdit.setPreferredSize(new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2));
        }
    }

    /**
     * Titel fuer den IPAddress-Dialog
     */
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    /**
     * Setzt den Tooltip-Text fuer das TextField.
     */
    @Override
    public void setToolTipText(String text) {
        tfIPAddress.setToolTipText(text);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        adminML = l;
        super.addMouseListener(adminML);

        // Workaround: dem TextField und Button wird ein anderer
        // MouseListener uebergeben, der die Events mit einer
        // anderen Event-Source an <code>l</code> weiterleitet.
        IPAddressCompMouseListener ml = new IPAddressCompMouseListener(this);
        tfIPAddress.addMouseListener(ml);
        btnEdit.addMouseListener(ml);
    }

    /**
     * Fuegt der DateComponent den SearchKey-Listener <code>searchKL</code> hinzu.
     */
    public void addSearchKeyListener(AKSearchKeyListener searchKL) {
        IPAddressCompSearchKeyListener dcKL = new IPAddressCompSearchKeyListener(searchKL);
        tfIPAddress.addKeyListener(dcKL);
    }

    /**
     * Fuegt einen neuen Observer hinzu
     */
    public void addObserver(Observer observer) {
        publisher.addObserver(observer);
    }

    /**
     * MouseListener fuer das TextField und den Button, um dem AdminMouseListener die DateComponent und nicht das
     * TextField bzw. den Button als AKManageableComponent zu uebergeben.
     */
    private class IPAddressCompMouseListener extends MouseAdapter {
        AKJIPAddressComponent ipAdrC = null;

        /**
         * Konstruktor mit Angabe der DateComponent, die an den AdminMouseListener uebergeben werden soll.
         */
        public IPAddressCompMouseListener(AKJIPAddressComponent ipAdrC) {
            super();
            this.ipAdrC = ipAdrC;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            e.setSource(ipAdrC);
            adminML.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            e.setSource(ipAdrC);
            adminML.mouseReleased(e);
            if (e.isPopupTrigger() && !StringUtils.isBlank(tfIPAddress.getText())) {
                selectTextInTextField(findStringInTextField(ipAddress.getAbsoluteAddress(), tfIPAddress), tfIPAddress);
            }
        }
    }

    /**
     * SearchKeyListener fuer das TextField der IPAddress-Component.
     */
    static class IPAddressCompSearchKeyListener extends KeyAdapter {
        private AKSearchKeyListener searchKL = null;

        /**
         * Default-Const.
         */
        public IPAddressCompSearchKeyListener(AKSearchKeyListener searchKL) {
            super();
            this.searchKL = searchKL;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            searchKL.keyPressed(e);
        }
    }

    /**
     * Observers benachrichtigen
     */
    private static class Publisher extends AbstractObservable {
        private static final long serialVersionUID = 6810039914479155211L;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKCleanableComponent#clean()
     */
    @Override
    public void clean() {
        ipAddress = null;
    }

} // end
