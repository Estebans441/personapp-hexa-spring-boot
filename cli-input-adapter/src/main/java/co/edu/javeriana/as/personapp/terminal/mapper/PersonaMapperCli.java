package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

@Mapper
public class PersonaMapperCli {

	public PersonaModelCli fromDomainToBasicModelCli(Person person) {
		return person != null ? PersonaModelCli.builder()
				.cc(person.getIdentification())
				.nombre(person.getFirstName())
				.apellido(person.getLastName())
				.genero(String.valueOf(parseGender(String.valueOf(person.getGender()))))
				.edad(person.getAge())
				.build() : null;
	}

	public Person fromBasicModelCliToDomain(PersonaModelCli personaModelCli) {
		return personaModelCli != null ? Person.builder()
				.identification(personaModelCli.getCc())
				.firstName(personaModelCli.getNombre())
				.lastName(personaModelCli.getApellido())
				.gender(personaModelCli.getGenero() != null ? parseGender(personaModelCli.getGenero()) : Gender.OTHER)
				.age(personaModelCli.getEdad())
				.build() : null;
	}
	private Gender parseGender(String genero) {
		switch (genero.toUpperCase()) {
			case "M":
				return Gender.MALE;
			case "F":
				return Gender.FEMALE;
			default:
				return Gender.OTHER;
		}
	}
}


