package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/estudios")
public class EstudioControllerV1 {

    @Autowired
    private EstudioInputAdapterRest estudioInputAdapterRest;

    @ResponseBody
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EstudioResponse> obtenerEstudios(@PathVariable String database) {
        log.info("Fetching all studies from database: {}", database);
        return estudioInputAdapterRest.historial(database.toUpperCase());
    }

    @ResponseBody
    @PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crearEstudio(@RequestBody EstudioRequest request, @PathVariable String database) {
        log.info("Creating a new study in database: {}", database);
        return estudioInputAdapterRest.crearEstudio(request, database.toUpperCase());
    }

    @ResponseBody
    @GetMapping(path = "/{database}/{ccPerson}/{idProf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obtenerEstudio(@PathVariable String database, @PathVariable int ccPerson, @PathVariable int idProf) {
        log.info("Fetching study with person ID: {} and profession ID: {} from database: {}", ccPerson, idProf, database);
        try {
            return estudioInputAdapterRest.obtenerEstudio(database.toUpperCase(), ccPerson, idProf);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), e.getMessage(), LocalDateTime.now()));
        }
    }

    @ResponseBody
    @PutMapping(path = "/{database}/{ccPerson}/{idProf}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarEstudio(@PathVariable String database, @PathVariable int ccPerson, @PathVariable int idProf, @RequestBody EstudioRequest request) {
        log.info("Updating study with person ID: {} and profession ID: {} in database: {}", ccPerson, idProf, database);
        try {
            return estudioInputAdapterRest.actualizarEstudio(database.toUpperCase(), ccPerson, idProf, request);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), e.getMessage(), LocalDateTime.now()));
        }
    }

    @ResponseBody
    @DeleteMapping(path = "/{database}/{ccPerson}/{idProf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> eliminarEstudio(@PathVariable String database, @PathVariable int ccPerson, @PathVariable int idProf) {
        log.info("Deleting study with person ID: {} and profession ID: {} from database: {}", ccPerson, idProf, database);
        try {
            return estudioInputAdapterRest.eliminarEstudio(database.toUpperCase(), ccPerson, idProf);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), e.getMessage(), LocalDateTime.now()));
        } catch (InvalidOptionException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> contarEstudios(@PathVariable String database) {
        log.info("Counting all studies in database: {}", database);
        return estudioInputAdapterRest.contarEstudios(database.toUpperCase());
    }
}
