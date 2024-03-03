/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2009 13:41:27
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.FeatureDAO;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.service.cc.FeatureService;

/**
 * Service-Implementierung fuer FeatureService.
 */
@CcTxRequiredReadOnly
public class FeatureServiceImpl extends DefaultCCService implements FeatureService {

    private static final Logger LOGGER = Logger.getLogger(FeatureServiceImpl.class);

    private FeatureDAO featureDAO;

    @Override
    public Boolean isFeatureOnline(FeatureName featureName) {
        Feature example = new Feature();
        example.setName(featureName);
        List<Feature> features = featureDAO.queryByExample(example, Feature.class);

        Boolean result = Boolean.FALSE;
        if (features.size() > 1) {
            LOGGER.error("Mehr als ein Feature mit der name " + featureName + " gefunden!");
        }
        else if (!features.isEmpty()) {
            result = features.get(0).getFlag();
        }
        return result;
    }

    @Override
    public Boolean isFeatureOffline(FeatureName name) {
        return !Boolean.TRUE.equals(isFeatureOnline(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllOnlineFeatures() {
        Feature example = new Feature();
        example.setFlag(true);
        final List<Feature> features = featureDAO.queryByExample(example, Feature.class);
        Set<String> featureNames = new HashSet<>(features.size());
        for (Feature feature : features) {
            featureNames.add(feature.getName().name());
        }
        return featureNames;
    }

    public FeatureDAO getFeatureDAO() {
        return featureDAO;
    }

    public void setFeatureDAO(FeatureDAO featureDAO) {
        this.featureDAO = featureDAO;
    }
}
