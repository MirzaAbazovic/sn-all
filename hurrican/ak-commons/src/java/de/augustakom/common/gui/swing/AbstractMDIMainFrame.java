/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKMenuOwner;
import de.augustakom.common.gui.iface.AKMenuOwnerListener;

/**
 * Abstrakte Basisklasse fuer ein <i>MDIMainFrame</i>. <br> Die Klasse stellt Methoden zur Verfuegung, die das MainFrame
 * einer MDI-Applikation benoetigt.
 *
 *
 */
public abstract class AbstractMDIMainFrame extends AKJFrame implements AKMenuOwnerListener {

    private static final Logger LOGGER = Logger.getLogger(AbstractMDIMainFrame.class);

    /**
     * Maximale Laenge fuer den Frame-Titel, der im Window-Menu dargestellt wird
     */
    protected static final int MAX_FRAME_TITLE_LENGTH = 30;
    private static final long serialVersionUID = 8122225093720210893L;
    /**
     * Flag, ob Frame bereits initialisiert ist
     */
    protected boolean initialized = false;
    /**
     * Flag, ob die DesktopPane in einer ScrollBar dargestellt werden soll.
     */
    protected boolean useScrollBar = true;
    /**
     * Verwendete DesktopPane.
     */
    protected MDIDesktopPane desktopPane = null;
    /**
     * Verwendete StatusBar. Muss von Ableitung erzeugt werden.
     */
    protected AKJStatusBar statusBar = null;
    /**
     * Verwendete MenuBar. Muss von Ableitung erzeugt werden.
     */
    protected AKJMenuBar menuBar = null;
    /**
     * Verwendete ToolBar. Muss von Ableitung erzeugt werden.
     */
    protected AKJToolBar toolBar = null;
    /**
     * Map mit den angezeigten InternalFrames.
     */
    protected Map<String, AKJInternalFrame> internalFrames = null;

    private AKJMenu additionalMenu = null;

    /**
     * Erzeugt ein neues MainFrame.
     */
    public AbstractMDIMainFrame() {
        super();
        initFrame();
    }

    /**
     * Konstruktor mit Angabe ob ScrollBars in der DesktopPane verwendet werden sollen.
     *
     * @param useScrollBar true: ScrollBars sollen verwendet werden (= default)
     * @throws HeadlessException
     */
    public AbstractMDIMainFrame(boolean useScrollBar) {
        super();
        this.useScrollBar = useScrollBar;
        initFrame();
    }

    /**
     * Erzeugt ein neues MainFrame mit Title.
     *
     * @param title
     */
    public AbstractMDIMainFrame(String title) {
        super(title);
        initFrame();
    }

    /**
     * Konstruktor mit Angabe des Titles und ob ScrollBars in der DesktopPane verwendet werden sollen.
     *
     * @param title        Title fuer das Frame
     * @param useScrollBar true: ScrollBars sollen verwendet werden (= default)
     */
    public AbstractMDIMainFrame(String title, boolean useScrollBar) {
        super(title);
        this.useScrollBar = useScrollBar;
        initFrame();
    }

    /**
     * In dieser Methode kann die GUI aufgebaut werden. <br> Die Methode muss von den Ableitungen jedoch selbst
     * aufgerufen werden!
     */
    protected abstract void createGUI();

    /**
     * Diese Methode wird bei der Initialisierung des Frames aufgerufen. Die Ableitungen koennen in dieser Methode die
     * MenuBar aufbauen. <br> Die MenuBar wird automatisch dem Frame zugeordnet.
     */
    protected abstract void createMenuBar();

    /**
     * Die Ableitungen sollen in dieser Methode eine Instanz von AKJMenuBar zurueck geben. <br> Diese MenuBar wird dem
     * Frame automatisch zugeordnet.
     *
     * @return MenuBar, die dem Frame zugeordnet werden soll.
     */
    protected abstract AKJMenuBar getAKJMenuBar();

    /**
     * Die Ableitungen sollen in dieser Methode ein Menu zurueck geben, in das die geoeffneten InternalFrames
     * eingetragen werden sollen.
     *
     * @return Menu, dass die Eintraege fuer die dargestellten InternalFrames erhalten soll.
     */
    protected abstract AKJMenu getWindowMenu();

    /**
     * Gibt die Anzahl der MenuItems innerhalb des 'Window'-Menus zurueck. <br> Ein Default-Item ist z.B. 'Close all'
     * etc.
     *
     * @return Standard-Anzahl der Items im Window-Menu
     */
    protected abstract int getDefaultWindowMenuItemCount();

    /**
     * Diese Methode wird bei der Initialisierung des Frames aufgerufen. Die Ableitungen koennen in dieser Methode die
     * ToolBar aufbauen.
     */
    protected abstract void createToolBar();

    /**
     * Wenn das Frame eine ToolBar darstellen soll, dann muss die Ableitung in dieser Methode eine Instanz von
     * AKJToolBar zurueck geben.
     *
     * @return ToolBar, die im Frame dargestellt werden soll
     */
    protected abstract AKJToolBar getToolBar();

    /**
     * Diese Methode wird bei der Initialisierung des Frames aufgerufen. Die Ableitungen koennen in dieser Methode die
     * StatusBar aufbauen.
     */
    protected abstract void createStatusBar();

    /**
     * Wenn das Frame eine StatusBar darstellen soll, dann muss die Ableitung in dieser Methode eine Instanz von
     * AKJStatusBar zurueck geben.
     *
     * @return StatusBar, die im Frame dargestellt werden soll
     */
    public abstract AKJStatusBar getStatusBar();

    /**
     * Initialisiert das Frame. <br> Bei der Initialisierung wird die DesktopPane erzeugt und dem Frame zugeordnet.
     * Liefern die Implementierungen von <code>getToolBar</code> und <code>getStatusBar</code> entsprechende Instanzen
     * zurueck, werden die enstprechenden Objekte durch diese Methode bereits dem Frame zugeordnet.
     */
    protected void initFrame() {
        if (!initialized) {
            try {
                setDefaultLookAndFeelDecorated(true);
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                desktopPane = new MDIDesktopPane();
                desktopPane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);

                this.getContentPane().setLayout(new BorderLayout());
                if (useScrollBar) {
                    this.getContentPane().add(new JScrollPane(desktopPane), BorderLayout.CENTER);
                }
                else {
                    this.getContentPane().add(desktopPane, BorderLayout.CENTER);
                }

                createMenuBar();
                this.menuBar = getAKJMenuBar();
                if (menuBar != null) {
                    this.setJMenuBar(menuBar);
                }

                createToolBar();
                this.toolBar = getToolBar();
                if (toolBar != null) {
                    this.getContentPane().add(toolBar, BorderLayout.NORTH);
                }

                createStatusBar();
                this.statusBar = getStatusBar();
                if (statusBar != null) {
                    this.getContentPane().add(statusBar, BorderLayout.SOUTH);
                }
            }
            finally {
                initialized = true;
            }
        }
    }

    /**
     * Gibt die zu verwendende DesktopPane zurueck.
     */
    public MDIDesktopPane getDesktopPane() {
        return desktopPane;
    }

    /**
     * Fuegt dem MDI-Frame ein InternalFrame hinzu. <br> Sofern die Methode <code>getWindowMenu</code> eine Instanz von
     * AKJMenu zurueck liefert, wird das Frame in diesem Menu eingetragen. <br> <br>
     * <p/>
     * Jedes InternalFrame kann nur <strong>einmal</strong> in dem MainFrame dargestellt werden. Als Identifier fuer die
     * Frames wird die Methode <code>AKJInternalFrame#getUniqueName()</code> verwendet. <br> Wird der Methode ein Frame
     * uebergeben, das bereits im MainFrame dargestellt wird, wird versucht, dieses zu aktivieren.
     *
     * @param frameToRegister Frame, das hinzugefuegt werden soll
     * @param maximize        wenn <code>true</code>, dann wird das InternalFrame maximiert dargestellt.
     */
    public void registerFrame(AKJInternalFrame frameToRegister, boolean maximize) {
        if (frameToRegister != null) {
            if (internalFrames == null) {
                internalFrames = new HashMap<>();
            }

            if (!internalFrames.containsKey(frameToRegister.getUniqueName())) {
                internalFrames.put(frameToRegister.getUniqueName(), frameToRegister);
                frameToRegister.addInternalFrameListener(new MDIInternalFrameListener());
                frameToRegister.addMenuOwnerListener(this);
                frameToRegister.setMainFrame(this);
                frameToRegister.setVisible(true);
                frameToRegister.pack();

                getDesktopPane().add(frameToRegister);

                if (maximize) {
                    try {
                        frameToRegister.setMaximum(true);
                    }
                    catch (PropertyVetoException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }

                try {
                    frameToRegister.setSelected(true);
                }
                catch (PropertyVetoException e) {
                    LOGGER.warn(e.getMessage(), e);
                }

                addFrame2WindowMenu(frameToRegister);
            }
            else {
                activateInternalFrame(frameToRegister.getUniqueName());
            }
        }
    }

    /**
     * Entfernt das angegebene InternalFrame aus dem MDIFrame, d.h. es wird geschlossen. <br> Sofern die Methode
     * <code>getWindowMenu</code> eine Instanz von AKJMenu liefert und das Frame <code>frameToUnregister</code> in
     * diesem Menu eingetragen war, wird es daraus entfernt.
     *
     * @param frameToUnregister Frame, das geschlossen werden soll.
     */
    public void unregisterFrame(AKJInternalFrame frameToUnregister) {
        getDesktopPane().remove(frameToUnregister);
        getDesktopPane().updateUI();

        if (internalFrames != null) {
            removeFrameFromWindowMenu(frameToUnregister);
            internalFrames.remove(frameToUnregister.getUniqueName());
        }
    }

    /**
     * Erstellt fuer das InternalFrame ein MenuItem und ordnet es dem Menu zu, das die Methode
     * <code>getWindowMenu</code> liefert.
     *
     * @param frame Frame, fuer das ein MenuItem erstellt werden soll.
     */
    protected void addFrame2WindowMenu(AKJInternalFrame frame) {
        JMenu windowMenu = getWindowMenu();
        if (windowMenu != null) {
            if (windowMenu.getItemCount() <= getDefaultWindowMenuItemCount()) {
                windowMenu.addSeparator();
            }

            String title = frame.getTitle();
            int titleLength = title != null ? title.length() : 0;
            if (titleLength > MAX_FRAME_TITLE_LENGTH) {
                title = StringUtils.substring(title, 0, MAX_FRAME_TITLE_LENGTH) + "...";
            }

            ActivateInternalFrameAction action = new ActivateInternalFrameAction(frame);
            action.putValue(Action.NAME, title);
            action.putValue(Action.SMALL_ICON, frame.getFrameIcon());
            action.putValue(Action.SHORT_DESCRIPTION, frame.getTitle());
            windowMenu.add(new AKJMenuItem(action));
        }
    }

    /**
     * Entfernt das MenuItem fuer das InternalFrame aus dem Window-Menu.
     *
     * @param frame Frame, das aus dem Window-Menu entfernt werden soll.
     */
    protected void removeFrameFromWindowMenu(AKJInternalFrame frame) {
        JMenu windowMenu = getWindowMenu();
        if (windowMenu != null) {
            for (int i = 0; i < windowMenu.getItemCount(); i++) {
                JMenuItem item = windowMenu.getItem(i);
                if ((item != null) && (item.getAction() instanceof ActivateInternalFrameAction)) {
                    ActivateInternalFrameAction action = (ActivateInternalFrameAction) item.getAction();
                    if (StringUtils.equals(frame.getTitle(), action.getFrame().getTitle())) {
                        windowMenu.remove(i);
                        break;
                    }
                }
            }
            if ((windowMenu.getItemCount() == (getDefaultWindowMenuItemCount() + 1))) {
                // Separator entfernen
                windowMenu.remove(getDefaultWindowMenuItemCount());
            }
        }
    }

    /**
     * Fuegt das Menu <code>toAdd</code> der MenuBar an der Position <code>getMenuIndex4AdditionalMenu()</code> hinzu.
     * <br> Alle anderen Menus werden um eine Position nach hinten verschoben. <br> <br> <strong> Wichtig: Es kann immer
     * nur ein Menu hinzugefuegt werden. Wurde bereits ein Menu der MenuBar hinzugefuegt und wird die Methode nochmals
     * aufgerufen, wird das zuerst eingefuegte Menu entfernt und anschliessend das neue Menu der MenuBar hinzugefuegt.
     * </strong>
     *
     * @param toAdd
     */
    protected void addAdditionalMenu(AKJMenu toAdd) {
        removeAdditionalMenu();
        addMenu(toAdd, getMenuIndex4AdditionalMenu());
    }

    /**
     * Entfernt das Menu aus der MenuBar, das ueber die Methode <code>addAdditionalMenu</code> hinzugefuegt wurde.
     */
    protected void removeAdditionalMenu() {
        if (additionalMenu != null) {
            removeMenu(additionalMenu);
            additionalMenu = null;
        }
    }

    /**
     * Fuegt ein Menu an der angegebenen Stelle in der MenuBar hinzu. Alle 'dahinter' liegenden Menus werden um eine
     * Position nach rechts verschoben.
     *
     * @param toAdd Menu, das hinzugefuegt werden soll.
     * @param index Index, an dem das Menu platziert werden soll.
     */
    protected void addMenu(AKJMenu toAdd, int index) {
        if ((getJMenuBar() != null) && (toAdd != null)) {
            additionalMenu = toAdd;

            JMenuBar mb = getJMenuBar();
            List<JMenu> menusToMove = new ArrayList<>();
            for (int i = index; i < mb.getMenuCount(); i++) {
                menusToMove.add(mb.getMenu(i));
            }

            for (int i = index; i < mb.getMenuCount(); i++) {
                mb.remove(i);
            }

            mb.add(toAdd);
            for (int i = 0; i < menusToMove.size(); i++) {
                mb.add((Component) menusToMove.get(i));
            }
            mb.revalidate();
            mb.repaint();
        }
    }

    /**
     * Entfernt das Menu <code>toRemove</code> aus der MenuBar.
     *
     * @param toRemove
     */
    protected void removeMenu(AKJMenu toRemove) {
        if ((getJMenuBar() != null) && (toRemove != null)) {
            JMenuBar mb = getJMenuBar();
            int index = -1;
            for (int i = 0; i < mb.getMenuCount(); i++) {
                if (StringUtils.equals(((JMenu) mb.getComponent(i)).getText(), toRemove.getText())) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                getJMenuBar().remove(index);
            }
            getJMenuBar().revalidate();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKMenuOwnerListener#menuChanged(de.augustakom.common.gui.iface.AKMenuOwner)
     */
    @Override
    public void menuChanged(AKMenuOwner menuOwner) {
        if (menuOwner != null) {
            addAdditionalMenu(menuOwner.getMenuOfOwner());
        }
    }

    /**
     * Gibt den Index der MenuBar zurueck, an dem zusaetzliche Menus ueber die Methode <code>addMenu(AKJMenu)</code>
     * eingefuegt werden koennen.
     *
     * @return Index-Position.
     */
    protected int getMenuIndex4AdditionalMenu() {
        return 0;
    }

    /**
     * Ueberprueft, ob ein InternalFrame mit dem angegebenen Unique-Name (Methode: <code>AKJInternalFrame#getUniqueName()</code>)
     * bereits in dem MainFrame dargestellt wird. <br> Dabei ist es unwichtig, ob das InternalFrame gerade minimiert
     * oder nicht aktiv ist.
     *
     * @param uniqueName
     * @return
     */
    public boolean isFrameDisplayed(String uniqueName) {
        if (internalFrames != null) {
            return internalFrames.containsKey(uniqueName);
        }

        return false;
    }

    /**
     * Aktiviert ein InternalFrame
     *
     * @param uniqueName UniqueName des Frames, das aktiviert werden soll
     */
    public void activateInternalFrame(String uniqueName) {
        try {
            JInternalFrame[] frames = getDesktopPane().getAllFrames();
            for (JInternalFrame tmp : frames) {
                if (AKJInternalFrame.class.isInstance(tmp)
                        && StringUtils.equals(uniqueName, ((AKJInternalFrame) tmp).getUniqueName())) {
                    tmp.setSelected(true);
                    if (tmp.isIcon()) {
                        tmp.setIcon(false);
                    }
                    tmp.show();
                    break;
                }
            }
        }
        catch (PropertyVetoException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gibt das z.Z. aktive Frame zurueck.
     *
     * @return das aktive AKJInternalFrame.
     */
    public AKJInternalFrame getActiveFrame() {
        return (AKJInternalFrame) getDesktopPane().getSelectedFrame();
    }

    /**
     * Sucht nach allen InternalFrames, die z.Z. am MainFrame registriert sind und vom Typ <code>type</code> sind.
     *
     * @param type gesuchter Typ
     * @return Array mit den gefundenen Frames oder <code>null</code>
     */
    public AKJInternalFrame[] findInternalFrames(Class<?> type) {
        JInternalFrame[] registered = getDesktopPane().getAllFrames();

        List<Object> frames = new ArrayList<>();
        for (JInternalFrame tmp : registered) {
            if ((tmp instanceof AKJInternalFrame) && type.isAssignableFrom(tmp.getClass())) {
                frames.add(tmp);
            }
        }

        if (!frames.isEmpty()) {
            return frames.toArray(new AKJInternalFrame[frames.size()]);
        }
        return null;
    }

    /**
     * Stellt den angegebenen Text in der StatusBar dar.
     *
     * @param area Position in der StatusBar, in der der Text dargestellt werden soll (Beginn bei 0!).
     * @param text Text, der dargestellt werden soll
     * @param icon URL eines Icons, das dargestellt werden soll
     */
    public void setStatusText(int area, String text, String icon) {
        if (statusBar != null) {
            statusBar.setText(area, text, icon);
        }
    }

    /**
     * Zeigt in der StatusBar eine ProgressBar an.
     *
     * @param area          Area, in der die ProgressBar dargestellt bzw gestoppt werden soll.
     * @param progressState Status der ProgressBar <br> starten: <code>AKJStatusBar.START_PROGRESS</code> <br> stoppen:
     *                      <code>AKJStatusBar.STOP_PROGRESS</code>
     * @param text          darzustellender Text
     * @param paintBorder   Angabe, ob die ProgressBar einen Border besitzen soll (true) oder nicht (false).
     */
    public void showProgress(int area, int progressState, String text, boolean paintBorder) {
        if (statusBar != null) {
            statusBar.showProgress(area, progressState, text, paintBorder);
        }
    }

    public void showProgressInLastField(final String message) {
        if (statusBar != null) {
            showProgress(statusBar.getAreaCount() - 1, AKJStatusBar.START_PROGRESS, message, true);
        }
    }

    public void stopProgressInLastField() {
        if (statusBar != null) {
            showProgress(statusBar.getAreaCount() - 1, AKJStatusBar.STOP_PROGRESS, " ", false);
        }
    }

    /**
     * Stellt alle InternalFrames kaskadiert (versetzt) dar.
     */
    public void cascadeAll() {
        getDesktopPane().cascadeFrames();
    }

    /**
     * Minimiert alle InternalFrames, die im MDIFrame registriert sind.
     */
    public void iconifyAll() {
        getDesktopPane().iconifyFrames();
    }

    /**
     * Alle registrierten InternalFrames werden auf ihre urspruengliche Groesse gesetzt.
     */
    public void deIconifyAll() {
        getDesktopPane().deIconifyFrames();
    }

    /**
     * Schliesst alle registrierten InternalFrames.
     */
    public void closeAll() {
        getDesktopPane().closeFrames();
    }

    /**
     * Setzt den Cursor-Typ auf 'Wait'
     */
    public void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Setzt den Cursor-Typ auf 'Default'
     */
    public void setDefaultCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}
