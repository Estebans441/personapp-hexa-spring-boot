package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

    @Autowired
    @Qualifier("profesionOutPutAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("profesionOutPutAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfesionMapperRest professionMapperRest;

    private ProfessionInputPort professionInputPort;

    private String setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
            return DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
            return DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<ProfesionResponse> historial(String database) {
        log.info("Into historial ProfessionEntity in Input Adapter");
        try {
            if (setProfessionOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                return professionInputPort.findAll().stream().map(professionMapperRest::fromDomainToAdapterRestMaria)
                        .collect(Collectors.toList());
            } else {
                return professionInputPort.findAll().stream().map(professionMapperRest::fromDomainToAdapterRestMongo)
                        .collect(Collectors.toList());
            }

        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public ProfesionResponse crearProfesion(ProfesionRequest request, String database) {
        try {
            setProfessionOutputPortInjection(database);
            Profession profession = professionInputPort.create(professionMapperRest.fromAdapterToDomain(request));
            return professionMapperRest.fromDomainToAdapterRestMaria(profession);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    public ResponseEntity<?> obtenerProfesion(String database, int id) throws NoExistException {
        try {
            setProfessionOutputPortInjection(database);
            Profession profession = professionInputPort.findOne(id);
            return ResponseEntity.ok(professionMapperRest.fromDomainToAdapterRestMaria(profession));
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> actualizarProfesion(String database, int id, ProfesionRequest request) throws NoExistException {
        try {
            setProfessionOutputPortInjection(database);
            Profession profession = professionInputPort.edit(id, professionMapperRest.fromAdapterToDomain(request));
            return ResponseEntity.ok(professionMapperRest.fromDomainToAdapterRestMaria(profession));
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response(String.valueOf(HttpStatus.NOT_FOUND),
                    "Profession with id " + id + " does not exist in the database", LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                    "Internal server error", LocalDateTime.now()));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> eliminarProfesion(String database, int id) throws InvalidOptionException, NoExistException {
        setProfessionOutputPortInjection(database);
        Optional<Profession> profession = Optional.ofNullable(professionInputPort.findOne(id));
        if (profession.isEmpty()) {
            throw new NoExistException("The profession with id " + id + " does not exist in the database, cannot be deleted");
        }
        professionInputPort.drop(id);
        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK),
                "Profession with id " + id + " deleted successfully",
                LocalDateTime.now()));
    }

    public ResponseEntity<?> contarProfesiones(String database) {
        try {
            setProfessionOutputPortInjection(database);
            return ResponseEntity.ok(professionInputPort.count());
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
