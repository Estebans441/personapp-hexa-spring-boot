package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
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
@RequestMapping("/api/v1/phone")
public class TelefonoControllerV1 {

    @Autowired
    private TelefonoInputAdapterRest telefonoInputAdapterRest;

    @ResponseBody
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TelefonoResponse> obtenerTelefonos(@PathVariable String database) {
        log.info("Into obtenerTelefonos REST API");
        return telefonoInputAdapterRest.historial(database.toUpperCase());
    }

    @ResponseBody
    @PostMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crearTelefono(@RequestBody TelefonoRequest request, @PathVariable String database) {
        log.info("Into crearTelefono REST API");
        try {
            return telefonoInputAdapterRest.crearTelefono(request, database.toUpperCase());
        } catch (NoExistException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @GetMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obtenerTelefono(@PathVariable String database, @PathVariable String number) {
        log.info("Into obtenerTelefono REST API");
        try {
            return telefonoInputAdapterRest.obtenerTelefono(database.toUpperCase(), number);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                            "Phone with number " + number + " does not exist in the database", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @PutMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarTelefono(@PathVariable String database, @PathVariable String number, @RequestBody TelefonoRequest request) {
        log.info("Into actualizarTelefono REST API");
        try {
            return telefonoInputAdapterRest.actualizarTelefono(database.toUpperCase(), number, request);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                            "Phone with number " + number + " does not exist in the database", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @DeleteMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> eliminarTelefono(@PathVariable String database, @PathVariable String number) {
        log.info("Into eliminarTelefono REST API");
        try {
            return telefonoInputAdapterRest.eliminarTelefono(database.toUpperCase(), number);
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                            "Phone with number " + number + " does not exist in the database", LocalDateTime.now()));
        } catch (InvalidOptionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(String.valueOf(HttpStatus.BAD_REQUEST),
                            "Invalid database option", LocalDateTime.now()));
        }
    }

    @ResponseBody
    @GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> contarTelefonos(@PathVariable String database) {
        log.info("Into contarTelefonos REST API");
        return telefonoInputAdapterRest.contarTelefonos(database.toUpperCase());
    }
}
