package de.bitconex.adlatus.wholebuy.provision.adapter.party.mapper;

import de.bitconex.tmf.pm.model.ContactMedium;
import de.bitconex.tmf.pm.model.IndividualCreate;
import de.bitconex.tmf.pm.model.MediumCharacteristic;
import de.telekom.wholesale.oss.v15.complex.AnsprechpartnerType;
import de.telekom.wholesale.oss.v15.connectivity.AnsprechpartnerPersonType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface WitaContactPersonMapper {

    // TODO: map to correct salutations
    Map<String, String> salutationMap = Map.of(
            "1", "Mr",
            "2", "Mrs",
            "3", "Firma",
            "4", "Herr und Frau",
            "5", "Firma und Herr",
            "6", "Firma und Frau",
            "7", "Firma und Herr und Frau"
    );

    default IndividualCreate mapToIndividualCreate(AnsprechpartnerType ansprechpartnerPersonType) {
        if (ansprechpartnerPersonType == null) {
            return null;
        }
        IndividualCreate.IndividualCreateBuilder individualCreate = IndividualCreate.builder();
        individualCreate.givenName(ansprechpartnerPersonType.getVorname());
        individualCreate.familyName(ansprechpartnerPersonType.getNachname());
        MediumCharacteristic emailMediumCharacteristic = MediumCharacteristic.builder()
                .emailAddress(ansprechpartnerPersonType.getEmailadresse())
                .build();
        MediumCharacteristic phoneMediumCharacteristic = MediumCharacteristic.builder()
                .phoneNumber(ansprechpartnerPersonType.getTelefonnummer())
                .build();
        ContactMedium emailContactMedium = ContactMedium.builder().mediumType("email").characteristic(emailMediumCharacteristic).build();
        ContactMedium phoneContactMedium = ContactMedium.builder().mediumType("phone").characteristic(phoneMediumCharacteristic).build();

        List<ContactMedium> contactMediumList = List.of(emailContactMedium, phoneContactMedium);

        individualCreate.contactMedium(contactMediumList);
        individualCreate.title(salutationMap.get(ansprechpartnerPersonType.getAnrede()));

        return individualCreate.build();
    }
}
