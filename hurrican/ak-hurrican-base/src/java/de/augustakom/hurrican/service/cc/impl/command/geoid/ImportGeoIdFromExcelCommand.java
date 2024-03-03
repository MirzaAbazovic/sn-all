package de.augustakom.hurrican.service.cc.impl.command.geoid;

import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.StoreException;


public interface ImportGeoIdFromExcelCommand {

    public Object execute() throws Exception;

    public Map<String, Object> importGeoIds() throws StoreException;


}
