/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 14:01:08
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Modellklasse zur Abbildung eines Ekp-Rahmenvertrags
 */
@ObjectsAreNonnullByDefault
@Entity
@Table(name = "T_FTTX_EKP_FRAME_CONTRACT",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "EKP_ID", "FRAME_CONTRACT_ID" }) })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FTTX_EKP_FRAME_CONTRACT_0", allocationSize = 1)
public class EkpFrameContract extends AbstractCCIDModel {
    private static final long serialVersionUID = 1L;

    public static final String EKP_ID_MNET = "MNET";
    public static final String FRAME_CONTRACT_ID_MNET = "MNET-001";

    public static final String EKP_ID = "ekpId";
    private String ekpId;

    public static final String FRAME_CONTRACT_ID = "frameContractId";
    private String frameContractId;

    private List<CVlan> cvlans = Lists.newArrayList();

    private Map<A10NspPort, Boolean> a10NspPortsOfEkp = new HashMap<A10NspPort, Boolean>();

    private Integer svlanFaktor;

    @Column(name = "EKP_ID")
    @NotNull
    public String getEkpId() {
        return ekpId;
    }

    public void setEkpId(String ekpId) {
        this.ekpId = ekpId;
    }

    @Column(name = "FRAME_CONTRACT_ID", unique = true)
    @NotNull
    public String getFrameContractId() {
        return frameContractId;
    }

    public void setFrameContractId(String frameContractId) {
        this.frameContractId = frameContractId;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.DELETE_ORPHAN, org.hibernate.annotations.CascadeType.ALL })
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "EKP_FRAME_CONTRACT_ID", nullable = false)
    public List<CVlan> getCvlans() {
        return cvlans;
    }

    /**
     * For hibernate
     */
    @SuppressWarnings("unused")
    private void setCvlans(List<CVlan> cvlans) {
        this.cvlans = cvlans;
    }

    @Transient
    public Set<CvlanServiceTyp> getCvlanServiceTypes() {
        Set<CvlanServiceTyp> cvlanServiceTypes = new HashSet<CvlanServiceTyp>();
        if (cvlans != null) {
            for (CVlan cvlan : cvlans) {
                cvlanServiceTypes.add(cvlan.getTyp());
            }
        }
        return cvlanServiceTypes;
    }

    @Transient
    public
    @CheckForNull
    CVlan getCvlanOfType(CvlanServiceTyp typ) {
        for (CVlan cvlan : cvlans) {
            if (typ.equals(cvlan.getTyp())) {
                return cvlan;
            }
        }
        return null;
    }

    /**
     * Map mit den zum EKP Rahmenvertrag zugeordneten {@link A10NspPort}s; <br> Der Boolean-Value gibt an, ob der A10NSP
     * Port fuer diesen EKP-Rahmenvertrag der Default ist oder nicht. <br> <b>Achtung:</b> es muss selbst dafuer gesorgt
     * werden, dass pro EKP nur ein {@link A10NspPort} als Default hinterlegt wird!
     */
    @ElementCollection(targetClass = Boolean.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_FTTX_EKP_2_A10NSPPORT",
            joinColumns = @JoinColumn(name = "EKP_FRAME_CONTRACT_ID"))
    @MapKeyJoinColumn(
            table = "T_FTTX_A10_NSP_PORT",
            name = "A10_NSP_PORT_ID" )
    @Column(name = "IS_DEFAULT_4_EKP")
    @Fetch(FetchMode.SUBSELECT)
    public Map<A10NspPort, Boolean> getA10NspPortsOfEkp() {
        return a10NspPortsOfEkp;
    }

    /**
     * For hibernate
     */
    @SuppressWarnings("unused")
    private void setA10NspPortsOfEkp(Map<A10NspPort, Boolean> a10NspPortsOfEkp) {
        this.a10NspPortsOfEkp = a10NspPortsOfEkp;
    }

    @Column(name = "svlan_faktor", nullable = false)
    public Integer getSvlanFaktor() {
        return svlanFaktor;
    }

    public void setSvlanFaktor(Integer svlanFaktor) {
        this.svlanFaktor = svlanFaktor;
    }
}


