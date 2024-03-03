/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.13
 */
package de.mnet.wbci.model;

import java.io.*;
import java.util.*;

/**
 * Main Interface for all incoming and outgoing WBCI messages.
 */
public interface WbciMessage<E extends Enum> extends Serializable {

    static final String SEND_AFTER = "sendAfter";

    /**
     * Returns the EKP Partner. This is the recipient of the message for outbound messages or the sender of the message
     * for inbound messages. M-Net should never be returned here.
     * <p/>
     * This is helpful when evaluating the target cdm version for this EKP from wbci configuration.
     *
     * @return the {@link CarrierCode} of the partner EKP
     */
    CarrierCode getEKPPartner();

    /**
     * Gets the vorabstimmungs id associated with this wbci message.
     *
     * @return the VA-ID
     */
    String getVorabstimmungsId();

    /**
     * Gets the geschaeftsfall typ associated with this wbci message.
     *
     * @return the {@link GeschaeftsfallTyp} of the message.
     */
    WbciGeschaeftsfall getWbciGeschaeftsfall();

    /**
     * Gets the processed date for any outgoing and incoming wbci message.
     *
     * @return
     */
    Date getProcessedAt();

    /**
     * Sets the processed date for any outgoing and incoming wbci messages.
     *
     * @param dateTime sent date
     */
    void setProcessedAt(Date dateTime);

    /**
     * @return the {@link Date} after which the message could be sent. In case it is null, the request will be sent
     * immediately.
     */
    Date getSendAfter();

    /**
     * Sets the sendAfter date for any outgoing message depending on the DB configuration
     */
    void setSendAfter(Date sendAfter);

    /**
     * Gets {@code IOType.IN} for incoming messages and {@code IOType.OUT} for outgoing messages (from the view of
     * M-Net).
     *
     * @return
     */
    IOType getIoType();

    /**
     * Set {@code IOType.IN} for incoming messages and {@code IOType.OUT} for outgoing messages (from the view of
     * M-Net).
     *
     * @param ioType
     */
    void setIoType(IOType ioType);


    /**
     * Gets the request type for a WBCI Message like: <ul> <li>VorabstimmungsAnfrage => VA</li>
     * <li>RückmeldungVorabstimmung => RUEM-VA</li> </ul>
     *
     * @return String value of the Type
     */
    E getTyp();
}
