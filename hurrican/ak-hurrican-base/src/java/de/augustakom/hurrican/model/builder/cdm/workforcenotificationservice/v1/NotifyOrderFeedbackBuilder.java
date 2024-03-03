/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.v1;

import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.workforcenotificationservice.WorkforceNotificationTypeBuilder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;

/**
 *
 */
public class NotifyOrderFeedbackBuilder implements WorkforceNotificationTypeBuilder<NotifyOrderFeedback> {

    private String orderId;
    private String workUnit;
    private LocalDateTime captured;
    private NotifyOrderFeedback.Material material;
    private String text;

    @Override
    public NotifyOrderFeedback build() {
        NotifyOrderFeedback feedback = new NotifyOrderFeedback();
        feedback.setOrderId(this.orderId);
        feedback.setWorkUnit(this.workUnit);
        feedback.setCaptured(this.captured);
        feedback.setMaterial(this.material);
        feedback.setText(this.text);
        return feedback;
    }

    public NotifyOrderFeedbackBuilder withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public NotifyOrderFeedbackBuilder withWorkUnit(String workUnit) {
        this.workUnit = workUnit;
        return this;
    }

    public NotifyOrderFeedbackBuilder withCaptured(LocalDateTime captured) {
        this.captured = captured;
        return this;
    }

    public NotifyOrderFeedbackBuilder withMaterial(NotifyOrderFeedback.Material material) {
        this.material = material;
        return this;
    }

    public NotifyOrderFeedbackBuilder withText(String text) {
        this.text = text;
        return this;
    }
}