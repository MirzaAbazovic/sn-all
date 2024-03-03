/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2011 11:50:21
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell-Klasse, um eine Konfiguration zwischen Produkt und Standorttyp inkl. Priorisierung zu halten.
 */
@Entity
@Table(name = "T_PRODUKT_2_TECH_LOCATION_TYPE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PRODUKT_2_TECH_LOC_TYPE_0", allocationSize = 1)
public class Produkt2TechLocationType extends AbstractCCIDModel {

    public static final String PRIORITY = "priority";

    private Long produktId;
    private Long techLocationTypeRefId;
    private Integer priority;
    private String userW;
    private Date dateW;

    /**
     * Ermittelt aus der angegebenen Liste {@code references} den {@link Reference} Eintrag, der zu dem aktuellen Objekt
     * (und somit zu {@code techLocationTypeRefId} passt.
     *
     * @param references
     * @return
     */
    public Reference findTechLocationTypeReference(List<Reference> references) {
        if (CollectionTools.isEmpty(references) || (techLocationTypeRefId == null)) {
            return null;
        }

        for (Reference reference : references) {
            if (reference.getId().equals(techLocationTypeRefId)) {
                return reference;
            }
        }
        return null;
    }

    @Column(name = "PROD_ID")
    @NotNull
    public Long getProduktId() {
        return produktId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * Die ID der Tech Location.<br> <b>Achtung:</b> es werden die Standort-Typ Referenzen verwendet, siehe HVTStandort
     * Konstanten mit Prefix HVT_STANDORT_TYP....
     *
     * @return
     */
    @Column(name = "TECH_LOCATION_TYPE_REF_ID")
    @NotNull
    public Long getTechLocationTypeRefId() {
        return techLocationTypeRefId;
    }

    public void setTechLocationTypeRefId(Long techLocationTypeRefId) {
        this.techLocationTypeRefId = techLocationTypeRefId;
    }

    public static boolean containsTechLocationTypeRefId(List<Produkt2TechLocationType> list2Check,
            final Long techLocationTypeRefId) {
        Iterable<Produkt2TechLocationType> filteredList = Iterables.filter(list2Check,
                new Predicate<Produkt2TechLocationType>() {
                    @Override
                    public boolean apply(Produkt2TechLocationType type) {
                        return NumberTools.equal(type.getTechLocationTypeRefId(), techLocationTypeRefId);
                    }
                }
        );
        return (Iterables.isEmpty(filteredList)) ? false : true;
    }

    @Column(name = "PRIORITY")
    @NotNull
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "USERW")
    @NotNull
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "DATEW")
    @NotNull
    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

}


