/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2005 17:08:48
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.lang.NumberTools;


/**
 * Panel zur graphischen Darstellung der Belegung einer UEVT-Leiste.
 *
 *
 */
public class UEVTLeistenPanel extends AKJAbstractPanel {

    private String leiste = null;

    private AKJTextField tfCuDAs = null;
    private AKJTextField tfPhysik = null;
    private AKJTextField tfBelegt = null;

    private Integer rangiert = null;
    private Integer belegt = null;

    /**
     * Konstruktor mit Angabe der Bezeichnung der Leiste, deren Belegung dargestellt werden soll.
     *
     * @param leiste
     */
    public UEVTLeistenPanel(String leiste) {
        super("de/augustakom/hurrican/gui/hvt/resources/UEVTLeistenPanel.xml");
        this.leiste = leiste;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJTextField tfLeiste = getSwingFactory().createTextField("leiste", false);
        tfLeiste.setAlignmentX(Component.CENTER_ALIGNMENT);
        tfLeiste.setText(leiste);

        tfCuDAs = getSwingFactory().createTextField("anzahl.cuda", false);
        tfPhysik = getSwingFactory().createTextField("physik", false);
        tfBelegt = getSwingFactory().createTextField("belegt", false);

        AKJPanel fillBottom = new AKJPanel();
        fillBottom.setPreferredSize(new Dimension(5, 3));

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfLeiste, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfCuDAs, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfPhysik, GBCFactory.createGBC(0, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfBelegt, GBCFactory.createGBC(0, 0, 0, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(fillBottom, GBCFactory.createGBC(0, 0, 0, 4, 3, 1, GridBagConstraints.HORIZONTAL));

        this.setBorder(BorderFactory.createRaisedBevelBorder());
        validateBackground();
    }

    /**
     * Gibt die Bezeichnung der Leiste zurueck, deren Belegung von dem Panel dargestellt wird.
     *
     * @return
     */
    protected String getLeiste() {
        return leiste;
    }

    /**
     * Uebergibt dem Panel die Anzahl der vorhandenen Stifte auf der Leiste. <br> Falls in der Map mehrere Stift-Typen
     * vorhanden sind, werden beide im entsprechenden TextField dargestellt. Die Anzahl der rangierten Stifte wird
     * jedoch zusammengefasst.
     *
     * @param counts Map: Key=Stift-Typ ('N' od. 'H'); Value=Anzahl der Stifte zum Typ
     *
     */
    protected void setCuDAs(Map<String, Integer> eqCounts) {
        Collection<String> keys = MapTools.getKeys(eqCounts);
        if (CollectionTools.isNotEmpty(keys)) {
            int count = 0;
            for (String key : keys) {
                if (key != null) {
                    if (count > 0) {
                        tfPhysik.setText(tfPhysik.getText() + " & ");
                    }
                    tfPhysik.setText(tfPhysik.getText() + key);
                    count++;
                }
                rangiert = NumberTools.add(rangiert, eqCounts.get(key));
            }
        }

        if ((rangiert != null) && (rangiert.intValue() > 0)) {
            tfCuDAs.setText(rangiert);
        }
        else {
            tfCuDAs.setText("");
        }
    }

    /**
     * Gibt die Art der CuDA-Physik (N oder H) zurueck.
     */
    protected String getCuDAPhysik() {
        return tfPhysik.getText();
    }

    /**
     * Uebergibt dem Panel die Anzahl der bereits belegten Stifte auf der Leiste.
     *
     * @param count
     */
    protected void setBelegt(Integer count) {
        this.belegt = count;
        if ((count != null) && (count.intValue() > 0)) {
            tfBelegt.setText(String.format("%s", count));
        }
        else {
            tfBelegt.setText("");
        }
    }

    /*
     * Gibt die Hintergrundfarbe fuer die Panels zurueck (abhaengig davon,
     * ob auf der Leiste Stifte definiert sind oder nicht).
     */
    private Color getPanelBackground() {
        Color background = null;
        if (StringUtils.isNotBlank(tfCuDAs.getText()) || StringUtils.isNotBlank(tfBelegt.getText())) {
            background = new Color(0, 0, 150); // blau
        }

        if (background == null) {
            background = new Color(64, 128, 128); // gruen
        }

        return background;
    }

    /**
     * Aendert die Hintergrundfarbe der Panels wenn noetig.
     */
    protected void validateBackground() {
        Color bg = getPanelBackground();
        if (!this.getBackground().equals(bg)) {
            changeBGColor(this, bg);
        }
    }

    /* Aendert die Hintergrundfarbe aller Panels, die auf diesem Panel dargestellt werden */
    private void changeBGColor(Container c, Color bgColor) {
        c.setBackground(bgColor);
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component comp = c.getComponent(i);
            if (comp instanceof JPanel) {
                changeBGColor((JPanel) comp, bgColor);
            }
        }
    }

    /**
     * Aendert die Hintergrundfarbe des TextFields <code>tfBelegt</code> auf rot, wenn belegt>=rangiert*0.9
     */
    protected void validateTFBackground() {
        Color bgColor = null;
        if ((belegt != null) && (belegt.intValue() > 0) && (rangiert != null) && (rangiert.intValue() > 0)) {
            bgColor = (belegt.intValue() >= (rangiert.intValue() * 0.9)) ? new Color(200, 0, 0) : null;
        }

        tfBelegt.setBackground((bgColor != null) ? bgColor : tfPhysik.getBackground());
        tfBelegt.setForeground((bgColor != null) ? Color.white : tfPhysik.getForeground());
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
    public void update(Observable o, Object arg) {
    }

}


