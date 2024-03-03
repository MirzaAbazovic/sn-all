/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 13:51:17
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;


/**
 * Baut sich automatisch ein Feature.
 *
 *
 */
@SuppressWarnings("unused")
public class FeatureBuilder extends AbstractCCIDModelBuilder<FeatureBuilder, Feature> implements IServiceObject {

    private FeatureName name = null;
    private Boolean flag = null;

    public FeatureBuilder withNameAndFlag(FeatureName name, Boolean flag) {
        this.name = name;
        this.flag = flag;
        return this;
    }

}
