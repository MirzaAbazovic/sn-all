/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2016
 */
package de.augustakom.hurrican.gui.hvt.switchmigration.util;

/**
 * Interface as alternative for Function<Void, Void>
 */
public interface SideEffect {
    void execute();
}