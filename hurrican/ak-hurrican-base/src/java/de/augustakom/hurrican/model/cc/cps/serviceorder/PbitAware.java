package de.augustakom.hurrican.model.cc.cps.serviceorder;

import javax.annotation.*;

public interface PbitAware {

    void addPbit(@Nonnull CPSPBITData pbit);

}
