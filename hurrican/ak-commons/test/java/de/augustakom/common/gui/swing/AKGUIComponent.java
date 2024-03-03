/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;


/**
 * Test-Klasse fuer de.augustakom.common.gui.swing.AKJTextField
 *
 *
 */
public class AKGUIComponent extends JFrame implements ActionListener, FocusListener {

    private static final Logger LOGGER = Logger.getLogger(AKGUIComponent.class);

    private AKJStatusBar statusBar = null;

    public static void main(String[] args) {
        AKGUIComponent test = new AKGUIComponent();
        test.setSize(new Dimension(200, 340));
        test.setVisible(true);
    }

    public AKGUIComponent() {
        createGUI();
    }

    private void createGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Alt-F4 auf Frame verhindern!
        //        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        //            .addKeyEventDispatcher ( new KeyEventDispatcher()  {
        //               public boolean dispatchKeyEvent(KeyEvent e){
        //                 if (e.getKeyCode() == KeyEvent.VK_F4 && e.getModifiers()==KeyEvent.ALT_MASK) {
        //                     e.consume();
        //                     return false;
        //                 }
        //                 return true;
        //            }});

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel childPanel = new JPanel(new GridBagLayout());

        long begin = System.currentTimeMillis();
        SwingFactory sf = SwingFactory.getInstance("de/augustakom/common/gui/swing/SwingFactoryTest.xml");

        setTitle(sf.getText("test.text"));

        AKJMenuBar menubar = sf.createMenuBar("test.menubar", null);
        LOGGER.debug("Time for MenuBar: " + (System.currentTimeMillis() - begin));
        this.setJMenuBar(menubar);

        AKJToolBar toolbar = sf.createToolBar("test.toolbar");

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(toolbar, BorderLayout.NORTH);
        this.getContentPane().add(childPanel, BorderLayout.CENTER);

        Color yellow = new Color(255, 255, 200);
        Color blue = new Color(204, 236, 255);

        // TextFields
        JTextField tfYellow = new JTextField("yellow");
        //tfYellow.setActiveColor(yellow);
        tfYellow.setEnabled(false);
        tfYellow.setEditable(true);
        Action[] actions = tfYellow.getActions();
        for (int i = 0; i < actions.length; i++) {
            LOGGER.debug("....... action: " + actions[i]);
        }

        AKJTextField tfBlue = new AKJTextField();
        tfBlue.setInactiveColor(blue);
        tfBlue.setActiveColor(yellow);
        tfBlue.setMaxChars(10);
        tfBlue.setText("01234567890123");

        AKJButton btn = new AKJButton("Test");
        btn.addActionListener(this);

        // TextPanes
        AKJTextPane tpYellow = new AKJTextPane();
        tpYellow.setActiveColor(yellow);

        AKJTextPane tpBlue = new AKJTextPane();
        tpBlue.setInactiveColor(blue);
        tpBlue.setActiveColor(yellow);

        // TextAreas
        AKJTextArea taYellow = new AKJTextArea();
        taYellow.setActiveColor(yellow);
        taYellow.setMaxChars(10);
        taYellow.setText("123");

        AKJTextArea taBlue = new AKJTextArea();
        taBlue.setInactiveColor(blue);
        taBlue.setActiveColor(yellow);

        // FormattedTextFields
        AKJFormattedTextField tfFYellow = new AKJFormattedTextField();
        tfFYellow.setActiveColor(yellow);

        AKJFormattedTextField tfFBlue = new AKJFormattedTextField();
        tfFBlue.setInactiveColor(blue);
        tfFBlue.setActiveColor(yellow);

        AKJFormattedTextField tfFormatted = sf.createFormattedTextField("test.formatted.textfield");
        //new AKJFormattedTextField(new DecimalFormat("#########")); //"#.##"));

        // FormattedTextField for phone number
        MaskFormatter fmt = null;
        try {
            fmt = new MaskFormatter("###-###-####");
        }
        catch (java.text.ParseException e) {
        }
        AKJFormattedTextField tfMask = new AKJFormattedTextField(fmt);

        // FormattedTextField for date
        AKJFormattedTextField tfDate = new AKJFormattedTextField(new SimpleDateFormat("dd.MM.yyyy"));
        tfDate.setValue(new Date());

        // PasswordField
        AKJPasswordField pwField = new AKJPasswordField();
        pwField.setActiveColor(yellow);

        statusBar = new AKJStatusBar(5, new int[] { 0, 100, 100, 100, 100 });

        AKJDateComponent dateComp = new AKJDateComponent(new Date());

        AKJRadioButton rb = new AKJRadioButton();
        rb.setEnabled(false);
        rb.setSelected(true);
        //rb.setBackground(Color.blue);
        rb.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel lbl = new JLabel("test");
        lbl.setBackground(Color.green);
        lbl.setOpaque(true);

        childPanel.add(tfYellow, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfBlue, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(btn, GBCFactory.createGBC(100, 0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tpYellow, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tpBlue, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(new JPanel(), GBCFactory.createGBC(100, 0, 0, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(taYellow, GBCFactory.createGBC(100, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(taBlue, GBCFactory.createGBC(100, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfFYellow, GBCFactory.createGBC(100, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfFBlue, GBCFactory.createGBC(100, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfFormatted, GBCFactory.createGBC(100, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfMask, GBCFactory.createGBC(100, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(tfDate, GBCFactory.createGBC(100, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(pwField, GBCFactory.createGBC(100, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(lbl, GBCFactory.createGBC(100, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(dateComp, GBCFactory.createGBC(100, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(rb, GBCFactory.createGBC(100, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        childPanel.add(statusBar, GBCFactory.createGBC(100, 0, 0, 10, 2, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        //        if (e.getSource() == tfFormatted) {
        //            try {
        //                tfFormatted.commitEdit();
        //            }
        //            catch (ParseException e1) {
        //                e1.printStackTrace();
        //            }
        //
        //            LOGGER.debug("... formatter: "+tfFormatted.getFormatter());
        //            LOGGER.debug("-----------> value type: "+tfFormatted.getValue().getClass().getName());
        //        }
    }

    /**
     * @see javax.swing.JFrame#processWindowEvent(java.awt.event.WindowEvent)
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        LOGGER.debug("...... " + e.getSource());
        super.processWindowEvent(e);
    }
}



