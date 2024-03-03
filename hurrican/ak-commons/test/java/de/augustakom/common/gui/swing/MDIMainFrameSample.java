/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.BasicConfigurator;


/**
 * Beispiel-Implementierung eines MDIMainFrames.
 *
 *
 */
public class MDIMainFrameSample extends AbstractMDIMainFrame {

    private static SwingFactory sf = SwingFactory.getInstance("de/augustakom/common/gui/swing/MDIMainFrameSample.xml");

    //    private static AKJMenuBar menuBar = null;
    //    private static AKJToolBar toolBar = null;
    //    private static AKJStatusBar statusBar = null;

    public MDIMainFrameSample() {
        super();
        setIconImage(new IconHelper().getIcon("de/augustakom/common/gui/images/Test.gif").getImage());
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createGUI()
     */
    protected void createGUI() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createMenuBar()
     */
    protected void createMenuBar() {
        menuBar = sf.createMenuBar("mdi.sample", null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getAKJMenuBar()
     */
    protected AKJMenuBar getAKJMenuBar() {
        return menuBar;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getWindowMenu()
     */
    protected AKJMenu getWindowMenu() {
        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            Component c = menuBar.getComponent(i);
            if ("menu.window".equals(c.getName())) {
                return (AKJMenu) c;
            }
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getDefaultWindowMenuItemCount()
     */
    protected int getDefaultWindowMenuItemCount() {
        return 3;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createToolBar()
     */
    protected void createToolBar() {
        toolBar = new AKJToolBar();
        toolBar.setFloatable(false);

        toolBar.add(new SampleAction());
        toolBar.add(new SampleActionTwo(this));
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getToolBar()
     */
    protected AKJToolBar getToolBar() {
        return toolBar;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createStatusBar()
     */
    protected void createStatusBar() {
        statusBar = new AKJStatusBar(4);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getStatusBar()
     */
    public AKJStatusBar getStatusBar() {
        return statusBar;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        BasicConfigurator.configure();

        MDIMainFrameSample sample = new MDIMainFrameSample();
        sample.setSize(new Dimension(800, 600));
        sample.setTitle("MDI Sample");
        sample.setVisible(true);
    }

    // Sample-Action - oeffnet InternalFrames.
    class SampleAction extends AbstractAction {
        public SampleAction() {
            putValue(AbstractAction.SMALL_ICON, sf.getIcon("de/augustakom/common/gui/images/Test.gif"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            InternalFrameOne f = new InternalFrameOne("Frame 1", true, true, true, true);
            registerFrame(f, true);
        }
    }

    // Sample-Action - oeffnet InternalFrames.
    class SampleActionTwo extends AbstractAction {
        private Component owner = null;

        public SampleActionTwo(Component owner) {
            putValue(AbstractAction.SMALL_ICON, sf.getIcon("de/augustakom/common/gui/images/Test.gif"));
            this.owner = owner;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            InternalFrameTwo f = new InternalFrameTwo("Frame 2 eins-zwei-drei-vier-fuenf", true, true, true, true);
            f.setPreferredSize(new Dimension(200, 200));
            registerFrame(f, false);

            //            AKJDialog dlg = new AKJDialog(); //(Frame) owner);
            //            dlg.setTitle("Test");
            //            dlg.setSize(new Dimension(200,200));
            //            DialogHelper.showDialog(owner, dlg, false, true);

            AKJOptionDialog dlg = new AKJOptionDialog();
            dlg.setSize(new Dimension(200, 200));
            dlg.setTitle("My OptionDialog");
            dlg.setIconURL("de/augustakom/common/gui/images/Test.gif");
            DialogHelper.showDialog(owner, dlg, false, true);
        }
    }

    // InternalFrame
    class InternalFrameOne extends AKJInternalFrame {
        public InternalFrameOne(String title, boolean resizable,
                boolean closable, boolean maximizable, boolean iconifiable) {
            super(title, resizable, closable, maximizable, iconifiable);
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
         */
        public String getUniqueName() {
            return "Frame one";
        }
    }

    // weiteres InternalFrame
    class InternalFrameTwo extends AKJInternalFrame {
        public InternalFrameTwo(String title, boolean resizable,
                boolean closable, boolean maximizable, boolean iconifiable) {
            super(title, resizable, closable, maximizable, iconifiable);
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
         */
        public String getUniqueName() {
            return "Frame two";
        }
    }

}
