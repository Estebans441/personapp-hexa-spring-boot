package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper
public class ProfesionMapperRest {

    public ProfesionResponse fromDomainToAdapterRest(Profession profession, String database) {
        return new ProfesionResponse(
                String.valueOf(profession.getIdentification()),
                profession.getName(),
                profession.getDescription(),
                database,
                "OK"
        );
    }

    public Profession fromAdapterToDomain(ProfesionRequest request) {
        return Profession.builder()
                .id(Integer.parseInt(request.getId()))
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}