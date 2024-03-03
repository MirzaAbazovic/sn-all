/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2010 13:17:36
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.innenauftrag.AbstractIaLevel;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.InnenauftragService;


/**
 * Dialog zur Abfrage von notwendigen Daten fuer ein neues Budget. Die Werte koennen ueber die Methoden - getBudget -
 * getResponsibleUserId abgefragt werden.
 */
public class CreateIABudgetDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final long serialVersionUID = -6461788415911458741L;

    private static final Logger LOGGER = Logger.getLogger(CreateIABudgetDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/innenauftrag/resources/CreateIABudgetDialog.xml";
    private static final String TITLE = "title";
    private static final String BUDGET = "budget";
    private static final String RESPONSIBLE = "responsible";
    private static final String IA_LEVEL_1 = "ia.level1";
    private static final String IA_LEVEL_3 = "ia.level3";
    private static final String IA_LEVEL_5 = "ia.level5";

    private final IA actIA;
    private AKJFormattedTextField tfBudget = null;
    protected AKReferenceField rfResponsible = null;
    private AKJComboBox cbIaLevel1 = null;
    private AKJComboBox cbIaLevel3 = null;
    private AKJComboBox cbIaLevel5 = null;
    private IABudget iaBudget;

    private InnenauftragService ias;
    private CCAuftragService ccAuftragService;
    private OEService oeService;

    public CreateIABudgetDialog(IA actIA) {
        super(RESOURCE);
        this.actIA = actIA;
        init();
        createGUI();
        loadData();
    }

    private void init() {
        try {
            ias = getCCService(InnenauftragService.class);
            ccAuftragService = getCCService(CCAuftragService.class);
            oeService = getBillingService(OEService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));

        AKJLabel lblBudget = getSwingFactory().createLabel(BUDGET);
        AKJLabel lblResponsible = getSwingFactory().createLabel(RESPONSIBLE);

        tfBudget = getSwingFactory().createFormattedTextField(BUDGET);
        tfBudget.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                process4Level3And5();
            }

            @Override
            public void insertUpdate(DocumentEvent e){
                process4Level3And5();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                process4Level3And5(); // wird das hier jemals aufgerufen?
            }
        });
        rfResponsible = getSwingFactory().createReferenceField(RESPONSIBLE);

        AKJLabel lblIaLevel1 = getSwingFactory().createLabel(IA_LEVEL_1);
        cbIaLevel1 = getSwingFactory().createComboBox(IA_LEVEL_1, new IaLevelCellRenderer());
        cbIaLevel1.addItemListener(this);
        resetComboBox(cbIaLevel1);
        AKJLabel lblIaLevel3 = getSwingFactory().createLabel(IA_LEVEL_3);
        cbIaLevel3 = getSwingFactory().createComboBox(IA_LEVEL_3, new IaLevelCellRenderer());
        cbIaLevel3.addItemListener(this);
        resetComboBox(cbIaLevel3);
        AKJLabel lblIaLevel5 = getSwingFactory().createLabel(IA_LEVEL_5);
        cbIaLevel5 = getSwingFactory().createComboBox(IA_LEVEL_5, new IaLevelCellRenderer());
        resetComboBox(cbIaLevel5);

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(lblBudget, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(tfBudget, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblResponsible, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(rfResponsible, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblIaLevel1, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbIaLevel1, GBCFactory.createGBC( 0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblIaLevel3, GBCFactory.createGBC( 0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbIaLevel3, GBCFactory.createGBC( 0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblIaLevel5, GBCFactory.createGBC( 0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbIaLevel5, GBCFactory.createGBC( 0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 5, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);

        }

    @Override
    public final void loadData() {
        try {
            final AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();

            rfResponsible.setReferenceList(createReferenceList());
            tfBudget.setValue(1000f);
            List<IaLevel1> iaLevel1s = ias.findIaLevelsForUser(currentUser);
            cbIaLevel1.addItems(iaLevel1s);

            if(currentUser.getBereich() != null)    {
                final Optional<IaLevel1> level1ToSet =
                        iaLevel1s.stream()
                        .filter(l -> l.getBereichName() != null)
                        .filter(l -> l.getBereichName().equals(currentUser.getBereich().getName()))
                        .findFirst();
                level1ToSet.ifPresent(l -> {
                    cbIaLevel1.setSelectedItem(l);
                    cbIaLevel1.setEnabled(!l.isLockMode());
                    rfResponsible.setEnabled(!l.isLockMode());
                    process4Level3And5();
                });
                final Long bereichsleiterId = currentUser.getBereich().getBereichsleiter();
                rfResponsible.setReferenceId(bereichsleiterId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected List<AKUser> createReferenceList() {
        try {
            final AKUserService userService = getAuthenticationService(AKUserService.class.getName(), AKUserService.class);
            final List<AKUser> managers = userService.findManagers();
            return managers;
        }
        catch (ServiceNotFoundException | AKAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSave() {
        try {
            Float budget = tfBudget.getValueAsFloat(null);
            Long responsibleUserId = rfResponsible.getReferenceIdAs(Long.class);

            if (budget == null) {
                throw new HurricanGUIException("Bitte geben Sie einen Betrag für das Budget an!");
            }

            if (responsibleUserId == null) {
                throw new HurricanGUIException("Bitte wählen Sie den zuständigen Budgetverantwortlichen aus!");
            }

            iaBudget = new IABudget();
            iaBudget.setIaId(actIA.getId());
            iaBudget.setCreatedAt(new Date());
            iaBudget.setBudget(budget);
            iaBudget.setBudgetUserId(HurricanSystemRegistry.instance().getCurrentUser().getId());
            iaBudget.setResponsibleUserId(responsibleUserId);

            final Either<String, IABudget> iaBudgetWithLevels = setSelectedLevels(iaBudget);
            if (iaBudgetWithLevels.isLeft()) {
                throw new HurricanGUIException(iaBudgetWithLevels.getLeft());
            }
            else {
                iaBudget = iaBudgetWithLevels.getRight();
            }

            ias.saveBudget(iaBudget, HurricanSystemRegistry.instance().getSessionId());

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    Either<String, IABudget> setSelectedLevels(final IABudget iaBudget) {
        final AbstractIaLevel iaLevel1 = (AbstractIaLevel) cbIaLevel1.getSelectedItem();
        final AbstractIaLevel iaLevel3 = (AbstractIaLevel) cbIaLevel3.getSelectedItem();
        final AbstractIaLevel iaLevel5 = (AbstractIaLevel) cbIaLevel5.getSelectedItem();

        final List<AbstractIaLevel> iaLevels = Lists.newArrayList(iaLevel1, iaLevel3, iaLevel5);

        final Either<String, IABudget> result;

        if (iaLevels.stream().allMatch(Objects::nonNull)) {
            iaBudget.setIaLevel1(iaLevel1.getName());
            iaBudget.setIaLevel1Id(iaLevel1.getSapId());
            iaBudget.setIaLevel3(iaLevel3.getName());
            iaBudget.setIaLevel3Id(iaLevel3.getSapId());
            iaBudget.setIaLevel5(iaLevel5.getName());
            iaBudget.setIaLevel5Id(iaLevel5.getSapId());
            result = Either.right(iaBudget);
        }
        else if (iaLevels.stream().filter(Objects::isNull).count() < iaLevels.size()) {
            result = Either.left("Bitte wählen Sie alle drei oder keine Ebene aus!");
        }
        else {
            result = Either.right(iaBudget);
        }
        return result;
    }

    public IABudget getIaBudget() {
        return iaBudget;
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private void resetComboBox(AKJComboBox cb) {
        cb.removeAllItems();
        cb.addItem(null);
    }

    private void updateLockMode4Level3() throws FindException {
        IaLevel3 iaLevel3 = ias.findLevel3Projekt4PD(actIA, getBudget4Text());
        cbIaLevel3.setSelectedItem(iaLevel3);
    }

    private float getBudget4Text() {
        float budget;
        try {
            budget = Float.valueOf(tfBudget.getText());
        }
        catch (Exception e) {
            budget = 0;
        }
        return budget;
    }

    private void updateLockMode4Level5() throws FindException {
        final IaLevel3 selectedLevel3Item = (IaLevel3) cbIaLevel3.getSelectedItem();
        final IaLevel3 grossprojekt = ias.findLevel3Grossprojekt4PD();
        if (grossprojekt != null && grossprojekt.equals(selectedLevel3Item)) {
            if (cbIaLevel5.getItemCount() == 2) {
                // index 0 = leerer Eintrag!
                cbIaLevel5.setSelectedIndex(1);
                cbIaLevel5.setEnabled(true);
            }
        }
        else {
            final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(actIA.getAuftragId());
            final String taifunProduktName = oeService.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig());

            final List<IaLevel5> iaLevel5s = ((IaLevel3) cbIaLevel3.getSelectedItem()).getLevel5s();
            final IaLevel5 iaLevel5 = ias.getLevel5ByTaifunProduktName(iaLevel5s, taifunProduktName);
            if (iaLevel5 == null) {
                cbIaLevel5.setEnabled(true);
            }
            else {
                cbIaLevel5.setSelectedItem(iaLevel5);
                cbIaLevel5.setEnabled(false);
            }
        }
    }

    private void process4Level3And5() {
        try {
            final IaLevel1 selectedLevel1Item = (IaLevel1) cbIaLevel1.getSelectedItem();
            if (selectedLevel1Item != null && selectedLevel1Item.isLockMode()) {
                cbIaLevel3.setEnabled(false);
                cbIaLevel5.setEnabled(false);
                updateLockMode4Level3();
                updateLockMode4Level5();
            }
            else {
                cbIaLevel3.setEnabled(true);
                cbIaLevel5.setEnabled(true);
            }
        }
        catch (FindException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == cbIaLevel1) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (cbIaLevel1.getSelectedItem() != null) {
                    IaLevel1 iaLevel1 = (IaLevel1) cbIaLevel1.getSelectedItem();
                    cbIaLevel3.addItems(iaLevel1.getLevel3s());
                }
            }
            else if (event.getStateChange() == ItemEvent.DESELECTED) {
                resetComboBox(cbIaLevel3);
                resetComboBox(cbIaLevel5);
            }
        }
        else if (event.getSource() == cbIaLevel3) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (cbIaLevel3.getSelectedItem() != null) {
                    IaLevel3 iaLevel3 = (IaLevel3) cbIaLevel3.getSelectedItem();
                    cbIaLevel5.addItems(iaLevel3.getLevel5s());
                }
            }
            else if (event.getStateChange() == ItemEvent.DESELECTED) {
                resetComboBox(cbIaLevel5);
            }
        }
    }

    static class IaLevelCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 4530554884570954233L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof AbstractIaLevel) {
                setText(((AbstractIaLevel)value).getName());
            }
            if (value == null) {
                setText(" ");
            }
            return comp;
        }

    }
}
