/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2012 15:33:53
 */
package de.augustakom.authentication.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 */
@Entity
@Table(name = "TEAM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_TEAM_0", allocationSize = 1)
public class AKTeam extends AbstractAuthenticationModel {

    private static final long serialVersionUID = -46958221065478842L;
    private Long id;
    private String name;

    // needed by Hibernate / JPA
    public AKTeam() {
    }

    public AKTeam(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TEAM_NAME", length = 30)
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
