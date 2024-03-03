package de.bitconex.agreement.mapper;

import de.bitconex.agreement.model.Agreement;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.model.AgreementUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgreementMapper {

    Agreement toAgreement(AgreementCreate agreementCreate);

    Agreement toAgreement(AgreementUpdate agreementUpdate);
}
