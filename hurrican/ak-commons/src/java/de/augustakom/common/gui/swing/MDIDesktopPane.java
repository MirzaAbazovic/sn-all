/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import org.apache.log4j.Logger;


/**
 * Ableitung von JDesktopPane um ein MultiDocument-Interface darstellen zu koennen. <br>
 *
 *
 */
public class MDIDesktopPane extends JDesktopPane {

    private static final Logger LOGGER = Logger.getLogger(MDIDesktopPane.class);

    private static final int FRAME_OFFSET = 0;
    private MDIDesktopManager manager;

    /**
     * Erzeugt eine neue Instanz von MDIDesktopPane.
     */
    public MDIDesktopPane() {
        init();
    }

    /* Initialisiert die DesktopPane */
    private void init() {
        manager = new MDIDesktopManager(this);
        setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    /**
     * Gibt die Anzahl der Frames zurueck, die von der DesktopPane z.Z. verwaltet/dargestellt werden.
     *
     * @return Anzahl Frames
     */
    public int getFrameCount() {
        return getAllFrames().length;
    }

    /**
     * @see javax.swing.JDesktopPane#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        checkDesktopSize();
    }

    /**
     * Fuegt der DesktopPane ein neues InternalFrame hinzu. <br>
     *
     * @param frame
     * @return
     * @see javax.swing.JDesktopPane#add(JInternalFrame)
     */
    public Component add(AKJInternalFrame frame) {
        Component retval = super.add(frame);
        checkDesktopSize();

        Point p = new Point(0, 0);
        frame.setLocation(p.x, p.y);

        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            frame.toBack();
        }
        return retval;
    }

    /**
     * @see java.awt.Container#remove(java.awt.Component)
     */
    @Override
    public void remove(Component c) {
        try {
            super.remove(c);
            checkDesktopSize();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Ordnet die sichtbaren Frames kaskadiert (versetzt) in der DesktopPane an.
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;
        JInternalFrame allFrames[] = getAllFrames();

        manager.setNormalSize();
        for (int i = allFrames.length - 1; i >= 0; i--) {
            allFrames[i].setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }

    /**
     * Schliesst alle InternalFrames.
     */
    public void closeFrames() {
        JInternalFrame allFrames[] = getAllFrames();
        for (int i = 0; i < allFrames.length; i++) {
            try {
                JInternalFrame frame = allFrames[i];
                frame.setClosed(true);
            }
            catch (PropertyVetoException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * Stellt alle Frames als Icon dar.
     */
    public void iconifyFrames() {
        JInternalFrame allFrames[] = getAllFrames();
        for (int i = 0; i < allFrames.length; i++) {
            try {
                allFrames[i].setIcon(true);
            }
            catch (PropertyVetoException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * Alle InternalFrames werden auf ihre urspruengliche Groesse gesetzt
     */
    public void deIconifyFrames() {
        JInternalFrame allFrames[] = getAllFrames();
        for (int i = 0; i < allFrames.length; i++) {
            try {
                allFrames[i].setIcon(false);
                allFrames[i].setMaximum(false);
            }
            catch (PropertyVetoException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        java.awt.Component allFrames[] = getAllFrames();
        manager.setNormalSize();
        int frameHeight = getBounds().height / allFrames.length;
        int y = 0;
        for (int i = 0; i < allFrames.length; i++) {
            allFrames[i].setSize(getBounds().width, frameHeight);
            allFrames[i].setLocation(0, y);
            y = y + frameHeight;
        }
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given dimension.
     */
    public void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given width and height.
     */
    public void setAllSize(int width, int height) {
        setAllSize(new Dimension(width, height));
    }

    /* Ueberprueft die Desktop-Groesse */
    private void checkDesktopSize() {
        if ((getParent() != null) && isVisible()) {
            manager.resizeDesktop();
        }
    }

    /**
     * Eigene Implementierung eines DesktopManagers, um Scrollbars darstellen zu koennen.
     */
    static class MDIDesktopManager extends DefaultDesktopManager {
        private MDIDesktopPane desktop;

        /**
         * Erzeugt eine neue Instanz von MDIDesktopManager
         *
         * @param desktop
         */
        public MDIDesktopManager(MDIDesktopPane desktop) {
            this.desktop = desktop;
        }

        /**
         * @see javax.swing.DefaultDesktopManager#endResizingFrame(JComponent)
         */
        @Override
        public void endResizingFrame(JComponent f) {
            super.endResizingFrame(f);
            resizeDesktop();
        }

        /**
         * @see javax.swing.DefaultDesktopManager#endDraggingFrame(JComponent)
         */
        @Override
        public void endDraggingFrame(JComponent f) {
            super.endDraggingFrame(f);
            resizeDesktop();
        }

        /**
         * Setzt die ScrollPane auf eine 'normale' Groesse
         */
        public void setNormalSize() {
            JScrollPane scrollPane = getScrollPane();
            int x = 0;
            int y = 0;
            Insets scrollInsets = getScrollPaneInsets();

            if (scrollPane != null) {
                Dimension d = scrollPane.getVisibleRect().getSize();
                if (scrollPane.getBorder() != null) {
                    d.setSize(
                            d.getWidth() - scrollInsets.left - scrollInsets.right,
                            d.getHeight() - scrollInsets.top - scrollInsets.bottom);
                }

                d.setSize(d.getWidth() - 20, d.getHeight() - 20);
                desktop.setAllSize(x, y);
                scrollPane.invalidate();
                scrollPane.validate();
            }
        }

        /**
         * Liest die Insets der ScrollPane aus und gibt diese zurueck.
         *
         * @return Insets der ScrollPane
         */
        private Insets getScrollPaneInsets() {
            JScrollPane scrollPane = getScrollPane();
            if (scrollPane == null) {
                return new Insets(0, 0, 0, 0);
            }

            return getScrollPane().getBorder().getBorderInsets(scrollPane);
        }

        /**
         * Gibt die ScrollPane zurueck.
         *
         * @return ScrollPane
         */
        private JScrollPane getScrollPane() {
            if (desktop.getParent() instanceof JViewport) {
                JViewport viewPort = (JViewport) desktop.getParent();
                if (viewPort.getParent() instanceof JScrollPane) {
                    return (JScrollPane) viewPort.getParent();
                }
            }

            return null;
        }

        /**
         * Passt die Groesse des Desktops an.
         */
        protected void resizeDesktop() {
            int x = 0;
            int y = 0;
            JScrollPane scrollPane = getScrollPane();
            Insets scrollInsets = getScrollPaneInsets();

            if (scrollPane != null) {
                JInternalFrame allFrames[] = desktop.getAllFrames();
                for (int i = 0; i < allFrames.length; i++) {
                    if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
                        x = allFrames[i].getX() + allFrames[i].getWidth();
                    }
                    if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
                        y = allFrames[i].getY() + allFrames[i].getHeight();
                    }
                }
                Dimension d = scrollPane.getVisibleRect().getSize();
                if (scrollPane.getBorder() != null) {
                    d.setSize(
                            d.getWidth() - scrollInsets.left - scrollInsets.right,
                            d.getHeight() - scrollInsets.top - scrollInsets.bottom);
                }

                if (x <= d.getWidth()) {
                    x = ((int) d.getWidth()) - 20;
                }

                if (y <= d.getHeight()) {
                    y = ((int) d.getHeight()) - 20;
                }

                desktop.setAllSize(x, y);
                scrollPane.invalidate();
                scrollPane.validate();
            }
        }
    }

}
