package de.mnet.migration.hurrican.common;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;


/**
 * Abstract Superclass for common Hurrican migration features
 */
public abstract class HurricanTransformator<T> extends MigrationTransformator<T> {

    protected static final Date BEGIN_DATE = DateTools.createDate(2010, 0, 1);
    protected static final Date END_DATE = DateTools.getHurricanEndDate();

    public static class HurricanMessages extends Messages {
        public Messages.Message DATA_NOT_FOUND_IN_HURRICAN = new Messages.Message(
                TransformationStatus.BAD_DATA, 0x4000000000000000L,
                "Daten nicht in Hurrican gefunden fuer ID %s");
        public Messages.Message HURRICAN_EXCEPTION = new Messages.Message(
                TransformationStatus.ERROR, 0x8000000000000000L,
                "Unexpected Hurrican-Exception");
        /**
         * add messages common to more than one hurrican migrations here
         */
    }

    protected <E extends AbstractCCIDModel> TargetIdList createTargetIdList(String prefix, List<E> createdElements) {
        List<SourceTargetId> ids = new ArrayList<SourceTargetId>();
        for (int i = 0; i < createdElements.size(); ++i) {
            ids.add(id(prefix + i, createdElements.get(i).getId()));
        }
        return targets(ids);
    }
}
