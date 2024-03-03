/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;


/**
 * Implementierung einer StatusBar.
 *
 *
 */
public class AKJStatusBar extends AKJPanel {

    private static final Logger LOGGER = Logger.getLogger(AKJStatusBar.class);

    /**
     * Konstante gibt an, dass eine ProgressBar gestartet werden soll.
     */
    public static final int START_PROGRESS = 0;

    /**
     * Konstante gibt an, dass eine ProgressBar gestoppt und dadurch ausgeblendet werden soll.
     */
    public static final int STOP_PROGRESS = 1;

    private int areaCount = 0;
    private int[] fill = null;
    private List<AKJPanel> areas = null;

    /**
     * Erstellt eine StatusBar mit der angegebenen Anzahl an Areas.
     *
     * @param areaCount
     */
    public AKJStatusBar(int areaCount) {
        super();
        init(areaCount, null);
    }

    /**
     * Erstellt eine StatusBar mit der angegebenen Anzahl an Areas. <br> In dem Array kann definiert werden, wie sich
     * die einzelnen Areas ausdehnen sollen.
     *
     * @param areaCount
     * @param fill
     */
    public AKJStatusBar(int areaCount, int[] fill) {
        super();
        init(areaCount, fill);
    }

    /**
     * Initialisiert die StatusBar.
     */
    protected void init(int areaCount, int[] fill) {
        this.areaCount = (areaCount > 0) ? areaCount : 1;
        this.fill = fill;

        this.areas = new ArrayList<AKJPanel>(this.areaCount);
        createGUI();
    }

    /**
     * Erzeugt die GUI fuer die StatusBar.
     */
    protected void createGUI() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createLoweredBevelBorder());

        int x = 0;
        for (int i = 0; i < areaCount; i++) {
            AKJPanel panel = new AKJPanel(new BorderLayout());
            panel.add(new AKJLabel(" "), BorderLayout.CENTER);
            areas.add(panel);

            int weightx = ((fill != null) && (fill.length >= i)) ? fill[i] : 100;
            this.add(panel, GBCFactory.createGBC(weightx, 0, x, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            x++;
            if ((areaCount - 1) > i) {
                JSeparator separator = new JSeparator(JSeparator.VERTICAL);
                this.add(separator, GBCFactory.createGBC(0, 100, x, 0, 1, 1, GridBagConstraints.VERTICAL));
                x++;
            }
        }
    }

    /**
     * Ueberprueft, ob an der angegebenen Position ein Panel liegt. Ist dies der Fall, wird es zurueck gegeben.
     *
     * @param area Area, an der ein Panel 'vermutet' wird
     * @return Panel, das in der angegebenen Area liegt oder <code>null</code>
     */
    private AKJPanel getPanelAtArea(int area) {
        if ((area >= 0) && (area <= areas.size())) {
            return areas.get(area);
        }

        return null;
    }

    /**
     * Gibt die Anzahl der StatusBar-Bereiche zurueck.
     *
     * @return Anzahl der dargestellten StatusBar-Bereiche
     */
    public int getAreaCount() {
        return areaCount;
    }

    /**
     * Zeigt fuer eine best. Zeit einen Text (und evtl. ein Icon) an.
     *
     * @param area         Area, die den Text erhalten soll
     * @param text         Text, der angezeigt werden soll
     * @param icon         das darzustellende Icon
     * @param milliseconds Anzahl Millisekunden, nach denen der Text wieder 'geloescht' werden soll.
     */
    public void showTimedText(final int area, String text, String icon, int milliseconds) {
        setText(area, text, icon);
        Timer timer = new Timer(milliseconds, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setText(area, " ");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * In der Area <code>area</code> wird der String <code>text</code> dargestellt. <br> <strong>WICHTIG:</strong> Der
     * Area-Count beginnt bei 0 ! <br> Ist der int-Wert <code>area</code> ungueltig, dann wird der Text ignoriert.
     *
     * @param area Area, die den Text erhalten soll
     * @param text Text, der in der Area angezeigt werden soll
     * @see de.augustakom.common.gui.swing.AKJStatusBar#setText(int, String, String)
     */
    public void setText(int area, String text) {
        setText(area, text, null);
    }

    /**
     * In der Area <code>area</code> wird der String <code>text</code> und das Icon <code>icon</code> dargestellt. <br>
     * <strong>WICHTIG:</strong> Der Area-Count beginnt bei 0 ! <br> Ist der int-Wert <code>area</code> ungueltig, dann
     * wird der Text und das Icon ignoriert.
     *
     * @param area Area, die den Text erhalten soll
     * @param text Text, der in der Area angezeigt werden soll
     * @param icon darzustellendes Icon (z.B. de/augustakom/images/MyImage.gif)
     */
    public void setText(int area, String text, String icon) {
        AKJPanel panel = getPanelAtArea(area);
        if (panel != null) {
            AKJLabel label = new AKJLabel(text, AKJLabel.LEFT);

            if (icon != null) {
                try {
                    URL iconURL = getClass().getClassLoader().getResource(icon);
                    ImageIcon imageIcon = new ImageIcon(iconURL);
                    label.setIcon(imageIcon);
                }
                catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }

            panel.removeAll();
            panel.add(label, BorderLayout.CENTER);
            panel.validate();
        }
    }

    /**
     * Startet bzw. stoppt eine ProgressBar in der angegebenen Area. <br> <strong>WICHTIG:</strong> Der Area-Count
     * beginnt bei 0 !
     *
     * @param area          Area, in der die ProgressBar dargestellt bzw gestoppt werden soll.
     * @param progressState Status der ProgressBar <br> starten: <code>START_PROGRESS</code> <br> stoppen:
     *                      <code>STOP_PROGRESS</code>
     * @param text          darzustellender Text
     * @param paintBorder   Angabe, ob die ProgressBar einen Border besitzen soll (true) oder nicht (false).
     */
    public void showProgress(int area, int progressState, String text, boolean paintBorder) {
        AKJPanel panel = getPanelAtArea(area);
        if (panel != null) {
            panel.setLayout(new BorderLayout());

            try {
                switch (progressState) {
                    case START_PROGRESS:
                        JProgressBar bar = new JProgressBar();
                        bar.setIndeterminate(true);
                        bar.setBorderPainted(paintBorder);
                        bar.setPreferredSize(panel.getSize());

                        if (text != null) {
                            bar.setString(text);
                            bar.setStringPainted(true);
                        }

                        panel.removeAll();
                        panel.add(bar, BorderLayout.CENTER);
                        break;

                    case STOP_PROGRESS:
                        text = ((text == null) || (text.length() == 0)) ? " " : text;

                        panel.removeAll();
                        panel.add(new AKJLabel(text, AKJLabel.LEFT), BorderLayout.CENTER);
                        break;

                    default:
                        break;
                }
            }
            finally {
                panel.validate();
            }
        }
    }

    /**
     * Startet bzw. stoppt eine ProgressBar in der angegebenen Area.
     *
     * @see de.augustakom.common.gui.swing.AKJStatusBar#showProgress(int, int, String, boolean)
     */
    public void showProgress(int area, int progressState) {
        showProgress(area, progressState, null, true);
    }

    /**
     * Startet bzw. stoppt eine ProgressBar in der angegebenen Area.
     *
     * @see de.augustakom.common.gui.swing.AKJStatusBar#showProgress(int, int, String, boolean)
     */
    public void showProgress(int area, int progressState, String text) {
        showProgress(area, progressState, text, true);
    }

    /**
     * Startet bzw. stoppt eine ProgressBar in der angegebenen Area.
     *
     * @see de.augustakom.common.gui.swing.AKJStatusBar#showProgress(int, int, String, boolean)
     */
    public void showProgress(int area, int progressState, boolean border) {
        showProgress(area, progressState, null, border);
    }


}


