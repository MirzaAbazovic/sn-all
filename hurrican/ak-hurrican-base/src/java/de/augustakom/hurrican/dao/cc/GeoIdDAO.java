package de.augustakom.hurrican.dao.cc;

import de.augustakom.hurrican.model.cc.GeoIdLocation;

public interface GeoIdDAO {
    <T extends GeoIdLocation> T save(T location);

    <T extends GeoIdLocation> T findLocation(Class<T> clazz, long id);
}
