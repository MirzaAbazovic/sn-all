/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.2010 10:36:44
 */
package de.augustakom.hurrican.gui.auftrag.search;

import static de.augustakom.hurrican.gui.auftrag.search.AuftragKundeSearchPanel.*;
import static de.augustakom.hurrican.gui.common.ButtonEnabledCondition.*;
import static org.fest.swing.edt.GuiActionRunner.*;

import javax.swing.text.*;
import org.apache.log4j.Logger;
import org.fest.swing.annotation.GUITest;
import org.fest.swing.data.TableCell;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.testng.annotations.Test;

import de.augustakom.hurrican.gui.AbstractHurricanGuiTest;
import de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel;

/**
 * Testet den Search-Dialog
 *
 *
 */
public class AuftragKundeSearchFrameTest extends AbstractHurricanGuiTest {

    private static final Logger LOGGER = Logger.getLogger(AuftragKundeSearchFrameTest.class);

    /**
     * Wechselt schnell zwischen Tabs im Auftragfenster, um zu sehen, ob es dabei Probleme gibt.
     */
    @Test(groups = { "gui" })
    @GUITest
    public void testTabSwitch() throws InterruptedException {
        FrameFixture mainFrame = startHurricanGui();
        mainFrame.menuItem("search.auftrag.kunde").click();
        mainFrame.radioButton(RB_ENDSTELLE).click();
        final JTextComponent vbzField = mainFrame.textBox(AuftragEndstelleFilterPanel.TF_VBZ).component();
        execute(new GuiTask() {
            @Override
            public void executeInEDT() {
                vbzField.setText("WA*");
            }
        });
        JButtonFixture searchButtonFixture = mainFrame.button(CMD_SEARCH);
        searchButtonFixture.click();
        waitForButtonEnabled(searchButtonFixture.component());
        JTableFixture resultTable = mainFrame.table();
        String auftragId = resultTable.contents()[0][2];
        TableCell firstEntry = resultTable.cell(auftragId.toString());
        resultTable.cell(firstEntry).doubleClick();
        JTabbedPaneFixture auftragTabs = mainFrame.tabbedPane();
        auftragTabs.selectTab("Endstellen");
        auftragTabs.selectTab("Rufnummern");
        auftragTabs.selectTab("Stammdaten");
        auftragTabs.selectTab("Endstellen");
        auftragTabs.selectTab("Stammdaten");
        auftragTabs.selectTab("Internet");
        auftragTabs.selectTab("Stammdaten");
        mainFrame.textBox(AuftragStammdatenPanel.TF_AUFTRAG_ID).requireNotEditable().requireText(auftragId);
        mainFrame.component().dispose();
    }

    /**
     * Sucht nach einem Auftrag via der Id und macht den Auftrag dann auf
     */
    @Test(groups = { "gui" })
    @GUITest
    public void testSearchAuftragId() throws InterruptedException {
        FrameFixture mainFrame = startHurricanGui();
        mainFrame.menuItem("search.auftrag.kunde").click();
        mainFrame.radioButton(RB_AUFTRAG).click();
        mainFrame.textBox(AuftragDatenFilterPanel.TF_CC_AUFTRAG_ID).enterText("388544");
        JButtonFixture searchButtonFixture = mainFrame.button(CMD_SEARCH);
        searchButtonFixture.click();
        waitForButtonEnabled(searchButtonFixture.component());
        JTableFixture resultTable = mainFrame.table();
        resultTable.cell(resultTable.cell("388544")).doubleClick();
        JTabbedPaneFixture auftragTabs = mainFrame.tabbedPane();
        auftragTabs.selectTab("Endstellen");
        auftragTabs.selectTab("Rufnummern");
        auftragTabs.selectTab("Stammdaten");
        mainFrame.textBox(AuftragStammdatenPanel.TF_AUFTRAG_ID).requireNotEditable().requireText("388544");
        mainFrame.component().dispose();
    }
}
