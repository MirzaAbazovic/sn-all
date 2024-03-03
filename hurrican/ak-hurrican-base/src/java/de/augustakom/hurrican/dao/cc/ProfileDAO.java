package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


public interface ProfileDAO extends FindDAO, FindAllDAO, StoreDAO {
    /**
     *  Retrieves profile defaults
     */
    List<ProfileParameter> findProfileParameters(Long baugruppenTypId);

    /**
     *  Retrieves profileAuftrag object as well as auftrag values by auftrag Id
     */
    Optional<ProfileAuftrag> findProfileAuftrag(Long auftragId, Date date);

    /**
     *  Retrives mapping values by parameter name
     */
    ProfileParameterMapper findParameterMapper(String parameterName);

    /**
     *  Retrives default profile by baugruppenTyp and parameterName
     */
    List<ProfileParameter> findProfileParameterDefaults(Long baugruppenTypId);

    List<String> findParameterNamesOfBaugruppenTyp(HWBaugruppenTyp baugruppenTyp);

    List<String> findParameterValuesByName(String name);

    String findDefaultParameterValueByName(String name);

    List<ProfileParameter> findParametersWithoutDefault(Long baugruppenTypId);

    List<HWBaugruppe> findHWBaugruppenByAuftragId(final Long auftragId);

    /**
     * Retrieves an equipment by auftragId. Excludes 'Out'- and 'Additional'-Equipments
     *
     * @param auftragId
     * @return
     */
    Equipment findEquipmentsByAuftragId(final Long auftragId);
}
