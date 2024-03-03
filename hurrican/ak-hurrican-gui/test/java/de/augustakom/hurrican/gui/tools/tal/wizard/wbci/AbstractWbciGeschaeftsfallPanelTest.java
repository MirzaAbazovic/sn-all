package de.augustakom.hurrican.gui.tools.tal.wizard.wbci;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.service.WbciCommonService;

public class AbstractWbciGeschaeftsfallPanelTest {

    @Mock
    WbciGeschaeftsfallKueMrnPanel wbciGeschaeftsfallKueMrnPanel;

    @Mock
    WbciCommonService wbciCommonService;

    @Mock
    FeatureService featureService;

    @Mock
    SwingFactory swingFactory;

    private AKJPanel childPanel;
    private AKJLabel label;
    private AKJButton button;
    private AKReferenceField referenceField;
    private AKJCheckBox checkBox;
    private AKJComboBox comboBox;
    private AKJTextField textField;
    private AKJFormattedTextField formattedTextField;
    private AKJRadioButton radioButton;
    private AKJDateComponent dateComponent;

    @BeforeMethod
    public void setup() throws InvocationTargetException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        doCallRealMethod().when(wbciGeschaeftsfallKueMrnPanel).createGUI();
        doCallRealMethod().when(wbciGeschaeftsfallKueMrnPanel).initChkAutomatable(anyLong());
        ReflectionTools.setFieldValue(WbciGeschaeftsfallKueMrnPanel.class, "featureService", wbciGeschaeftsfallKueMrnPanel, featureService);
        ReflectionTools.setFieldValue(WbciGeschaeftsfallKueMrnPanel.class, "wbciCommonService", wbciGeschaeftsfallKueMrnPanel, wbciCommonService);
        ReflectionTools.setFieldValue(WbciGeschaeftsfallKueMrnPanel.class, "wbciGeschaeftsfallTyp", wbciGeschaeftsfallKueMrnPanel, GeschaeftsfallTyp.VA_KUE_MRN);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                childPanel = new AKJPanel();
                label = new AKJLabel();
                button = new AKJButton();
                referenceField = new AKReferenceField();
                checkBox = new AKJCheckBox();
                comboBox = new AKJComboBox();
                textField = new AKJTextField();
                formattedTextField = new AKJFormattedTextField();
                radioButton = new AKJRadioButton();
                dateComponent = new AKJDateComponent();
            }
        });

        when(wbciGeschaeftsfallKueMrnPanel.getSwingFactory()).thenReturn(swingFactory);
        when(wbciGeschaeftsfallKueMrnPanel.getChildPanel()).thenReturn(childPanel);

        when(swingFactory.createLabel(anyString(), any(Integer.class), any(Integer.class))).thenReturn(label);
        when(swingFactory.createLabel(anyString())).thenReturn(label);
        when(swingFactory.createButton(anyString(), any(ActionListener.class))).thenReturn(button);
        when(swingFactory.createReferenceField(anyString())).thenReturn(referenceField);
        when(swingFactory.createCheckBox(anyString())).thenReturn(checkBox);
        when(swingFactory.createCheckBox(anyString(), any(ActionListener.class), anyBoolean())).thenReturn(checkBox);
        when(swingFactory.createComboBox(anyString())).thenReturn(comboBox);
        when(swingFactory.createComboBox(anyString(), any(ListCellRenderer.class))).thenReturn(comboBox);
        when(swingFactory.createTextField(anyString(), anyBoolean(), anyBoolean())).thenReturn(textField);
        when(swingFactory.createFormattedTextField(anyString(), anyBoolean())).thenReturn(formattedTextField);
        when(swingFactory.createRadioButton(anyString(), any(ActionListener.class), anyBoolean(), any(ButtonGroup.class))).thenReturn(radioButton);
        when(swingFactory.createDateComponent(anyString())).thenReturn(dateComponent);
    }

    @DataProvider
    public Object[][] initChkAutomatableDataProvider() {
        // @formatter:off
        return new Object[][] {
                {false, null, false, false},
                {true, null, false, true},
                {true, KundenTyp.GK, false, true},
                {true, KundenTyp.PK, true, true},
        };
        // @formatter:on
    }

    @Test(dataProvider = "initChkAutomatableDataProvider")
    public void initChkAutomatable(boolean featureOnline, KundenTyp kundenTyp, boolean expectedSelected, boolean expectedEnabled) throws FindException, InvocationTargetException, InterruptedException {
        when(featureService.isFeatureOnline(Feature.FeatureName.WBCI_WITA_AUTO_PROCESSING)).thenReturn(featureOnline);
        when(wbciCommonService.getKundenTyp(1L)).thenReturn(kundenTyp);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                wbciGeschaeftsfallKueMrnPanel.createGUI();
            }
        });
        wbciGeschaeftsfallKueMrnPanel.initChkAutomatable(1L);
        assertEquals(checkBox.isSelected(), expectedSelected);
        assertEquals(checkBox.isEnabled(), expectedEnabled);
        verify(wbciCommonService, times(featureOnline ? 1 : 0)).getKundenTyp(1L);
    }

    @Test(expectedExceptions = WbciBaseException.class)
    public void initChkAutomatableFindExceptionThrown() throws FindException, InvocationTargetException, InterruptedException {
        when(featureService.isFeatureOnline(Feature.FeatureName.WBCI_WITA_AUTO_PROCESSING)).thenReturn(Boolean.TRUE);
        when(wbciCommonService.getKundenTyp(1L)).thenThrow(new FindException("some error message"));
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                wbciGeschaeftsfallKueMrnPanel.createGUI();
            }
        });
        wbciGeschaeftsfallKueMrnPanel.initChkAutomatable(1L);
    }

}
