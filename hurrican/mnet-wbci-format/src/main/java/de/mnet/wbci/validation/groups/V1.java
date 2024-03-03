/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */

package de.mnet.wbci.validation.groups;

/**
 * Bean validation group which should be used for validation rules that apply only to CDM Version 1 model beans.
 * <p/>
 * This group intentionally doesn't extend the {@link javax.validation.groups.Default} group. The reason being that
 * default validation rules exist that are only relevant for when a bean is stored within the database (like the {@link
 * de.mnet.wbci.model.WbciRequest#creationDate} parameter).
 */
public interface V1 {
}
