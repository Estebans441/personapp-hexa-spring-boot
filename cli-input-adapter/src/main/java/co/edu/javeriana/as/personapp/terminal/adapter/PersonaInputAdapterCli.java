package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {
	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;
	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;
	PersonInputPort personInputPort;
	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}
	public void historial() {
	    log.info("Into historial PersonaEntity in Input Adapter");
	    personInputPort.findAll().stream()
	        .map(personaMapperCli::fromDomainToBasicModelCli)
	        .forEach(System.out::println);
	}

	public void create (int cc, String nombre, String apellido, String genero, Integer edad){
		PersonaModelCli persona = PersonaModelCli.builder()
				.cc(cc)
				.nombre(nombre)
				.apellido(apellido)
				.genero(genero)
				.edad(edad)
				.build();
		personInputPort.create(personaMapperCli.fromBasicModelCliToDomain(persona));
	}
	public void edit (int cc, String nombre, String apellido, String genero, Integer edad) throws NoExistException {
		PersonaModelCli persona = PersonaModelCli.builder()
				.cc(cc)
				.nombre(nombre)
				.apellido(apellido)
				.genero(genero)
				.edad(edad)
				.build();
		personInputPort.edit(cc, personaMapperCli.fromBasicModelCliToDomain(persona));
	}
	public void drop (int cc) throws NoExistException {
		personInputPort.drop(cc);
	}
	public void findOne (int cc) throws NoExistException {
		PersonaModelCli persona = personaMapperCli.fromDomainToBasicModelCli(personInputPort.findOne(cc));
		if (persona != null) {
			System.out.println(persona.toString());
		}
	}
	public void count () {
		System.out.println(personInputPort.count());
	}

}

