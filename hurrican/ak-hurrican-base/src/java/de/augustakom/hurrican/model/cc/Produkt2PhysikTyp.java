/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 07:36:35
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modell bildet ein Mapping zw. einem Produkt und einem PhysikTyp ab.
 *
 *
 */
public class Produkt2PhysikTyp extends AbstractCCIDModel implements CCProduktModel, DebugModel {

    private Long produktId = null;
    private Long physikTypId = null;
    private Long physikTypAdditionalId = null;
    private Boolean virtuell = null;
    private Long parentPhysikTypId = null;
    private Long priority = null;
    private Boolean useInRangMatrix = null;

    /**
     * Ueberprueft, ob der aktuelle Produkt-Physiktyp Zuordnung zu den Physiktypen der angegebenen Rangierungen
     * kompatibel ist. <br>
     *
     * @param rangierung
     * @param rangierungAdditional
     * @return
     *
     */
    public boolean isCompatible(Rangierung rangierung, Rangierung rangierungAdditional) {
        if (((rangierung == null) && (rangierungAdditional == null)) || (rangierung == null)) {
            return false;
        }

        if (NumberTools.equal(getPhysikTypId(), rangierung.getPhysikTypId())) {
            if (getPhysikTypAdditionalId() != null) {
                if (rangierungAdditional == null) {
                    return false;
                }
                else {
                    return NumberTools.equal(
                            getPhysikTypAdditionalId(), rangierungAdditional.getPhysikTypId());
                }
            }
            return true;
        }

        return false;
    }

    @Transient
    public boolean isSimple() {
        if ((getPhysikTypAdditionalId() == null) && (getParentPhysikTypId() == null)) {
            return true;
        }
        return false;
    }

    public Long getParentPhysikTypId() {
        return parentPhysikTypId;
    }

    public void setParentPhysikTypId(Long parentPhysikTypId) {
        this.parentPhysikTypId = parentPhysikTypId;
    }

    public Long getPhysikTypId() {
        return physikTypId;
    }

    public void setPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
    }

    public Long getPhysikTypAdditionalId() {
        return this.physikTypAdditionalId;
    }

    public void setPhysikTypAdditionalId(Long physikTypAdditionalId) {
        this.physikTypAdditionalId = physikTypAdditionalId;
    }

    @Override
    @Transient
    public Long getProdId() {
        return getProduktId();
    }

    public Long getProduktId() {
        return produktId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    public Boolean getVirtuell() {
        return virtuell;
    }

    public void setVirtuell(Boolean virtuell) {
        this.virtuell = virtuell;
    }

    /**
     * Gibt die Prioritaet fuer die Produkt/Physiktyp Kombination auf dem Produkt an. <br/>
     * Ein hoeherer Wert bedeutet dabei auch eine hoehere Prioritaet; keine Angabe (also {@code null} bedeutet
     * 'keine Prio'). <br/>
     * In der Auswertung der Prioritaet fuer die Rangierungszuordnung hat die Prio auf der {@link Rangierungsmatrix}
     * jedoch Vorrang vor dieser Prio. Dadurch ist sichergestellt, dass die Prio noch standort-abhaengig uebersteuert
     * werden kann.
     * @return
     */
    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    /**
     * Flag, welches anzeigt, ob ein fuer einen Physiktyp eine Rangierungsmatrix erstellt werden darf, oder nicht.
     */
    public Boolean getUseInRangMatrix() {
        return useInRangMatrix;
    }

    public void setUseInRangMatrix(Boolean useInRangMatrix) {
        this.useInRangMatrix = useInRangMatrix;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Produkt2PhysikTyp.class.getName());
            logger.debug("  ID             : " + getId());
            logger.debug("  PhysikTyp      : " + getPhysikTypId());
            logger.debug("  PhysikTyp add. : " + getPhysikTypAdditionalId());
            logger.debug("  Parent PT      : " + getParentPhysikTypId());
            logger.debug("  Virtuell       : " + getVirtuell());
            logger.debug("  Priority       : " + getPriority());
            logger.debug("  UseInRangMatrix: " + getUseInRangMatrix());
        }
    }
}


