/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2016
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.model.cc.*;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProfileService extends ICCService {

    Map<String, List<ProfileParameter>> findProfileParametersGroupByName(@NotNull Long hvtStandortId, @NotNull HWBaugruppe baugruppe);

    List<ProfileAuftrag> findProfileAuftrags(Long auftragId);

    ProfileAuftrag findNewestProfileAuftrag(Long auftragId);

    ProfileAuftrag findProfileAuftragForDate(Long auftragId, Date datumGueltig);

    List<ProfileParameterMapper> findParameterMappers(Set<ProfileAuftragValue> profileAuftragValues);

    List<String> findLineSpectrumValues();

    String findLineSpectrumDefaultValue();

    Map<String, DSLAMProfileChangeReason> findAllChangeReasons();

    ProfileAuftrag createNewProfile(@NotNull Long auftragId, @NotNull Long sessionId);

    /**
     * Speichert ein neues Profil und passt die gültigBis-Werte der vorherigen Profile an
     *
     * @param profileAuftrag zu speicherndes Profil
     * @return entweder das gespeicherte Profil oder eine Fehlermeldung, falls das gültigVon-Datum in der Vergangenheit
     * liegt
     */
    Either<String, ProfileAuftrag> persistProfileAuftrag(ProfileAuftrag profileAuftrag);

    DSLAMProfileChangeReason getChangeReasonById(Long changeReasonId);
}
