/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */
package de.augustakom.common.gui.swing.wizard;

import java.util.*;

/**
 * Interface fuer einen Wizard.
 */

public interface AKJWizard {

    public List<?> getWizardPanelList();

    public void setWizardPanelList(List<?> panelList);

    public void addWizardPanel(AKJDefaultWizardPanel panel);

    public void addWizardPanel(int index, AKJDefaultWizardPanel panel);

    public AKJDefaultWizardPanel removeWizardPanel(AKJDefaultWizardPanel panel);

    public AKJDefaultWizardPanel removeWizardPanel(int index);

    public AKJDefaultWizardPanel getWizardPanel(int index);

}
