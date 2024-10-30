package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
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
@RequestMapping("/api/v1/profession")
public class ProfessionControllerV1 {

    @Autowired
    private ProfesionInputAdapterRest professionInputAdapterRest;

    @ResponseBody
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfesionResponse> professions(@PathVariable String database) {
        log.info("Into professions REST API");
        return professionInputAdapterRest.historial(database.toUpperCase());
    }

    @ResponseBody
    @PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request, @PathVariable String database) {
        log.info("Into crearProfesion REST API");
        return professionInputAdapterRest.crearProfesion(request, database.toUpperCase());
    }

    @ResponseBody
    @GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> profession(@PathVariable String database, @PathVariable int id) {
        log.info("Into profession REST API");
        try {
            return ResponseEntity.ok(professionInputAdapterRest.obtenerProfesion(database.toUpperCase(), id));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                    "Profession with id " + id + " does not exist in the database", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @PutMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarProfesion(@PathVariable String database, @PathVariable int id, @RequestBody ProfesionRequest request) {
        log.info("Into actualizarProfesion REST API");
        try {
            return ResponseEntity.ok(professionInputAdapterRest.actualizarProfesion(database.toUpperCase(), id, request));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                    "Profession with id " + id + " does not exist in the database", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> eliminarProfesion(@PathVariable String database, @PathVariable int id) {
        log.info("Into eliminarProfesion REST API");
        try {
            return professionInputAdapterRest.eliminarProfesion(database.toUpperCase(), id);
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                    "Profession with id " + id + " does not exist in the database", LocalDateTime.now()));
        } catch (InvalidOptionException e) {
            return ResponseEntity.status(500).body(new Response(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                    "Bad Request", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> contarProfesiones(@PathVariable String database) {
        log.info("Into contarProfesiones REST API");
        return ResponseEntity.ok(professionInputAdapterRest.contarProfesiones(database.toUpperCase()));
    }
}
