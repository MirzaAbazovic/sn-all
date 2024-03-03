/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2007 14:44:47
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.accessibility.*;
import javax.swing.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;


/**
 * GUI-Komponente fuer Referenz-Daten. <br> Die Komponente enthaelt ein TextField und einen Button. <br> Durch die
 * Uebergabe einer Referenz-ID und eines Referenz-Typs ist die Komponente in der Lage, das notwendige Referenz-Objekt
 * selbststaendig zu laden (vorausgesetzt, es ist ein entsprechender 'FindService' uebergeben). <br> Die Komponente
 * besitzt ausserdem die Moeglichkeit, weitere Referenzen ueber eine GUI anzuzeigen und auswaehlen zu lassen.
 *
 *
 */
public class AKReferenceField extends AKJPanel implements AKManageableComponent,
        ActionListener, AKSwingConstants, AKReferenceFieldEvent {

    private static final Logger LOGGER = Logger.getLogger(AKReferenceField.class);

    private static final String REF_SELECTION_ITEM = "de/augustakom/common/gui/images/reference.gif";

    /**
     * Property-Name fuer Aenderungen der Reference-ID
     */
    public static final String PROPERTY_CHANGE_REFERENCE_ID = "referenceId";

    private static final int MAX_SIZE_4_POPUP = 15;

    private boolean executable = true;
    private boolean visible = true;

    private AKJTextField tfRef = null;
    private AKJButton btnRefSel = null;
    private ClearRefAction clearReferenceAction = null;

    private Collection<?> referenceList = null;
    private Class<?> referenceModel = null;
    private String referenceIdProperty = null;
    private Object referenceId = null;
    private String referenceShowProperty = null;
    private String referenceTooltipProperty = null;
    private Object referenceObject = null;
    private Object referenceFindExample = null;
    private ISimpleFindService findService = null;
    private String selectionDialogClass = null;
    private ImageIcon dialogIcon = null;
    private boolean showClearItem = true;
    private boolean referencesDefined = false;

    // temp. Reference-Objekt, um auch Referenzen von nicht-persistenten Objekten
    // anzeigen zu koennen (z.B. von View-Modellen).
    private Object refObjectTmp = null;

    private final List<AKReferenceFieldObserver> observers = new ArrayList<AKReferenceFieldObserver>();

    /**
     * Default-Const.
     */
    public AKReferenceField() {
        super();
        init();
    }

    /* Initialisiert die GUI-Komponente */
    private void init() {
        refObjectTmp = null;
        tfRef = new AKJTextField();
        tfRef.setEditable(false);

        clearReferenceAction = new ClearRefAction();
        AKJPopupMenu popup = new AKJPopupMenu();
        popup.add(clearReferenceAction);
        tfRef.setComponentPopupMenu(popup);

        btnRefSel = new AKJButton();
        IconHelper ih = new IconHelper();
        Icon icon = ih.getIcon(REF_SELECTION_ITEM);
        if (icon != null) {
            btnRefSel.setIcon(icon);
            btnRefSel.setBorder(BorderFactory.createEtchedBorder());
        }
        else {
            btnRefSel.setText("...");
        }

        btnRefSel.setPreferredSize(new Dimension(20, tfRef.getPreferredSize().height));
        btnRefSel.addActionListener(this);

        setLayout(new GridBagLayout());
        Insets insetsTF = new Insets(0, 0, 0, 0);
        Insets insetsBTN = new Insets(0, 2, 0, 0);
        this.add(tfRef, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, insetsTF));
        this.add(btnRefSel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, insetsBTN));
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        return tfRef.getAccessibleContext();
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        tfRef.addMouseListener(l);
        super.addMouseListener(l);
    }

    /**
     * Setzt den Tooltip-Text fuer das TextField der Date-Komponente.
     *
     * @see javax.swing.JComponent#setToolTipText(java.lang.String)
     */
    @Override
    public void setToolTipText(String text) {
        tfRef.setToolTipText(text);
    }

    /**
     * @param columns
     */
    public void setColumns(int columns) {
        tfRef.setColumns(columns);
    }

    /**
     * Setzt den Tooltip-Text fuer den Button der Date-Komponente.
     *
     * @param text
     */
    public void setButtonToolTipText(String text) {
        btnRefSel.setToolTipText(text);
    }

    /**
     * Setzt den Text fuer den Button der Date-Komponente.
     *
     * @param text
     */
    public void setButtonText(String text) {
        btnRefSel.setText(text);
    }

    /**
     * Versucht das Icon mit der URL <code>iconURL</code> zu laden und uebergibt dieses an den Button.
     *
     * @param iconURL
     */
    public void setButtonIcon(String iconURL) {
        IconHelper helper = new IconHelper();
        ImageIcon icon = helper.getIcon(iconURL);
        if (icon != null) {
            btnRefSel.setIcon(icon);
            btnRefSel.setPreferredSize(new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2));
        }
    }

    /**
     * Versucht das Icon mit der URL <code>iconURL</code> zu laden und uebergibt dieses an den Dialog.
     *
     * @param iconURL
     */
    public void setDialogIcon(String iconURL) {
        IconHelper helper = new IconHelper();
        dialogIcon = helper.getIcon(iconURL);
    }

    /**
     * Setzt die TextFarbe des TextFields.
     */
    @Override
    public void setForeground(Color foreground) {
        if (tfRef != null) {
            tfRef.setForeground(foreground);
        }
        else {
            super.setForeground(foreground);
        }
    }

    /**
     * Setzt die Hintergrundfarbe des TextFields.
     */
    @Override
    public void setBackground(Color background) {
        if (tfRef != null) {
            tfRef.setBackground(background);
        }
        else {
            super.setBackground(background);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        btnRefSel.setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return tfRef.isManagementCalled();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
    public void setManagementCalled(boolean called) {
        tfRef.setManagementCalled(true);
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        tfRef.setEditable(x);
        btnRefSel.setEnabled(x);
        clearReferenceAction.setEnabled(x);
        super.setEnabled(x);
    }

    /**
     * @see javax.swing.text.JTextComponent#setEditable(boolean)
     */
    public void setEditable(boolean editable) {
        tfRef.setEditable(editable);
    }

    /**
     * @return Returns the referenceList.
     */
    public Collection<?> getReferenceList() {
        return referenceList;
    }

    /**
     * Uebergibt dem Referenz-Feld eine Collection mit den zur Auswahl stehenden Objekten.
     *
     * @param referenceList The referenceList to set.
     */
    public void setReferenceList(Collection<?> referenceList) {
        this.referenceList = referenceList;
        referencesDefined = true;
    }

    /**
     * @return Returns the selectionDialogClass.
     */
    public String getSelectionDialogClass() {
        return selectionDialogClass;
    }

    /**
     * Definiert fuer das Reference-Feld einen speziellen Such-Dialog fuer die Auswahl der Referenz. <br> Der Suchdialog
     * muss vom Typ <code>AKJOptionDialog</code> sein und einen Default-Konstruktor besitzen.
     *
     * @param selectionDialogClass The selectionDialogClass to set.
     */
    public void setSelectionDialogClass(String selectionDialogClass) {
        this.selectionDialogClass = selectionDialogClass;
    }

    /**
     * @return Returns the referenceModel.
     */
    public Class<?> getReferenceModel() {
        return referenceModel;
    }

    /**
     * Definiert, welche Art von Referenz-Modell verwendet werden soll.
     *
     * @param referenceModel The referenceModel to set.
     */
    public void setReferenceModel(Class<?> referenceModel) {
        this.referenceModel = referenceModel;
    }

    /**
     * @return Returns the referenceIdProperty.
     */
    public String getReferenceIdProperty() {
        return referenceIdProperty;
    }

    /**
     * Definiert den Property-Namen des Referenz-Modells, der als ID dient.
     *
     * @param referenceIdProperty The referenceIdProperty to set.
     */
    public void setReferenceIdProperty(String referenceIdProperty) {
        this.referenceIdProperty = referenceIdProperty;
    }

    /**
     * @return Returns the referenceId.
     */
    public Object getReferenceId() {
        return referenceId;
    }

    /**
     * Uebergibt dem Reference-Field die ID des referenzierenden Modells. <br> Die Aenderung der ID hat ein
     * PropertyChangeEvent fuer Property 'referenceId' zur Folge.
     *
     * @param referenceId The referenceId to set.
     */
    public void setReferenceId(Object referenceId) {
        Object oldRefId = getReferenceId();

        this.referenceId = referenceId;
        loadReference();

        firePropertyChange(PROPERTY_CHANGE_REFERENCE_ID, oldRefId, referenceId);

        try {
            notifyObservers();
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * @return Returns the referenceShowProperty.
     */
    public String getReferenceShowProperty() {
        return referenceShowProperty;
    }

    /**
     * Definiert das Property des Referenz-Modells, das zur Anzeige ausgelesen werden soll.
     *
     * @param referenceShowProperty The referenceShowProperty to set.
     */
    public void setReferenceShowProperty(String referenceShowProperty) {
        this.referenceShowProperty = referenceShowProperty;
    }

    /**
     * @return the referenceTooltipProperty
     */
    public String getReferenceTooltipProperty() {
        return referenceTooltipProperty;
    }

    /**
     * @param referenceTooltipProperty the referenceTooltipProperty to set
     */
    public void setReferenceTooltipProperty(String referenceTooltipProperty) {
        this.referenceTooltipProperty = referenceTooltipProperty;
    }

    /**
     * @return Returns the referenceObject.
     */
    public Object getReferenceObject() {
        return referenceObject;
    }

    /**
     * Gibt das aktuell selektierte Objekt zurueck, falls es vom Typ <T> ist; andernfalls {@code null}.
     *
     * @param <T>
     * @param requestedType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getReferenceObjectAs(Class<T> requestedType) {
        Object tmp = getReferenceObject();
        if (requestedType.isInstance(tmp)) {
            return (T) tmp;
        }
        else {
            return null;
        }
    }

    /**
     * Uebergibt dem Reference-Field das referenzierte Objekt.
     *
     * @param referenceObject The referenceObject to set.
     */
    public void setReferenceObject(Object referenceObject) {
        this.referenceObject = referenceObject;
        showReferenceValue();
    }

    /**
     * @return Returns the referenceFindExample.
     */
    public Object getReferenceFindExample() {
        return referenceFindExample;
    }

    /**
     * Uebergibt dem Reference-Field ein Example-Objekt, ueber das weitere Referenz-Objekte fuer die Auswahl ermittelt
     * werden sollen. <br> Durch die Uebergabe eines solchen Objekts kann die Ergebnismenge eingeschraenkt werden.
     *
     * @param referenceFindExample The referenceFindExample to set.
     */
    public void setReferenceFindExample(Object referenceFindExample) {
        this.referenceFindExample = referenceFindExample;
    }

    /**
     * @return Returns the findService.
     */
    public ISimpleFindService getFindService() {
        return findService;
    }

    /**
     * @param findService The findService to set.
     */
    public void setFindService(ISimpleFindService findService) {
        this.findService = findService;
    }

    /**
     * @return Returns the showClearItem.
     */
    public boolean isShowClearItem() {
        return showClearItem;
    }

    /**
     * @param showClearItem The showClearItem to set.
     */
    public void setShowClearItem(boolean showClearItem) {
        this.showClearItem = showClearItem;
    }

    /**
     * Ueberprueft, ob die aktuelle Reference-ID vom Typ <code>requestedType</code> ist. Ist dies der Fall, wird die ID
     * zurueck gegeben, sonst 'null'.
     *
     * @param requestedType Typ des erwarteten Results.
     * @return Objekt oder <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public <T> T getReferenceIdAs(Class<T> requestedType) {
        Object tmp = getReferenceId();
        if (requestedType.isInstance(tmp)) {
            return (T) tmp;
        }
        else {
            return null;
        }
    }

    /*
     * Laedt das referenzierte Objekt ueber die angegebene ID.
     */
    private void loadReference() {
        if (getReferenceId() != null) {
            try {
                if (!referencesDefined) {
                    Object ref = getFindService().findById((Serializable) getReferenceId(), getReferenceModel());
                    setReferenceObject(ref);
                }
                else {
                    // Reference in ReferenceList suchen
                    if (CollectionTools.isNotEmpty(getReferenceList())) {
                        for (Object o : getReferenceList()) {
                            Object refValue = PropertyUtils.getProperty(o, getReferenceIdProperty());
                            if (getReferenceId().equals(refValue)) {
                                setReferenceObject(o);
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage(), e);

                try {
                    if (refObjectTmp != null) {
                        // falls Referenz-Objekt nicht geladen werden kann, wird versucht,
                        // das temp. Referenz-Modell zu verwenden (sofern gesetzt).
                        setReferenceObject(refObjectTmp);
                        refObjectTmp = null;
                    }
                    else if (referencesDefined && CollectionTools.isNotEmpty(getReferenceList())) {
                        // Reference in ReferenceList suchen
                        for (Object o : getReferenceList()) {
                            Object refValue = PropertyUtils.getProperty(o, getReferenceIdProperty());
                            if (getReferenceId().equals(refValue)) {
                                setReferenceObject(o);
                                break;
                            }
                        }
                    }
                }
                catch (Exception ex) {
                    LOGGER.info(ex.getMessage(), e);
                }
            }
        }
        else {
            setReferenceObject(null);
        }
    }

    /*
     * Ermittelt den anzuzeigenden Referenzwert und uebergibt diesen
     * dem TextField.
     */
    private void showReferenceValue() {
        String showValue = null;
        if (getReferenceObject() != null) {
            try {
                if (StringUtils.isNotBlank(getReferenceShowProperty())) {
                    Object value = PropertyUtils.getProperty(getReferenceObject(), getReferenceShowProperty());
                    if (value != null) {
                        showValue = value.toString();
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (showValue == null) {
            if (getReferenceId() != null) {
                showValue = "" + getReferenceId();
            }
            else {
                showValue = "";
            }
        }

        tfRef.setText(showValue);
    }

    /**
     * 'Loescht' das Feld.
     *
     *
     */
    public void clearReference() {
        setReferenceId(null);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if ((getFindService() == null) && CollectionTools.isEmpty(getReferenceList())) {
                MessageHelper.showMessageDialog(this, "FindService ist nicht definiert!",
                        "Service", JOptionPane.OK_OPTION);
            }

            if (getReferenceModel() != null) {
                if (StringUtils.isNotBlank(getSelectionDialogClass())) {
                    // definierten Selection-Dialog oeffnen
                    Object tmp = Class.forName(getSelectionDialogClass()).newInstance();
                    if (tmp instanceof AKJOptionDialog) {
                        AKJOptionDialog dlg = (AKJOptionDialog) tmp;
                        if (dialogIcon != null) {
                            dlg.setIcon(dialogIcon);
                        }
                        Object result = DialogHelper.showDialog(this, dlg, true, true);
                        if ((result != null) && !((result instanceof Integer)
                                && (JOptionPane.CANCEL_OPTION == ((Integer) result).intValue()))) {
                            refObjectTmp = result;
                            setReferenceId(PropertyUtils.getProperty(result, getReferenceIdProperty()));
                        }
                    }
                    else {
                        MessageHelper.showMessageDialog(this, "Angegebener Dialog ist nicht vom Typ AKJOptionDialog!",
                                "Dialog", JOptionPane.OK_OPTION);
                    }
                }
                else {
                    Collection<?> refs = null;
                    if (referencesDefined) {
                        refs = getReferenceList();
                    }
                    else {
                        if (getReferenceFindExample() == null) {
                            refs = getFindService().findAll(getReferenceModel());
                        }
                        else {
                            Object example = getReferenceFindExample();
                            refs = getFindService().findByExample(example, example.getClass());
                        }
                    }

                    // Anzahl <= 15 --> Anzeige ueber DropDown, sonst ueber Dialog
                    if (CollectionTools.isNotEmpty(refs)) {
                        if (refs.size() <= MAX_SIZE_4_POPUP) {
                            AKJPopupMenu popup = new AKJPopupMenu();
                            Iterator<?> it = refs.iterator();
                            while (it.hasNext()) {
                                Object next = it.next();
                                StringBuilder name = new StringBuilder();
                                name.append(PropertyUtils.getProperty(next, getReferenceIdProperty()));
                                if (StringUtils.isNotBlank(getReferenceShowProperty())) {
                                    name.append("  ");
                                    name.append(PropertyUtils.getProperty(next, getReferenceShowProperty()));
                                }

                                SelectRefAction srAction = new SelectRefAction();
                                srAction.putValue(SelectRefAction.OBJECT_4_ACTION, next);
                                srAction.setName(name.toString());

                                if (StringUtils.isNotBlank(getReferenceTooltipProperty())) {
                                    Object ttObj = PropertyUtils.getProperty(next, getReferenceTooltipProperty());
                                    srAction.setTooltip((ttObj != null) ? ttObj.toString() : null);
                                }

                                popup.add(srAction);
                            }

                            if (isShowClearItem() && (getReferenceId() != null)) {
                                ClearRefAction clear = new ClearRefAction();
                                popup.addSeparator();
                                popup.add(clear);
                            }

                            popup.show(btnRefSel, 0, btnRefSel.getHeight() + 1);
                        }
                        else {
                            AKSelectReferenceDialog dlg = new AKSelectReferenceDialog(
                                    refs, getReferenceModel(), getReferenceIdProperty(), getReferenceShowProperty());
                            Object result = DialogHelper.showDialog(this, dlg, true, true);
                            if (result != null) {
                                setReferenceId(PropertyUtils.getProperty(result, getReferenceIdProperty()));
                                notifyObservers();
                            }
                        }
                    }
                    else {
                        MessageHelper.showMessageDialog(this, "Keine Referenzen gefunden!",
                                "Referenzen", JOptionPane.OK_OPTION);
                    }
                }
            }
            else {
                MessageHelper.showMessageDialog(this, "Referenzdaten sind nicht definiert!",
                        "Referenzen", JOptionPane.OK_OPTION);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.Subject#addObserver(de.augustakom.common.gui.swing.Observer)
     */
    @Override
    public void addObserver(AKReferenceFieldObserver o) {
        observers.add(o);
    }

    /**
     * @see de.augustakom.common.gui.swing.Subject#removeObserver(de.augustakom.common.gui.swing.Observer)
     */
    @Override
    public void removeObserver(AKReferenceFieldObserver o) {
        observers.remove(o);
    }

    /**
     * If this object has changed, then notify all of its observers
     *
     * @throws Exception
     */
    private void notifyObservers() throws Exception {
        Iterator<AKReferenceFieldObserver> observerIterator = observers.iterator();

        while (observerIterator.hasNext()) {
            AKReferenceFieldObserver akReferenceFieldObserver = observerIterator.next();
            akReferenceFieldObserver.update(this);
        }
    }

    /**
     * Action fuer die Auswahl einer Referenz ueber ein PopupMenu
     */
    class SelectRefAction extends AKAbstractAction {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object ref = getValue(OBJECT_4_ACTION);
                setReferenceId(PropertyUtils.getProperty(ref, getReferenceIdProperty()));
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Action, um eine Referenz zu entfernen.
     */
    class ClearRefAction extends AKAbstractAction {
        /**
         * Default-Const.
         */
        public ClearRefAction() {
            setName("<entfernen>");
            setTooltip("Entfernt die aktuelle Referenz");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                setReferenceId(null);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

}


