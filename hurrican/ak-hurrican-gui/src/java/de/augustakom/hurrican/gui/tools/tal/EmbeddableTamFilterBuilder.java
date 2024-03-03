/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2016
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.event.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.SwingFactory;

public final class EmbeddableTamFilterBuilder {
    private List<String> toggleButtonCommands;
    private AKJComboBox additionalFilterComponent;
    private ActionListener actionListener;
    private SwingFactory swingFactory;

    private EmbeddableTamFilterBuilder() {
    }

    public static EmbeddableTamFilterBuilder builder() {
        return new EmbeddableTamFilterBuilder();
    }

     EmbeddableTamFilterBuilder withToggleButtonCommands(List<String> toggleButtonCommands) {
        this.toggleButtonCommands = toggleButtonCommands;
        return this;
    }

    EmbeddableTamFilterBuilder withAdditionalFilterComponent(AKJComboBox additionalFilterComponent) {
        this.additionalFilterComponent = additionalFilterComponent;
        return this;
    }

    EmbeddableTamFilterBuilder withActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    EmbeddableTamFilterBuilder withSwingFactory(SwingFactory swingFactory) {
        this.swingFactory = swingFactory;
        return this;
    }

    public EmbeddableTamFilter build() {
        return new EmbeddableTamFilter(toggleButtonCommands, additionalFilterComponent, actionListener, swingFactory);
    }
}

