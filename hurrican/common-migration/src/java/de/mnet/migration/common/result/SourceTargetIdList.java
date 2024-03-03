/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2010 09:27:57
 */
package de.mnet.migration.common.result;

import java.util.*;


/**
 * List of IDs, to be specialized for source/target. Immutable.
 *
 *
 */
public abstract class SourceTargetIdList<SUBCLASS extends SourceTargetIdList<?>> {
    private List<SourceTargetId> list = new ArrayList<SourceTargetId>();

    protected SourceTargetIdList(List<SourceTargetId> list) {
        if (list != null) {
            for (SourceTargetId sourceTargetId : list) {
                sourceTargetId.setSource(isSource());
            }
            this.list = list;
        }
    }

    protected SourceTargetIdList(SourceTargetId... ids) {
        if (ids != null) {
            list = Arrays.asList(ids);
            for (SourceTargetId sourceTargetId : list) {
                sourceTargetId.setSource(isSource());
            }
        }
    }

    protected abstract SUBCLASS construct(List<SourceTargetId> list);

    protected abstract boolean isSource();

    public SUBCLASS merge(SUBCLASS toMerge) {
        List<SourceTargetId> merged = new ArrayList<SourceTargetId>(list);
        if (toMerge != null) {
            for (SourceTargetId id : toMerge.getList()) {
                if (!merged.contains(id)) {
                    merged.add(id);
                }
            }
        }
        return construct(merged);
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public List<SourceTargetId> getList() {
        return list;
    }
}
