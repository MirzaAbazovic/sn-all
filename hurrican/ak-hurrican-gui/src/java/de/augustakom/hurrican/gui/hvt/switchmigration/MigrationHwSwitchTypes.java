/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2011 11:37:09
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Reraesentiert die Kombination aus Ursprungs-{@link HWSwitchType} und Ziel-{@link HWSwitchType} fuer eine
 * Switchmigration.
 *
 *
 * @since Release 10
 */
class MigrationHwSwitchTypes {

    private HWSwitchType source;
    private HWSwitchType destination;

    MigrationHwSwitchTypes() {
    }

    static MigrationHwSwitchTypes create(HWSwitchType source, HWSwitchType destination) {
        MigrationHwSwitchTypes types = new MigrationHwSwitchTypes();
        types.setSource(source);
        types.setDestination(destination);
        return types;
    }

    boolean isSourceHwSwitchImsAndDestinationHwSwitchNot() {
        return HWSwitchType.IMS.equals(getSource())
                && !HWSwitchType.IMS.equals(getDestination());
    }

    private boolean isSourceHwSwitchNspAndDestinationHwSwitchNot() {
        return HWSwitchType.NSP.equals(getSource())
                && !HWSwitchType.NSP.equals(getDestination());
    }

    boolean isDestinationHwSwitchImsAndSourceHwSwitchNot() {
        return HWSwitchType.IMS.equals(getDestination())
                && !HWSwitchType.IMS.equals(getSource());
    }

    private boolean isDestinationHwSwitchNspAndSourceHwSwitchNot() {
        return HWSwitchType.NSP.equals(getDestination())
                && !HWSwitchType.NSP.equals(getSource());
    }

    boolean isMigrationNeeded() {
        return ((isSourceHwSwitchImsAndDestinationHwSwitchNot() ^ isDestinationHwSwitchImsAndSourceHwSwitchNot())
                || (isSourceHwSwitchNspAndDestinationHwSwitchNot() ^ isDestinationHwSwitchNspAndSourceHwSwitchNot()));
    }

    /**
     * @param source The source to set.
     */
    private void setSource(HWSwitchType source) {
        this.source = source;
    }

    /**
     * @param destination The destination to set.
     */
    private void setDestination(HWSwitchType destination) {
        this.destination = destination;
    }

    /**
     * @return Returns the source.
     */
    HWSwitchType getSource() {
        return source;
    }

    /**
     * @return Returns the destination.
     */
    HWSwitchType getDestination() {
        return destination;
    }

} // end
