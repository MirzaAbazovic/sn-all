/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2010 16:35:01
 */

package de.augustakom.hurrican.gui.tools.scheduler.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.scheduler.controller.AKSchedulerControllerFrame;


/**
 * Action, um den Dialog fuer den Scheduler-Controller anzuzeigen.
 *
 *
 */
public class OpenSchedulerControllerFrameAction extends AKAbstractOpenFrameAction {

    public static final String SCHEDULER_DEFAULT = "default.Scheduler";
    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    public AKJInternalFrame getFrameToOpen() {
        AKSchedulerControllerFrame frame = new AKSchedulerControllerFrame(SCHEDULER_DEFAULT);
        uniqueName = frame.getUniqueName();
        return frame;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getMainFrame()
     */
    @Override
    public AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getUniqueName()
     */
    @Override
    protected String getUniqueName() {
        return uniqueName;
    }

}
