/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2009 11:22:34
 */
package de.augustakom.authentication.model;

import java.text.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;


/**
 * Creates a user session, defaulting to the hurrican application, which is Id '1' in the database.
 *
 *
 */
@SuppressWarnings("unused")
public class AKUserSessionBuilder extends EntityBuilder<AKUserSessionBuilder, AKUserSession> {

    private Long sessionId = null;
    private AKUser user = null;
    private Date loginTime = null;
    private Date deprecationTime = null;
    private String hostUser = "testUser";
    private String hostName = "testHost";
    private String ipAddress = "10.0.0.1";
    private Long applicationId = AKApplicationBuilder.APPLICATION_ID_HURRICAN;
    private String applicationVersion = DateFormat.getDateInstance().format(new Date()) + "-test";


    public AKUserSessionBuilder() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        loginTime = cal.getTime();
        cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        deprecationTime = cal.getTime();
    }


    @Override
    protected void beforeBuild() {
        getUser();
    }

    public AKUser getUser() {
        if (user == null) {
            user = getBuilder(AKUserBuilder.class).build();
        }
        return user;
    }
}
