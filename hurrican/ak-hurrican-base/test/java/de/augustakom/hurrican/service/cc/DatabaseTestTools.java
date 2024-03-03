/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2011 13:00:39
 */
package de.augustakom.hurrican.service.cc;

public interface DatabaseTestTools extends ICCService {

    void executeHqlAndAutoCommit(String hql);

    void delete(final Object object);

    void save(final Object object);
}
