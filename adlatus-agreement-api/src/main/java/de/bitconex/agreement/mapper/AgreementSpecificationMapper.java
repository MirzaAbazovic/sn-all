package de.bitconex.agreement.mapper;

import de.bitconex.agreement.model.AgreementSpecification;
import de.bitconex.agreement.model.AgreementSpecificationCreate;
import de.bitconex.agreement.model.AgreementSpecificationUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgreementSpecificationMapper {

    AgreementSpecification toAgreementSpecification(AgreementSpecificationCreate agreementSpecificationCreate);

    AgreementSpecification toAgreementSpecification(AgreementSpecificationUpdate agreementSpecificationUpdate);
}
