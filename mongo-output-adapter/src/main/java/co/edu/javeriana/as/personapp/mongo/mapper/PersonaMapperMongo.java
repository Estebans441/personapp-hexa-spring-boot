package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class PersonaMapperMongo {

	@Autowired
	@Lazy
	private EstudiosMapperMongo estudiosMapperMongo;

	public PersonaDocument fromDomainToAdapter(Person person) {
		return PersonaDocument.builder()
				.id(person.getIdentification())
				.nombre(person.getFirstName())
				.apellido(person.getLastName())
				.genero(validateGenero(person.getGender()))
				.edad(person.getAge() != null ? person.getAge() : 0)
				.estudios(validateEstudios(person.getStudies()))
				.telefonos(new ArrayList<>())
				.build();
	}

	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		return Person.builder()
				.identification(personaDocument.getId() != null ? personaDocument.getId() : 0) // Maneja nulo con un valor por defecto
				.firstName(personaDocument.getNombre() != null ? personaDocument.getNombre() : "Desconocido")
				.lastName(personaDocument.getApellido() != null ? personaDocument.getApellido() : "Desconocido")
				.gender(validateGender(personaDocument.getGenero()))
				.age(personaDocument.getEdad() != null ? personaDocument.getEdad() : 0)
				.studies(validateStudies(personaDocument.getEstudios()))
				.phoneNumbers(new ArrayList<>())
				.build();
	}

	private String validateGenero(Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M" : " ";
	}

	private Gender validateGender(String genero) {
		return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private List<EstudiosDocument> validateEstudios(List<Study> studies) {
		return studies != null ? studies.stream()
				.map(study -> {
					EstudiosDocument doc = estudiosMapperMongo.fromDomainToAdapter(study);
					doc.setPrimaryPersona(null);
					return doc;
				})
				.collect(Collectors.toList()) : new ArrayList<>();
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null ? estudiosDocuments.stream()
				.map(estudiosMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList()) : new ArrayList<>();
	}

	public Person fromAdapterToDomainBasic(PersonaDocument personaDocument) {
		return Person.builder()
				.identification(personaDocument.getId() != null ? personaDocument.getId() : 0)
				.firstName(personaDocument.getNombre() != null ? personaDocument.getNombre() : "Desconocido")
				.lastName(personaDocument.getApellido() != null ? personaDocument.getApellido() : "Desconocido")
				.gender(validateGender(personaDocument.getGenero()))
				.age(personaDocument.getEdad() != null ? personaDocument.getEdad() : 0)
				.build(); // No cargar 'studies' ni 'phoneNumbers' para evitar referencias cíclicas
	}
}