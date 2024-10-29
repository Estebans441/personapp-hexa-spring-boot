package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class ProfesionMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	public ProfesionEntity fromDomainToAdapter(Profession profession) {
		if (profession == null) return null;

		ProfesionEntity profesionEntity = new ProfesionEntity();
		profesionEntity.setId(profession.getIdentification());
		profesionEntity.setNom(profession.getName());
		profesionEntity.setDes(validateDescription(profession.getDescription()));
		profesionEntity.setEstudios(validateEstudios(profession.getStudies())); // Solo en mapeo completo
		return profesionEntity;
	}

	private String validateDescription(String description) {
		return description != null ? description : "";
	}

	private List<EstudiosEntity> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty()
				? studies.stream()
				.map(estudiosMapperMaria::fromDomainToAdapter) // Mapeo básico de estudios
				.collect(Collectors.toList())
				: new ArrayList<>();
	}

	public Profession fromAdapterToDomain(ProfesionEntity profesionEntity) {
		if (profesionEntity == null) return null;

		return Profession.builder()
				.identification(profesionEntity.getId())
				.name(profesionEntity.getNom())
				.description(validateDescription(profesionEntity.getDes()))
				.studies(validateStudies(profesionEntity.getEstudios())) // Solo en mapeo completo
				.build();
	}

	public Profession fromAdapterToDomainBasic(ProfesionEntity profesionEntity) {
		if (profesionEntity == null) return null;

		return Profession.builder()
				.identification(profesionEntity.getId())
				.name(profesionEntity.getNom())
				.description(validateDescription(profesionEntity.getDes()))
				.build(); // Sin estudios para evitar ciclos
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty()
				? estudiosEntity.stream()
				.map(estudiosMapperMaria::fromAdapterToDomain) // Mapeo básico de estudios
				.collect(Collectors.toList())
				: new ArrayList<>();
	}
}





