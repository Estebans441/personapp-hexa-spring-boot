package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
		if (study == null) {
			return null;
		}
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private Date validateFecha(LocalDate graduationDate) {
		return graduationDate != null
				? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
				: null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		if (estudiosEntity == null || estudiosEntity.getPersona() == null || estudiosEntity.getProfesion() == null) {
			return null;
		}

		return Study.builder()
				.person(personaMapperMaria.fromAdapterToDomainBasic(estudiosEntity.getPersona()))
				.profession(profesionMapperMaria.fromAdapterToDomainBasic(estudiosEntity.getProfesion()))
				.graduationDate(validateGraduationDate(estudiosEntity.getFecha()))
				.universityName(validateUniversityName(estudiosEntity.getUniver()))
				.build();
	}

	private LocalDate validateGraduationDate(Date fecha) {
		if (fecha == null) {
			return null;
		}

		if (fecha instanceof java.sql.Date) {
			return ((java.sql.Date) fecha).toLocalDate();
		}
		return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
