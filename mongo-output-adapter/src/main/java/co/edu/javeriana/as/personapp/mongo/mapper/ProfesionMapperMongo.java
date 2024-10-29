package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

@Mapper
public class ProfesionMapperMongo {
	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;
	public ProfesionDocument fromDomainToAdapter(Profession profession) {
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(validateDes(profession.getDescription()));
		profesionDocument.setEstudios(validateEstudios(profession.getStudies()));
		return profesionDocument;
	}
	private String validateDes(String description) {
		return description != null ? description : "";
	}

	private List<EstudiosDocument> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty() ? studies.stream()
				.map(study -> estudiosMapperMongo.fromDomainToAdapter(study)).collect(Collectors.toList())
				: new ArrayList<EstudiosDocument>();
	}

	public Profession fromAdapterToDomain(ProfesionDocument profesionDocument) {
		Profession profession = new Profession();
		profession.setIdentification(profesionDocument.getId());
		profession.setName(profesionDocument.getNom());
		profession.setDescription(validateDescription(profesionDocument.getDes()));
		profession.setStudies(validateStudies(profesionDocument.getEstudios()));
		return profession;
	}

	private String validateDescription(String des) {
		return des != null ? des : "";
	}

	public Profession fromAdapterToDomainBasic(ProfesionDocument profesionDocument) {
		return Profession.builder()
				.identification(profesionDocument.getId() != null ? profesionDocument.getId() : 0)
				.name(profesionDocument.getNom() != null ? profesionDocument.getNom() : "Desconocido")
				.description(profesionDocument.getDes() != null ? profesionDocument.getDes() : "")
				.build(); // No cargar 'studies' para evitar referencias cíclicas
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocument) {
		return estudiosDocument != null && !estudiosDocument.isEmpty() ? estudiosDocument.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomainBasic(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}
