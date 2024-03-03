/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 08:56:11
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.annotation.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.DepartmentListCellRenderer;
import de.augustakom.authentication.gui.basics.ExtServiceProviderListCellRenderer;
import de.augustakom.authentication.gui.basics.NiederlassungListCellRenderer;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.basics.TeamListCellRenderer;
import de.augustakom.authentication.gui.bereich.BereichComboBox;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKBereich;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKExtServiceProviderView;
import de.augustakom.authentication.model.AKNiederlassungView;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKBereichService;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;

/**
 * Panel zur Definition von User-Eigenschaften. <br>
 *
 *
 */
public class UserDataPanel extends AbstractAuthenticationPanel implements SavePanel, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(UserDataPanel.class);

    private static final String USER_ID = "user.id";
    private static final String USER_NAME = "user.name";
    private static final String USER_FIRSTNAME = "user.firstname";
    private static final String USER_LOGINNAME = "user.loginname";
    private static final String USER_EMAIL = "user.email";
    private static final String USER_PHONE = "user.phone";
    private static final String USER_PHONE_NEUTRAL = "user.phoneneutral";
    private static final String USER_FAX = "user.fax";
    private static final String USER_PROJEKTLEITER = "user.projektleiter";
    private static final String USER_ACTIVE = "user.active";
    private static final String DEPARTMENTS = "departments";
    private static final String NIEDERLASSUNG = "niederlassung";
    private static final String EXT_PARTNER = "ext.partner";
    private static final String USER_TEAM = "user.team";

    private static final AKTeam EMPTY_TEAM_DUMMY = new AKTeam("----");
    private static final AKBereich EMPTY_BEREICH = new AKBereich("----", 0L);
    public static final String USER_BEREICH = "user.bereich";

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextField tfFirstName = null;
    private AKJTextField tfLoginName = null;
    private AKJTextField tfEMail = null;
    private AKJTextField tfPhone = null;
    private AKJTextField tfPhoneNeutral = null;
    private AKJTextField tfFax = null;
    private AKJCheckBox chbActive = null;
    private AKJCheckBox chbProjektleiter = null;
    private AKJComboBox cobDepartment = null;
    private AKJComboBox cbNiederlassung = null;
    private AKJComboBox cbExtPartner = null;
    private AKJComboBox cbTeam;
    private BereichComboBox cbBereich;

    private AKUser model = null;

    /**
     * Konstruktor fuer das Panel mit Angabe eines User-Objekts.
     *
     * @param model
     */
    public UserDataPanel(AKUser model) {
        super("de/augustakom/authentication/gui/user/resources/UserDataPanel.xml");
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel(USER_ID);
        AKJLabel lblName = getSwingFactory().createLabel(USER_NAME);
        AKJLabel lblFirstName = getSwingFactory().createLabel(USER_FIRSTNAME);
        AKJLabel lblLoginName = getSwingFactory().createLabel(USER_LOGINNAME);
        AKJLabel lblEMail = getSwingFactory().createLabel(USER_EMAIL);
        AKJLabel lblPhone = getSwingFactory().createLabel(USER_PHONE);
        AKJLabel lblPhoneNeutral = getSwingFactory().createLabel(USER_PHONE_NEUTRAL);
        AKJLabel lblFax = getSwingFactory().createLabel(USER_FAX);
        AKJLabel lblProjektleiter = getSwingFactory().createLabel(USER_PROJEKTLEITER);
        AKJLabel lblDepartment = getSwingFactory().createLabel(DEPARTMENTS);
        AKJLabel lblActive = getSwingFactory().createLabel(USER_ACTIVE);
        AKJLabel lblNiederlassung = getSwingFactory().createLabel(NIEDERLASSUNG);
        AKJLabel lblExtPartner = getSwingFactory().createLabel(EXT_PARTNER);
        AKJLabel lblTeam = getSwingFactory().createLabel(USER_TEAM);
        AKJLabel lblBereich = getSwingFactory().createLabel(USER_BEREICH);

        tfId = getSwingFactory().createFormattedTextField(USER_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(USER_NAME);
        tfFirstName = getSwingFactory().createTextField(USER_FIRSTNAME);
        tfLoginName = getSwingFactory().createTextField(USER_LOGINNAME);
        tfEMail = getSwingFactory().createTextField(USER_EMAIL);
        tfPhone = getSwingFactory().createTextField(USER_PHONE);
        tfPhoneNeutral = getSwingFactory().createTextField(USER_PHONE_NEUTRAL);
        tfFax = getSwingFactory().createTextField(USER_FAX);
        chbProjektleiter = getSwingFactory().createCheckBox(USER_PROJEKTLEITER);
        cobDepartment = getSwingFactory().createComboBox(DEPARTMENTS);
        cobDepartment.setRenderer(new DepartmentListCellRenderer());
        chbActive = getSwingFactory().createCheckBox(USER_ACTIVE);
        cbNiederlassung = getSwingFactory().createComboBox(NIEDERLASSUNG);
        cbNiederlassung.setRenderer(new NiederlassungListCellRenderer());
        cbExtPartner = getSwingFactory().createComboBox(EXT_PARTNER);
        cbExtPartner.setRenderer(new ExtServiceProviderListCellRenderer());
        cbTeam = getSwingFactory().createComboBox(USER_TEAM, new TeamListCellRenderer());
        cbBereich = new BereichComboBox(findAllBereiche());

        AKJPanel prefPanelL = new AKJPanel();
        prefPanelL.setPreferredSize(new Dimension(100, 10));

        AKJPanel prefPanelR = new AKJPanel();
        prefPanelR.setPreferredSize(new Dimension(100, 10));

        //@formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new JPanel()   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(prefPanelL     , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblId          , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new JPanel()   , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfId           , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblName        , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfName         , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblFirstName   , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfFirstName    , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLoginName   , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLoginName    , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEMail       , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfEMail        , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPhone       , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPhone        , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPhoneNeutral, GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPhoneNeutral , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblFax         , GBCFactory.createGBC(  0,  0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfFax          , GBCFactory.createGBC(100,  0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProjektleiter,GBCFactory.createGBC(  0,  0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbProjektleiter,GBCFactory.createGBC(100,  0, 3, 9, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new JPanel()    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(prefPanelR      , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblBereich      , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbBereich       , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblDepartment   , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cobDepartment   , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblTeam         , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbTeam          , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblNiederlassung, GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbNiederlassung , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblExtPartner   , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbExtPartner    , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblActive       , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbActive       , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(left             , GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(new JPanel()     , GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(right            , GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(new JPanel()     , GBCFactory.createGBC(100, 100, 3, 0, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on

        read();

        cobDepartment.addItemListener(this);
    }

    private List<AKBereich> findAllBereiche() {
        try {
            return getAuthenticationService(AKAuthenticationServiceNames.BEREICH_SERVICE, AKBereichService.class).findAll();
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            return Collections.emptyList();
        }
    }

    /* Liest die Model-Daten aus und stellt sie in den TextFields dar. */
    private void read() {
        readDepartments();

        if (model.getId() != null) {
            // Daten vom Modell anzeigen
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
            tfFirstName.setText(model.getFirstName());
            tfLoginName.setText(model.getLoginName());
            tfEMail.setText(model.getEmail());
            tfPhone.setText(model.getPhone());
            tfPhoneNeutral.setText(model.getPhoneNeutral());
            tfFax.setText(model.getFax());
            chbProjektleiter.setSelected(model.isProjektleiter());

            chbActive.setSelected(model.isActive());

            // Department auswaehlen
            selectCBs(model.getDepartmentId(), model.getNiederlassungId(), model.getExtServiceProviderId(), model.getBereich());
        }
        else {
            // Default-Daten anzeigen
            tfEMail.setText("@m-net.de");
            chbActive.setSelected(true);
            selectCBs(model.getDepartmentId(), model.getNiederlassungId(), model.getExtServiceProviderId(), null);
        }

        readTeams(); // Department muss vor Aufruf selektiert sein
    }

    private void readTeams() {
        DefaultComboBoxModel teamCbModel = new DefaultComboBoxModel();
        cbTeam.setModel(teamCbModel);
        cbTeam.addItem(EMPTY_TEAM_DUMMY);
        if (cobDepartment.getSelectedItem() != null) {
            AKDepartment departmentSelected = (AKDepartment) cobDepartment.getSelectedItem();
            List<AKTeam> teams = departmentSelected.getTeams();
            cbTeam.addItems(teams);
        }
        AKTeam usersTeam = model.getTeam();
        cbTeam.selectItem("getId", AKTeam.class, (usersTeam != null) ? usersTeam.getId() : null);
    }

    /* Selektiert die Eintraege der ComboBoxen */
    private void selectCBs(Long depId, Long niederlassungId, Long extServiceProviderId, @CheckForNull AKBereich bereich) {
        if (depId != null) {
            for (int i = 0; i < cobDepartment.getModel().getSize(); i++) {
                Object obj = cobDepartment.getModel().getElementAt(i);
                if ((obj instanceof AKDepartment) && (((AKDepartment) obj).getId() != null)
                        && NumberTools.equal(((AKDepartment) obj).getId(), depId)) {
                    cobDepartment.setSelectedItem(obj);
                    break;
                }
            }
        }
        else {
            cobDepartment.setSelectedItem(null);
        }
        if (niederlassungId != null) {
            for (int i = 0; i < cbNiederlassung.getModel().getSize(); i++) {
                Object obj = cbNiederlassung.getModel().getElementAt(i);
                if ((obj instanceof AKNiederlassungView) && (((AKNiederlassungView) obj).getId() != null)
                        && (NumberTools.equal(((AKNiederlassungView) obj).getId(), niederlassungId))) {
                    cbNiederlassung.setSelectedItem(obj);
                    break;
                }
            }
        }
        else {
            cbNiederlassung.setSelectedItem(null);
        }
        if (extServiceProviderId != null) {
            for (int i = 0; i < cbExtPartner.getModel().getSize(); i++) {
                Object obj = cbExtPartner.getModel().getElementAt(i);
                if ((obj instanceof AKExtServiceProviderView) && (((AKExtServiceProviderView) obj).getId() != null)
                        && (NumberTools.equal(((AKExtServiceProviderView) obj).getId(), extServiceProviderId))) {
                    cbExtPartner.setSelectedItem(obj);
                    break;
                }
            }
        }
        else {
            cbExtPartner.setSelectedItem(null);
        }
        cbBereich.select(bereich);
    }

    /* Liest alle AKDepartment-Objekte aus und traegt sie in ein ComboBoxModel ein. */
    private void readDepartments() {
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
        DefaultComboBoxModel cbModelNl = new DefaultComboBoxModel();
        DefaultComboBoxModel cbModelSp = new DefaultComboBoxModel();
        try {
            AKDepartmentService depService = getAuthenticationService(
                    AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);

            // Abteilungen
            List<AKDepartment> departments = depService.findAll();
            if (departments != null) {
                for (int i = 0; i < departments.size(); i++) {
                    cbModel.addElement(departments.get(i));
                }
            }

            // Niederlassungen
            List<AKNiederlassungView> nls = depService.findAllNiederlassungen();
            if (nls != null) {
                cbModelNl.addElement(new AKNiederlassungView());
                for (int i = 0; i < nls.size(); i++) {
                    cbModelNl.addElement(nls.get(i));
                }
            }

            // Ext. Partner
            List<AKExtServiceProviderView> sps = depService.findAllExtServiceProvider();
            if (sps != null) {
                cbModelSp.addElement(new AKExtServiceProviderView());
                for (int i = 0; i < sps.size(); i++) {
                    cbModelSp.addElement(sps.get(i));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
        finally {
            cobDepartment.setModel(cbModel);
            cbNiederlassung.setModel(cbModelNl);
            cbExtPartner.setModel(cbModelSp);
        }
    }

    /**
     * @see de.augustakom.authentication.gui.basics.SavePanel#doSave()
     */
    @Override
    public void doSave() throws GUIException {
        try {
            setWaitCursor();

            model.setName(tfName.getText());
            model.setFirstName(tfFirstName.getText());
            model.setLoginName(tfLoginName.getText());
            model.setEmail(tfEMail.getText());
            model.setPhone(tfPhone.getText());
            model.setPhoneNeutral(tfPhoneNeutral.getText());
            model.setFax(tfFax.getText());
            model.setProjektleiter(chbProjektleiter.isSelected());
            AKDepartment dep = (AKDepartment) cobDepartment.getSelectedItem();
            model.setDepartmentId((dep != null) ? dep.getId() : null);
            model.setActive(chbActive.isSelected());
            AKNiederlassungView nl = (AKNiederlassungView) cbNiederlassung.getSelectedItem();
            model.setNiederlassungId((nl != null) ? nl.getId() : null);
            AKExtServiceProviderView sp = (AKExtServiceProviderView) cbExtPartner.getSelectedItem();
            model.setExtServiceProviderId((sp != null) ? sp.getId() : null);
            model.setTeam((cbTeam.getSelectedItem() == EMPTY_TEAM_DUMMY) ? null : (AKTeam) cbTeam.getSelectedItem());
            model.setBereich(cbBereich.getSelectedBereich().orElse(null));
            validateModel(model);

            try {
                AKUserService userService = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE,
                        AKUserService.class);
                userService.save(model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new GUIException(GUIException.USER_SAVING_ERROR, e);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Validiert das AKUser-Objekt. <br> Werden ungueltige Daten festgestellt, wird eine GUIException geworfen.
     *
     * @param user Objekt, das validiert werden soll.
     * @throws GUIException wenn ungueltige Daten festgestellt werden.
     */
    private void validateModel(AKUser user) throws GUIException {
        if (StringUtils.isBlank(user.getLoginName())) {
            throw new GUIException(GUIException.USER_VALIDATE_LOGINNAME);
        }

        if (StringUtils.isBlank(user.getName())) {
            throw new GUIException(GUIException.USER_VALIDATE_NAME);
        }

        if (StringUtils.isBlank(user.getFirstName())) {
            throw new GUIException(GUIException.USER_VALIDATE_FIRSTNAME);
        }

        if (user.getDepartmentId() == null) {
            throw new GUIException(GUIException.USER_VALIDATE_DEPARTMENT);
        }

        if (NumberTools.equal(user.getDepartmentId(), AKDepartment.DEP_EXTERN)
                && (user.getExtServiceProviderId() == null)) {
            throw new GUIException(GUIException.USER_VALIDATE_DEP_EXTERN);
        }

        if ((user.getExtServiceProviderId() != null)
                && !NumberTools.equal(user.getDepartmentId(), AKDepartment.DEP_EXTERN)) {
            throw new GUIException(GUIException.USER_VALIDATE_EXT_PARTNER);
        }

        if ((user.getNiederlassungId() == null) && !NumberTools.equal(user.getDepartmentId(), AKDepartment.DEP_EXTERN)) {
            throw new GUIException(GUIException.USER_VALIDATE_NIEDERLASSUNG);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if ((e.getStateChange() == ItemEvent.SELECTED) && (e.getItem() instanceof AKDepartment)) {
            AKDepartment selectedDepartment = (AKDepartment) e.getItem();
            cbTeam.removeAllItems();
            cbTeam.addItem(EMPTY_TEAM_DUMMY);
            cbTeam.addItems(selectedDepartment.getTeams());
        }
    }
}
