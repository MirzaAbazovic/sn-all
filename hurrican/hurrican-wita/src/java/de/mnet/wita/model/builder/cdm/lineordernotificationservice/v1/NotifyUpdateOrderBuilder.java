/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERGMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypMTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypVZMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.NotifyUpdateOrder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class NotifyUpdateOrderBuilder implements LineOrderNotificationTypeBuilder<NotifyUpdateOrder> {

    private AuftragstypType order;
    private NotifyUpdateOrder.Message message;

    @Override
    public NotifyUpdateOrder build() {
        NotifyUpdateOrder notifyUpdateOrder = new NotifyUpdateOrder();
        notifyUpdateOrder.setMessage(message);
        notifyUpdateOrder.setOrder(order);
        return notifyUpdateOrder;
    }

    public NotifyUpdateOrderBuilder withOrder(AuftragstypType order) {
        this.order = order;
        return this;
    }

    public NotifyUpdateOrderBuilder withAbbm(MeldungstypABBMType abbm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setABBM(abbm);
        return this;
    }

    public NotifyUpdateOrderBuilder withAbbmpv(MeldungstypABBMPVType abbmpv) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setABBMPV(abbmpv);
        return this;
    }

    public NotifyUpdateOrderBuilder withAbm(MeldungstypABMType abm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setABM(abm);
        return this;
    }

    public NotifyUpdateOrderBuilder withAbmpv(MeldungstypABMPVType abmpv) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setABMPV(abmpv);
        return this;
    }

    public NotifyUpdateOrderBuilder withAkmpv(MeldungstypAKMPVType akmpv) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setAKMPV(akmpv);
        return this;
    }

    public NotifyUpdateOrderBuilder withEntm(MeldungstypENTMType entm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setENTM(entm);
        return this;
    }

    public NotifyUpdateOrderBuilder withEntmpv(MeldungstypENTMPVType entmpv) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setENTMPV(entmpv);
        return this;
    }

    public NotifyUpdateOrderBuilder withErgm(MeldungstypERGMType ergm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setERGM(ergm);
        return this;
    }

    public NotifyUpdateOrderBuilder withErlm(MeldungstypERLMType erlm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setERLM(erlm);
        return this;
    }

    public NotifyUpdateOrderBuilder withErlmpv(MeldungstypERLMPVType erlmpv) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setERLMPV(erlmpv);
        return this;
    }

    public NotifyUpdateOrderBuilder withMtam(MeldungstypMTAMType mtam) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setMTAM(mtam);
        return this;
    }

    public NotifyUpdateOrderBuilder withQeb(MeldungstypQEBType qeb) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setQEB(qeb);
        return this;
    }

    public NotifyUpdateOrderBuilder withTam(MeldungstypTAMType tam) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setTAM(tam);
        return this;
    }

    public NotifyUpdateOrderBuilder withVzm(MeldungstypVZMType vzm) {
        this.message = new NotifyUpdateOrder.Message();
        this.message.setVZM(vzm);
        return this;
    }

}
