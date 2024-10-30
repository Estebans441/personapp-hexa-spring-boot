package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Mapper
public class EstudioMapperRest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Mapea desde el dominio a la respuesta para MariaDB
    public EstudioResponse fromDomainToAdapterRestMaria(Study study) {
        return fromDomainToAdapterRest(study, "MariaDB");
    }

    // Mapea desde el dominio a la respuesta para MongoDB
    public EstudioResponse fromDomainToAdapterRestMongo(Study study) {
        return fromDomainToAdapterRest(study, "MongoDB");
    }

    // Método genérico para mapear Study a EstudioResponse
    public EstudioResponse fromDomainToAdapterRest(Study study, String database) {
        if (study == null || study.getPerson() == null || study.getProfession() == null) {
            return new EstudioResponse(null, null, null, null, database, "Error: Incomplete study data");
        }

        return new EstudioResponse(
                String.valueOf(study.getPerson().getIdentification()),
                String.valueOf(study.getProfession().getIdentification()),
                study.getUniversityName(),
                study.getGraduationDate() != null ? study.getGraduationDate().format(DATE_FORMATTER) : null,
                database,
                "OK"
        );
    }

    // Convierte el request en un objeto de dominio Study
    public Study fromAdapterToDomain(EstudioRequest request, Person person, Profession profession) {
        LocalDate graduationDate;
        try {
            graduationDate = LocalDate.parse(request.getGraduationDate(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for graduationDate. Expected format: dd-MM-yyyy");
        }

        return Study.builder()
                .person(person)
                .profession(profession)
                .universityName(request.getUniversityName())
                .graduationDate(graduationDate)
                .build();
    }
}
