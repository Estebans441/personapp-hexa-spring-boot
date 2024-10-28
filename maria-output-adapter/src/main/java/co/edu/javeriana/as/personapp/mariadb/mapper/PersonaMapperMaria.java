package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;

@Mapper
public class PersonaMapperMaria {

    @Autowired
    private EstudiosMapperMaria estudiosMapperMaria;

    @Autowired
    private TelefonoMapperMaria telefonoMapperMaria;

    public PersonaEntity fromDomainToAdapter(Person person) {
        if (person == null) return null;

        PersonaEntity personaEntity = new PersonaEntity();
        personaEntity.setCc(person.getIdentification());
        personaEntity.setNombre(person.getFirstName());
        personaEntity.setApellido(person.getLastName());
        personaEntity.setGenero(validateGenero(person.getGender()));
        personaEntity.setEdad(validateEdad(person.getAge()));
        personaEntity.setEstudios(validateEstudios(person.getStudies()));
        personaEntity.setTelefonos(mapPhonesWithoutOwnerValidation(person.getPhoneNumbers()));
        return personaEntity;
    }

    private Character validateGenero(@NonNull Gender gender) {
        return gender == Gender.FEMALE ? 'F' : gender == Gender.MALE ? 'M' : ' ';
    }

    private Integer validateEdad(Integer age) {
        return age != null && age >= 0 ? age : null;
    }

    private List<EstudiosEntity> validateEstudios(List<Study> studies) {
        return studies != null && !studies.isEmpty()
                ? studies.stream().map(estudiosMapperMaria::fromDomainToAdapter).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private List<TelefonoEntity> mapPhonesWithoutOwnerValidation(List<Phone> phoneNumbers) {
        return phoneNumbers != null && !phoneNumbers.isEmpty()
                ? phoneNumbers.stream()
                .map(telefonoMapperMaria::fromDomainToAdapterWithoutOwner)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    public Person fromAdapterToDomain(PersonaEntity personaEntity) {
        if (personaEntity == null) return null;

        return Person.builder()
                .identification(personaEntity.getCc())
                .firstName(personaEntity.getNombre())
                .lastName(personaEntity.getApellido())
                .gender(validateGender(personaEntity.getGenero()))
                .age(validateAge(personaEntity.getEdad()))
                .studies(validateStudies(personaEntity.getEstudios()))
                .phoneNumbers(mapPhonesWithoutOwner(personaEntity.getTelefonos()))
                .build();
    }

    public Person fromAdapterToDomainBasic(PersonaEntity personaEntity) {
        if (personaEntity == null) return null;

        return Person.builder()
                .identification(personaEntity.getCc())
                .firstName(personaEntity.getNombre())
                .lastName(personaEntity.getApellido())
                .gender(Gender.OTHER) // Valor predeterminado para evitar NullPointerException
                .build();
    }

    private @NonNull Gender validateGender(Character genero) {
        return genero == 'F' ? Gender.FEMALE : genero == 'M' ? Gender.MALE : Gender.OTHER;
    }

    private Integer validateAge(Integer edad) {
        return edad != null && edad >= 0 ? edad : null;
    }

    private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
        return estudiosEntity != null && !estudiosEntity.isEmpty()
                ? estudiosEntity.stream()
                .map(estudiosMapperMaria::fromAdapterToDomain) // Usa mapeo básico de estudios
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private List<Phone> mapPhonesWithoutOwner(List<TelefonoEntity> telefonoEntities) {
        return telefonoEntities != null && !telefonoEntities.isEmpty()
                ? telefonoEntities.stream()
                .map(telefonoMapperMaria::fromAdapterToDomainWithoutOwner)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
