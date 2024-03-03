/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.bereich.BereichComboBox;
import de.augustakom.authentication.model.AKBereich;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKBereichService;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.innenauftrag.AbstractIaLevel;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;

/**
 *
 */
public class EditIaLevelDialog extends AbstractServiceOptionDialog {
    private static final long serialVersionUID = -1773221940244053720L;

    private static final Logger LOGGER = Logger.getLogger(EditIaLevelDialog.class);
    private Optional<BereichComboBox> bereichComboBox = Optional.empty();
    private Optional<AKJCheckBox> chbLocked = Optional.empty();
    private JComboBox<String> departmentComboBox = null;
    private final AbstractIaLevel level;
    private final DynamicTreeNode parent;
    AKJTextField name;
    AKJTextField sapId;

    public EditIaLevelDialog(String title, AbstractIaLevel level, DynamicTreeNode parent) {
        super("de/augustakom/hurrican/gui/innenauftrag/resources/EditIaLevelDialog.xml");
        this.level = level;
        this.parent = parent;
        if (this.level instanceof IaLevel1) {
            bereichComboBox = Optional.of(createBereichComboBoxAndSelect(((IaLevel1) this.level)));
            chbLocked = Optional.of(new AKJCheckBox("Auswahl Level1 sperren"));
        }
        else if (this.level instanceof IaLevel3) {
            departmentComboBox = getDepartments(((IaLevel3) level).getAbteilungsName());
        }
        createGUI();
        setTitle(title);
        name.setText(level.getName());
        sapId.setText(level.getSapId());
        chbLocked.ifPresent(chb -> chb.setSelected(((IaLevel1) level).isLockMode()));
    }

    private JComboBox<String> getDepartments(String departmentName) {
        departmentComboBox = new JComboBox<>(getAllDepartmentNames());
        departmentComboBox.setSelectedItem(departmentName);
        return departmentComboBox;
    }

    private BereichComboBox createBereichComboBoxAndSelect(final IaLevel1 iaLevel1) {
        return new BereichComboBox(getAllBereiche())
                .select(getBereichByName(iaLevel1.getBereichName()));
    }

    private String[] getAllDepartmentNames() {
        final String emptyElement = null;
        final List<String> departments = Lists.newArrayList(emptyElement);
        try {
            final List<String> values = getDepartmentService().findAll()
                    .stream().map(AKDepartment::getName).collect(Collectors.toList());
            departments.addAll(values);
        }
        catch (Exception e) {
            handleException(e);
        }
        return departments.toArray(new String[departments.size()]);
    }

    @Nonnull
    private List<AKBereich> getAllBereiche() {
        try {
            return getBereichService().findAll();
        }
        catch (ServiceNotFoundException e) {
            handleException(e);
            return Collections.emptyList();
        }
    }

    private AKBereichService getBereichService() throws ServiceNotFoundException {
        return getAuthenticationService(AKAuthenticationServiceNames.BEREICH_SERVICE, AKBereichService.class);
    }

    private AKDepartmentService getDepartmentService() throws ServiceNotFoundException {
        return getAuthenticationService(AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);
    }

    private void handleException(Exception e) {
        LOGGER.error(e);
        MessageHelper.showErrorDialog(this, e);
    }

    @Nullable
    private AKBereich getBereichByName(@CheckForNull final String bereichName) {
        try {
            return (bereichName == null)
                    ? null
                    : getBereichService().findByName(bereichName);
        }
        catch (ServiceNotFoundException e) {
            handleException(e);
            return null;
        }
    }

    @Override
    protected final void createGUI() {
        name = getSwingFactory().createTextField("name");
        sapId = getSwingFactory().createTextField("sapId");

        AKJPanel panel = new AKJPanel();
        panel.setLayout(new GridLayout(4, 3));
        panel.add(getSwingFactory().createLabel("name"));
        panel.add(name);
        panel.add(getSwingFactory().createLabel("sapId"));
        panel.add(sapId);
        bereichComboBox.ifPresent(cb -> {
            panel.add(new AKJLabel("Bereich"));
            panel.add(cb);
        });
        chbLocked.ifPresent(panel::add);
        if (departmentComboBox != null) {
            panel.add(new AKJLabel("Abteilung"));
            panel.add(departmentComboBox);
        }

        getChildPanel().setLayout(new FlowLayout());
        getChildPanel().add(panel);
    }

    @Override
    protected void execute(String command) { // NOSONAR squid:UnusedProtectedMethod ;
        //class does not listen to any actions
    }

    @Override
    protected void doSave() {
        if (StringUtils.isBlank(name.getText()) || StringUtils.isBlank(sapId.getText())) {
            MessageHelper.showWarningDialog(HurricanSystemRegistry.instance().getMainFrame(), "Alle Felder müssen einen Wert haben", true);
            return;
        }
        String newName = name.getText().trim();
        if (checkIsEqualLevelNamePresent(newName)) {
            MessageHelper.showWarningDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    String.format("Es existiert bereits ein Knoten mit Name \"%s\"", newName), true);
            return;
        }

        final Optional<String> levelNameOfEqualBereich = checkSelectedBereichIsAlreadyInUse();
        if (levelNameOfEqualBereich.isPresent()) {
            MessageHelper.showWarningDialog(this, String.format("Der Bereich ist bereits für den Level1 mit Namen %s gesetzt!", levelNameOfEqualBereich.get()), true);
            return;
        }

        level.setName(newName);
        level.setSapId(sapId.getText().trim());
        setBereichIfPresent();
        setLockIfPresent();
        setDepartmentIfPresent();

        prepare4Close();
        setValue(OK_OPTION);
    }

    private void setDepartmentIfPresent() {
        if (departmentComboBox != null) {
            String departmentName = null;
            if (departmentComboBox.getSelectedItem() != null)
                departmentName = (String) departmentComboBox.getSelectedItem();
            ((IaLevel3) level).setAbteilungsName(departmentName);
        }
    }

    private boolean checkIsEqualLevelNamePresent(String newName) {
        return StringUtils.isNotBlank(newName) && !newName.equals(level.getName())
                && parent.getChildren().stream().anyMatch(n -> ((AbstractIaLevel) n.getUserObject()).getName().equals(newName));
    }

    private Optional<String> checkSelectedBereichIsAlreadyInUse() {
        return bereichComboBox
                .flatMap(this::getSelectedBereich)
                .flatMap(this::determineLevelNameOfEqualBereich);
    }

    private Optional<String> determineLevelNameOfEqualBereich(final String bereichToSet) {
        return parent.getChildren().stream()
                .filter(n -> (n.getUserObject() instanceof IaLevel1))
                .map(n -> ((IaLevel1) n.getUserObject()))
                .filter(level1 -> !level1.equals(level))
                .filter(level1 -> StringUtils.equals(level1.getBereichName(), bereichToSet))
                .findFirst()
                .map(IaLevel1::getName);
    }

    private void setBereichIfPresent() {
        bereichComboBox.ifPresent(cb -> ((IaLevel1) this.level).setBereichName(getSelectedBereich(cb).orElse(null)));
    }

    private void setLockIfPresent() {
        chbLocked.ifPresent(chb -> ((IaLevel1) this.level).setLockMode(chb.isSelected()));
    }

    private Optional<String> getSelectedBereich(BereichComboBox cb) {
        return cb.getSelectedBereich().map(AKBereich::getName);
    }

    @Override
    public void update(Observable o, Object arg) {
        //class does not subscribe to any observables
    }
}
