/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2015 14:27
 */
package de.augustakom.authentication.gui.bereich;

import java.util.*;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.collect.ImmutableList;

import de.augustakom.authentication.gui.basics.BereichListCellRenderer;
import de.augustakom.authentication.model.AKBereich;
import de.augustakom.common.gui.swing.AKJComboBox;

/**
 *
 */
public final class BereichComboBox extends AKJComboBox {
    private static final AKBereich EMPTY_BEREICH = new AKBereich("----", 0L);

    public BereichComboBox(final Collection<AKBereich> bereiche) {
        setRenderer(new BereichListCellRenderer());
        List<AKBereich> data = ImmutableList.copyOf(bereiche);
        final DefaultComboBoxModel<AKBereich> bereichComboBoxModel = new DefaultComboBoxModel<>();
        data.stream().forEach(bereichComboBoxModel::addElement);
        bereichComboBoxModel.addElement(EMPTY_BEREICH);
        this.setModel(bereichComboBoxModel);
        this.deselect();
    }

    public Optional<AKBereich> getSelectedBereich() {
        return isBereichSelected()
                ? Optional.of((AKBereich) this.getSelectedItem())
                : Optional.empty();
    }

    public boolean isBereichSelected() {
        return (this.getSelectedItem() instanceof AKBereich) && (this.getSelectedItem() != EMPTY_BEREICH);
    }

    public BereichComboBox select(@CheckForNull final AKBereich bereich) {
        if (bereich == null) {
            deselect();
        }
        else {
            for (int i = 0; i < this.getModel().getSize(); i++) {
                final Object obj = this.getModel().getElementAt(i);
                if (obj instanceof AKBereich && bereich.equals(obj)) {
                    this.setSelectedItem(obj);
                }
            }
        }
        return this;
    }

    public void deselect() {
        this.setSelectedItem(EMPTY_BEREICH);
    }
}
