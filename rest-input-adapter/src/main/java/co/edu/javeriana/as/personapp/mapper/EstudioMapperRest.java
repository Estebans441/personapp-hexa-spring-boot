package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

import java.time.LocalDate;

@Mapper
public class EstudioMapperRest {

    public EstudioResponse fromDomainToAdapterRest(Study study, String database) {
        return new EstudioResponse(
                String.valueOf(study.getPerson().getIdentification()),
                String.valueOf(study.getProfession().getIdentification()),
                study.getGraduationDate().toString(),
                study.getUniversityName(),
                database,
                "OK"
        );
    }

    public Study fromAdapterToDomain(EstudioRequest request) {
        return Study.builder()
                .person(Person.builder().identification(Integer.parseInt(request.getPersonId())).build())
                .profession(Profession.builder().identification(Integer.parseInt(request.getProfessionId())).build())
                .graduationDate(LocalDate.parse(request.getGraduationDate()))
                .universityName(request.getUniversityName())
                .build();
    }
}
