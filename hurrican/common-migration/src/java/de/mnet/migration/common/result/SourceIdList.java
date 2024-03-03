/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2010 09:30:26
 */
package de.mnet.migration.common.result;

import java.util.*;


/**
 * List of source IDs for migration result. Immutable.
 *
 *
 */
public class SourceIdList extends SourceTargetIdList<SourceIdList> {
    public SourceIdList(SourceTargetId... ids) {
        super(ids);
    }

    public SourceIdList(List<SourceTargetId> list) {
        super(list);
    }

    @Override
    protected boolean isSource() {
        return true;
    }

    @Override
    protected SourceIdList construct(List<SourceTargetId> list) {
        return new SourceIdList(list);
    }
}
