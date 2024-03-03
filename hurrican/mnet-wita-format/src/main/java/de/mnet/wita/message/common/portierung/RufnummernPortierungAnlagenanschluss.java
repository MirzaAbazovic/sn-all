/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 12:55:46
 */
package de.mnet.wita.message.common.portierung;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.validators.AbfragestelleValid;
import de.mnet.wita.validators.AnlagenanschlussRufnummerValid;
import de.mnet.wita.validators.DurchwahlValid;
import de.mnet.wita.validators.OnkzValid;
import de.mnet.wita.validators.RufnummernBlockValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@AnlagenanschlussRufnummerValid
@Entity
@DiscriminatorValue("anlagenanschluss")
public class RufnummernPortierungAnlagenanschluss extends RufnummernPortierung {

    private static final long serialVersionUID = 6732984340164462129L;


    private String onkz;
    private String durchwahl;
    private String abfragestelle;
    private List<RufnummernBlock> rufnummernBloecke = new ArrayList<RufnummernBlock>();

    @Transient
    public boolean isFachlichEqual(RufnummernPortierungAnlagenanschluss other) {
        // @formatter:off
        if (!abfragestelle.equals(other.getAbfragestelle())
                || !durchwahl.equals(other.getDurchwahl())
                || !onkz.equals(other.getOnkz())) {
            return false;
        }
        // @formatter:on
        Set<RufnummernBlock> rufnummernBloeckeSet = new TreeSet<RufnummernBlock>(RufnummernBlock.FACHLICHER_COMPARATOR);
        rufnummernBloeckeSet.addAll(rufnummernBloecke);
        for (RufnummernBlock rufnummernBlock : other.getRufnummernBloecke()) {
            if (!rufnummernBloeckeSet.contains(rufnummernBlock)) {
                return false;
            }
            rufnummernBloeckeSet.remove(rufnummernBlock);
        }
        return rufnummernBloeckeSet.isEmpty();
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    @OnkzValid
    @NotNull
    public String getOnkz() {
        return onkz;
    }

    public void setDurchwahl(String durchwahl) {
        this.durchwahl = durchwahl;
    }

    @DurchwahlValid
    @NotNull
    public String getDurchwahl() {
        return durchwahl;
    }

    public void setAbfragestelle(String abfragestelle) {
        this.abfragestelle = abfragestelle;
    }

    @AbfragestelleValid
    @NotNull
    public String getAbfragestelle() {
        return abfragestelle;
    }

    @NotNull
    @RufnummernBlockValid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "PORTIERUNGS_ID")
    public List<RufnummernBlock> getRufnummernBloecke() {
        return rufnummernBloecke;
    }

    /**
     * Required by Hibernate
     */
    @SuppressWarnings("unused")
    private void setRufnummernBloecke(List<RufnummernBlock> rufnummernBloecke) {
        this.rufnummernBloecke = rufnummernBloecke;
    }

    public void addRufnummernBlock(RufnummernBlock rufnummernBlock) {
        this.rufnummernBloecke.add(rufnummernBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rufnummerportierung für Anlagenanschluss [Vorwahl: ").append(onkz)
                .append(", Durchwahl: ").append(durchwahl)
                .append(", Abfragestelle: ").append(abfragestelle).append("]");
        for (RufnummernBlock block : rufnummernBloecke) {
            sb.append("\n").append(block.toString());
        }
        return sb.toString();
    }

}
