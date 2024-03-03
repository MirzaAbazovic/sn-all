package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@MappedSuperclass
@ObjectsAreNonnullByDefault
public abstract class GeoIdLocation extends AbstractCCModel {
    public static final String SERVICEABLE = "serviceable";
    public static final String REPLACED_BY = "replacedById";

    @NotNull
    private Long id = Long.valueOf(0);
    @Nullable
    private Long replacedById;
    @Nullable
    private String technicalId;
    @NotNull
    private Date modified = new Date();
    @NotNull
    private Boolean serviceable = Boolean.FALSE;

    @NotNull
    @Id
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    @Column(name = "REPLACED_BY_ID")
    public Long getReplacedById() {
        return replacedById;
    }

    public void setReplacedById(@Nullable Long replacedById) {
        this.replacedById = replacedById;
    }

    @Nullable
    @Column(name = "TECHNICAL_ID")
    public String getTechnicalId() {
        return technicalId;
    }

    public void setTechnicalId(@Nullable String technicalId) {
        this.technicalId = technicalId;
    }

    @NotNull
    @Column(name = "MODIFIED")
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @NotNull
    @Column(name = "SERVICEABLE")
    public Boolean isServiceable() {
        return serviceable;
    }

    public void setServiceable(Boolean serviceable) {
        if (serviceable == null) {
            serviceable = Boolean.FALSE;
        }
        this.serviceable = serviceable;
    }
}
