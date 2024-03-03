/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2005 09:37:04
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import static de.augustakom.hurrican.HurricanConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.common.tools.ReflectionTools;

@Test(groups = BaseTest.UNIT)
public class WitaSelectCBVorgangWizardPanelTest extends BaseTest {

    @Mock
    WitaSelectCBVorgangWizardPanel witaSelectCBVorgangWizardPanel;

    @Mock
    SwingFactory swingFactory;
    @Mock
    FeatureService featureService;

    AKJPanel childPanel;
    AKJLabel label;
    AKJButton button;
    AKReferenceField referenceField;
    AKJCheckBox checkBox;

    @BeforeMethod
    public void setup() throws InvocationTargetException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        doCallRealMethod().when(witaSelectCBVorgangWizardPanel).createGUI();
        ReflectionTools.setFieldValue(WitaSelectCBVorgangWizardPanel.class, "featureService", witaSelectCBVorgangWizardPanel, featureService);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                childPanel = new AKJPanel();
                label = new AKJLabel();
                button = new AKJButton();
                referenceField = new AKReferenceField();
                checkBox = new AKJCheckBox();
            }
        });

        Mockito.when(witaSelectCBVorgangWizardPanel.getSwingFactory()).thenReturn(swingFactory);
        Mockito.when(witaSelectCBVorgangWizardPanel.getChildPanel()).thenReturn(childPanel);
        Mockito.when(witaSelectCBVorgangWizardPanel.getAuftragId()).thenReturn(1L);

        Mockito.when(swingFactory.createLabel(anyString(), any(Integer.class), any(Integer.class))).thenReturn(label);
        Mockito.when(swingFactory.createLabel(anyString())).thenReturn(label);
        Mockito.when(swingFactory.createButton(anyString(), any(ActionListener.class))).thenReturn(button);
        Mockito.when(swingFactory.createReferenceField(anyString())).thenReturn(referenceField);
        Mockito.when(swingFactory.createCheckBox(anyString())).thenReturn(checkBox);
    }

    @Test
    public void testWitaTestMode() throws Exception {
        System.setProperty(WITA_SIMULATOR_TEST_MODE, "false");

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                witaSelectCBVorgangWizardPanel.createGUI();
            }
        });

        verify(swingFactory, times(0)).createTextField(WitaSelectCBVorgangWizardPanel.TEST_AM_USER);
    }

    @Test
    public void testWbciEnabled() throws Exception {
        Mockito.when(featureService.isFeatureOnline(eq(Feature.FeatureName.WBCI_ENABLED))).thenReturn(true);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                witaSelectCBVorgangWizardPanel.createGUI();
            }
        });

        verify(featureService, times(1)).isFeatureOnline(eq(Feature.FeatureName.WBCI_ENABLED));
        Assert.assertEquals(childPanel.getComponentCount(), 3);
    }

    @Test
    public void testWbciDisabled() throws Exception {
        Mockito.when(featureService.isFeatureOnline(eq(Feature.FeatureName.WBCI_ENABLED))).thenReturn(false);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                witaSelectCBVorgangWizardPanel.createGUI();
            }
        });

        verify(featureService, times(1)).isFeatureOnline(eq(Feature.FeatureName.WBCI_ENABLED));
        Assert.assertEquals(childPanel.getComponentCount(), 1);
    }

}
