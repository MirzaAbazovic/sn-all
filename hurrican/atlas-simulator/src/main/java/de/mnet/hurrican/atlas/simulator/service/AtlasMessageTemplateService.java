/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.service;

import java.io.*;
import java.util.*;
import com.consol.citrus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.mnet.hurrican.atlas.simulator.ffm.FFMVersion;
import de.mnet.hurrican.atlas.simulator.ffm.builder.FFM_TRIGGER_NotifyFeedback;
import de.mnet.hurrican.atlas.simulator.ffm.builder.FFM_TRIGGER_NotifyUpdate;
import de.mnet.hurrican.simulator.config.SimulatorConfiguration;
import de.mnet.hurrican.simulator.model.MessageTemplate;
import de.mnet.hurrican.simulator.service.MessageTemplateService;

/**
 *
 */
public class AtlasMessageTemplateService implements MessageTemplateService {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(AtlasMessageTemplateService.class);

    @Autowired
    private SimulatorConfiguration simulatorConfiguration;

    @Override
    public List<MessageTemplate> getDefaultMessageTemplates() {
        List<MessageTemplate> defaultTemplates = new ArrayList<>();

        try {
            defaultTemplates.add(new MessageTemplate("notifyUpdate",
                    FileUtils.readToString(getFfmFileResource("notifyOrderUpdate")),
                    FFM_TRIGGER_NotifyUpdate.class));
            defaultTemplates.add(new MessageTemplate("notifyMaterialFeedback",
                    FileUtils.readToString(getFfmFileResource("notifyOrderFeedbackMaterial")),
                    FFM_TRIGGER_NotifyFeedback.class));
            defaultTemplates.add(new MessageTemplate("notifyTextFeedback",
                    FileUtils.readToString(getFfmFileResource("notifyOrderFeedbackText")),
                    FFM_TRIGGER_NotifyFeedback.class));
        }
        catch (IOException e) {
            LOG.warn("Failed to load default request message template", e);
        }

        return defaultTemplates;
    }

    /**
     * Gets a classpath file resource from base template package.
     *
     * @param fileName
     * @return
     */
    protected Resource getFfmFileResource(String fileName) {
        return new ClassPathResource(simulatorConfiguration.getTemplatePath() + "/ffm/" + FFMVersion.V1.getMajorVersion() + "/" + fileName + ".xml");
    }

    public void setSimulatorConfiguration(SimulatorConfiguration simulatorConfiguration) {
        this.simulatorConfiguration = simulatorConfiguration;
    }
}
