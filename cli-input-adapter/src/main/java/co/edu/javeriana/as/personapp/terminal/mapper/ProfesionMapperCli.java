package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Mapper
public class ProfesionMapperCli {

    // Mapea desde el dominio al modelo CLI (solo información básica)
    public ProfesionModelCli fromDomainToBasicModelCli(Profession profession) {
        return profession != null ? ProfesionModelCli.builder()
                .id(profession.getIdentification())
                .name(profession.getName())
                .description(profession.getDescription())
                .build() : null;
    }

    // Mapea desde el modelo CLI al dominio (solo información básica)
    public Profession fromBasicModelCliToDomain(ProfesionModelCli profesionModelCli) {
        return profesionModelCli != null ? Profession.builder()
                .identification(profesionModelCli.getId())
                .name(profesionModelCli.getName())
                .description(profesionModelCli.getDescription())
                .build() : null;
    }
}