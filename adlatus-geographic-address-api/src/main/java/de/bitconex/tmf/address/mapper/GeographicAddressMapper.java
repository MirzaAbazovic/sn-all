package de.bitconex.tmf.address.mapper;


import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.address.model.GeographicLocation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class GeographicAddressMapper {
}
