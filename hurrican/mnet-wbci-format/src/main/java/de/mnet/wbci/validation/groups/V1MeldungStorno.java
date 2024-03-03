/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */

package de.mnet.wbci.validation.groups;

/**
 * Extension of the {@link V1} bean validation group which should be used for validation rules that apply only to CDM
 * Version 1 {@link de.mnet.wbci.model.Meldung} beans which will be used for answering an {@link
 * de.mnet.wbci.model.StornoAnfrage}.
 */
public interface V1MeldungStorno extends V1Meldung {
}
