/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 12:55:46
 */
package de.mnet.wita.message.common.portierung;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;

@Entity
@Table(name = "T_MWF_PORTIERUNG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "portierungstyp", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_PORTIERUNG_0", allocationSize = 1)
public abstract class RufnummernPortierung extends MwfEntity {

    private static final long serialVersionUID = -4757899782359355304L;

    public static final String RUFNUMMERN_PORTIERUNG_PROPERTY_PATH = "geschaeftsfall.auftragsPosition.geschaeftsfallProdukt.rufnummernPortierung";

    private String portierungsKenner;

    public void setPortierungsKenner(String portierungsKenner) {
        this.portierungsKenner = portierungsKenner;
    }

    @Pattern(regexp = "D[0-9]{3}\\-[0-9]{3}", message = "Carrier code must be set correctly")
    public String getPortierungsKenner() {
        return portierungsKenner;
    }

    /**
     * Der PortierungsKenner soll ignoriert werden, da dieser bei AKM-PVs nicht gesetzt ist.
     */
    @Transient
    public boolean isFachlichEqual(RufnummernPortierung other) {
        if ((this instanceof RufnummernPortierungAnlagenanschluss)
                && (other instanceof RufnummernPortierungAnlagenanschluss)) {
            RufnummernPortierungAnlagenanschluss current = (RufnummernPortierungAnlagenanschluss) this;
            return current.isFachlichEqual((RufnummernPortierungAnlagenanschluss) other);
        }
        else if ((this instanceof RufnummernPortierungEinzelanschluss)
                && (other instanceof RufnummernPortierungEinzelanschluss)) {
            RufnummernPortierungEinzelanschluss current = (RufnummernPortierungEinzelanschluss) this;
            return current.isFachlichEqual((RufnummernPortierungEinzelanschluss) other);
        }
        return false;
    }

}


