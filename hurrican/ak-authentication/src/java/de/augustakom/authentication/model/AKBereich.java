/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2015 13:12
 */
package de.augustakom.authentication.model;


import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "bereich")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_BEREICH_0")
public class AKBereich extends AbstractAuthenticationModel {

    private static final long serialVersionUID = -1643929202861401234L;

    /**
     * nur fuer Hibernate, nicht verwenden!
     */
    protected AKBereich() {
    }

    public AKBereich(@Nonnull final String name, final @Nonnull Long bereichsleiter) {
        this.name = name;
        this.bereichsleiter = bereichsleiter;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private Long id;

    @Nonnull
    private String name;

    private Long bereichsleiter;

    public Long getId() {
        return id;
    }

    /**
     * nur fuer Hibernate, nicht verwenden!
     */
    protected void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Column(name = "bereichsleiter")
    @Nonnull
    public Long getBereichsleiter() {
        return bereichsleiter;
    }

    public void setBereichsleiter(@Nonnull Long bereichsleiter) {
        this.bereichsleiter = bereichsleiter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bereichsleiter);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final AKBereich other = (AKBereich) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.bereichsleiter, other.bereichsleiter);
    }
}
