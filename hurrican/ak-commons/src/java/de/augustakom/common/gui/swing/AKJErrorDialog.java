/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 08:19:46
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.persistence.*;
import javax.swing.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.tools.validation.ValidationException;


/**
 * Implementierung eines Error-Dialogs. <br> Der Dialog zeigt eine Exception auf zwei TabbedPanes ('Fehlertext' und
 * 'Details') an.
 *
 *
 */
public class AKJErrorDialog extends AKJAbstractOptionDialog {

    private static final long serialVersionUID = -9126391633644041178L;
    private static final String RESOURCE = "de/augustakom/common/gui/resources/AKJErrorDialog.xml";

    /**
     * Command-Name fuer den Close-Button
     */
    private static final String ACTION_CLOSE = "close";

    private Throwable throwable = null;
    private List<Throwable> throwables = null;

    private AKJTextArea taMessage = null;
    private AKJTextArea taDetails = null;

    /**
     * Konstruktor fuer den Error-Dialog.
     *
     * @param throwable Exception, die im Dialog angezeigt werden soll.
     */
    public AKJErrorDialog(Throwable throwable) {
        super(RESOURCE);
        this.throwable = throwable;
        createGUI();
        showMessage();
    }

    /**
     * Konstruktor fuer den Error-Dialog.
     *
     * @param throwables Exception, die im Dialog angezeigt werden soll.
     */
    public AKJErrorDialog(List<Throwable> throwables) {
        super(RESOURCE);
        this.throwables = throwables;
        createGUI();
        showMessage();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setPreferredSize(new Dimension(400, 300));
        setTitle(getSwingFactory().getText("title"));
        setIconURL(getSwingFactory().getText("icon"));
        AKJButton btnClose = getSwingFactory().createButton(ACTION_CLOSE, getActionListener());

        taMessage = getSwingFactory().createTextArea("message");
        taMessage.setEditable(false);
        taMessage.setWrapStyleWord(true);
        taMessage.setLineWrap(true);
        taMessage.setFontStyle(Font.BOLD);

        taDetails = getSwingFactory().createTextArea("details");
        taDetails.setEditable(false);
        taDetails.setWrapStyleWord(true);
        taDetails.setLineWrap(true);

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("error.text"), new JScrollPane(taMessage));
        tabbedPane.addTab(getSwingFactory().getText("error.details"), new JScrollPane(taDetails));

        AKJPanel btnPanel = new AKJPanel();
        btnPanel.setLayout(new GridBagLayout());
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnClose, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tabbedPane, BorderLayout.CENTER);
        getChildPanel().add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Zeigt die Message an.
     */
    private void showMessage() {
        ValidationException valEx = null;
        if (throwable != null) {
            valEx = (throwable instanceof ValidationException)
                    ? (ValidationException) throwable :
                    ((throwable.getCause()) instanceof ValidationException) ? (ValidationException) throwable.getCause() : null;
        }

        if (valEx != null) {
            showValidationErrorMessage(valEx.getAllDefaultMessages());
        }
        else if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) throwable;
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation constraintViolation :cve.getConstraintViolations()) {
                errorMessages.append(SystemUtils.LINE_SEPARATOR);
                errorMessages.append(constraintViolation.getMessage());
            }

            showValidationErrorMessage(errorMessages.toString());
        }
        else if ((throwable instanceof OptimisticLockingFailureException) || (throwable instanceof OptimisticLockException) ||
                (throwable instanceof HibernateOptimisticLockingFailureException)) {
            taMessage.setText("Der Datensatz wurde zwischenzeitlich geändert. Bitte aktualisieren und noch einmal versuchen.");
            taDetails.setText(ExceptionUtils.getFullStackTrace(throwable));
        }
        else if ((throwables != null) && (!throwables.isEmpty())) {
            StringBuilder errorMessages = new StringBuilder();
            StringBuilder errorDetails = new StringBuilder();

            for (Throwable t : throwables) {
                errorMessages.append(t.getLocalizedMessage());
                errorMessages.append(SystemUtils.LINE_SEPARATOR);

                errorDetails.append(ExceptionUtils.getFullStackTrace(t));
                errorDetails.append(SystemUtils.LINE_SEPARATOR);
            }

            taMessage.setText(errorMessages.toString());
            taDetails.setText(errorDetails.toString());
        }
        else {
            String msg = (throwable.getLocalizedMessage() != null)
                    ? throwable.getLocalizedMessage() : ExceptionUtils.getFullStackTrace(throwable);
            if (msg == null) {
                msg = "Unknown Error - NULL!";
            }
            StringBuilder errorMessage = new StringBuilder(msg);

            String errorMsgSub = getSubErrorMessages(throwable);
            if (StringUtils.isNotBlank(errorMsgSub)) {
                errorMessage.append(SystemUtils.LINE_SEPARATOR);
                errorMessage.append(SystemUtils.LINE_SEPARATOR);
                errorMessage.append(errorMsgSub);
            }

            taMessage.setText(errorMessage.toString());
            taDetails.setText(ExceptionUtils.getFullStackTrace(throwable));
        }
    }

    private void showValidationErrorMessage(String errorMessage) {
        StringBuilder msg = new StringBuilder();
        msg.append(getSwingFactory().getText("validation.message"));
        msg.append(SystemUtils.LINE_SEPARATOR);
        msg.append(errorMessage);
        taMessage.setText(msg.toString());
        taDetails.setText(ExceptionUtils.getFullStackTrace(throwable));
    }

    /*
     * Ermittelt aus dem Throwable-Objekt 't' weitere Sub-Throwables
     * (beginnend mit dem 2. Sub-Throwable).
     * Die Messages der Sub-Throws werden zu einem String zusammengefasst
     * und zuruck gegeben.
     * @param t Throwable-Objekt, aus dem weitere Sub-Throws ermittelt werden sollen.
     * @return String-Objekt mit den Messages der Sub-Throws
     */
    @SuppressWarnings("unchecked")
    private String getSubErrorMessages(Throwable t) {
        List<Throwable> subThrows = ExceptionUtils.getThrowableList(t);
        if ((subThrows != null) && (subThrows.size() > 1)) {
            StringBuilder errorMessagesSub = new StringBuilder();
            errorMessagesSub.append("sub.messages");
            errorMessagesSub.append(SystemUtils.LINE_SEPARATOR);

            for (int i = 1; i < subThrows.size(); i++) {
                Throwable subThrow = subThrows.get(i);
                errorMessagesSub.append("  - ").append(subThrow.getLocalizedMessage());
                errorMessagesSub.append(SystemUtils.LINE_SEPARATOR);
            }

            return errorMessagesSub.toString();
        }

        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (ACTION_CLOSE.equals(command)) {
            setValue(AKJOptionDialog.OK_OPTION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
