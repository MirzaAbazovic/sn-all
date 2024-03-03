/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2011 15:15:04
 */
package de.mnet.wita.message.meldung.position;

import static com.google.common.collect.Lists.*;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.validators.RufnummernBlockValid;

@Embeddable
public class AnschlussPortierungKorrekt implements Serializable {

    private static final long serialVersionUID = 5772048166190233818L;

    private BestandsSuche onkzDurchwahlAbfragestelle;
    private List<RufnummernBlock> rufnummernbloecke = newArrayList();

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BESTANDSSUCHE_ID")
    public BestandsSuche getOnkzDurchwahlAbfragestelle() {
        return onkzDurchwahlAbfragestelle;
    }

    public void setOnkzDurchwahlAbfragestelle(BestandsSuche onkzDurchwahlAbfragestelle) {
        this.onkzDurchwahlAbfragestelle = onkzDurchwahlAbfragestelle;
    }

    @NotNull
    @RufnummernBlockValid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "ANSCHLUSS_PORTIERUNG_KORR_ID")
    public List<RufnummernBlock> getRufnummernbloecke() {
        return rufnummernbloecke;
    }

    @Transient
    public void addRufnummernblock(RufnummernBlock rufnummernBlockToAdd) {
        rufnummernbloecke.add(rufnummernBlockToAdd);
    }

    /**
     * Required by Hibernate
     */
    @SuppressWarnings("unused")
    private void setRufnummernbloecke(List<RufnummernBlock> rufnummernbloecke) {
        this.rufnummernbloecke = rufnummernbloecke;
    }

    @Override
    public String toString() {
        return "AnschlussPortierungKorrekt: [onkzDurchwahlAbfragestelle=" + onkzDurchwahlAbfragestelle
                + ", rufnummernbloecke=" + rufnummernbloecke + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((onkzDurchwahlAbfragestelle == null) ? 0 : onkzDurchwahlAbfragestelle.hashCode());
        result = (prime * result) + ((rufnummernbloecke == null) ? 0 : rufnummernbloecke.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AnschlussPortierungKorrekt other = (AnschlussPortierungKorrekt) obj;
        if (onkzDurchwahlAbfragestelle == null) {
            if (other.onkzDurchwahlAbfragestelle != null) {
                return false;
            }
        }
        else if (!onkzDurchwahlAbfragestelle.equals(other.onkzDurchwahlAbfragestelle)) {
            return false;
        }
        if (rufnummernbloecke == null) {
            if (other.rufnummernbloecke != null) {
                return false;
            }
        }
        else if (!rufnummernbloecke.equals(other.rufnummernbloecke)) {
            return false;
        }
        return true;
    }

}
