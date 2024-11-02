package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@PathVariable String database) {
		log.info("Into personas REST API");
			return personaInputAdapterRest.historial(database.toUpperCase());
	}
	
	@ResponseBody
	@PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request, @PathVariable String database) {
		log.info("esta en el metodo crearTarea en el controller del api");
		return personaInputAdapterRest.crearPersona(request, database);
	}

	@ResponseBody
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> persona(@PathVariable String database, @PathVariable int cc) {
		log.info("Into persona REST API");
		try {
			return ResponseEntity.ok(personaInputAdapterRest.obtenerPersona(database.toUpperCase(), cc));
		}
		catch (NoExistException e) {
			return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
					"Persona con cc " + cc + " no existe en la base de datos ", LocalDateTime.now()));
		}
	}

	@ResponseBody
	@PutMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> actualizarPersona(@PathVariable String database, @PathVariable int cc, @RequestBody PersonaRequest request) {
		log.info("Into actualizarPersona REST API");
		try {
			return ResponseEntity.ok(personaInputAdapterRest.actualizarPersona(database.toUpperCase(), cc, request));
		}
		catch (NoExistException e) {
			return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
					"Persona con cc " + cc + " no existe en la base de datos ", LocalDateTime.now()));
		}
	}

	@ResponseBody
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> eliminarPersona(@PathVariable String database, @PathVariable int cc)  {
		log.info("Into eliminarPersona REST API");
		try {
			return personaInputAdapterRest.eliminarPersona(database.toUpperCase(), cc);
		}
		catch (NoExistException e) {
			return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
					"Persona con cc " + cc + " no existe en la base de datos ", LocalDateTime.now()));
		}
		catch (InvalidOptionException e) {
			return ResponseEntity.status(500).body(new Response(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
					"Bad Request", LocalDateTime.now()));
		}
	}

	@ResponseBody
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> contarPersonas(@PathVariable String database) {
		log.info("Into contarPersonas REST API");
		return ResponseEntity.ok(personaInputAdapterRest.contarPersonas(database.toUpperCase()));
	}
}
