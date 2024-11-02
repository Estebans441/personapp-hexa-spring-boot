package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
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
public class TelefonoInputAdapterRest {

    @Autowired
    @Qualifier("phoneOutPutAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutPutAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private TelefonoMapperRest telefonoMapperRest;

    @Autowired
    private PhoneInputPort phoneInputPort;

    @Autowired
    private PersonInputPort personInputPort;

    private String setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            if (phoneOutputPortMaria == null || personOutputPortMaria == null) {
                throw new InvalidOptionException("MariaDB output ports are not properly initialized.");
            }
            phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
            return DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            if (phoneOutputPortMongo == null || personOutputPortMongo == null) {
                throw new InvalidOptionException("MongoDB output ports are not properly initialized.");
            }
            phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
            return DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<TelefonoResponse> historial(String database) {
        log.info("Into historial PhoneEntity in Input Adapter");
        try {
            if (setPhoneOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                return phoneInputPort.findAll().stream().map(telefonoMapperRest::fromDomainToAdapterRestMaria)
                        .collect(Collectors.toList());
            } else {
                return phoneInputPort.findAll().stream().map(telefonoMapperRest::fromDomainToAdapterRestMongo)
                        .collect(Collectors.toList());
            }

        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public ResponseEntity<?> crearTelefono(TelefonoRequest request, String database) throws NoExistException {
        try {
            // Configurar la conexión y puertos según la base de datos especificada
            setPhoneOutputPortInjection(database);

            // Verificar si la persona existe en la base de datos especificada
            Person person;
            if (database.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                person = personOutputPortMaria.findById(Integer.valueOf(request.getOwnerId()));
                if (person == null) {
                    throw new NoExistException("The person with id " + request.getOwnerId() + " does not exist in MariaDB.");
                }
            } else if (database.equalsIgnoreCase(DatabaseOption.MONGO.toString())) { // usar DatabaseOption.MONGO
                person = personOutputPortMongo.findById(Integer.valueOf(request.getOwnerId()));
                if (person == null) {
                    throw new NoExistException("The person with id " + request.getOwnerId() + " does not exist in MongoDB.");
                }
            } else {
                throw new InvalidOptionException("Invalid database option: " + database);
            }

            // Crear el teléfono en la base de datos especificada
            Phone phone = phoneInputPort.create(telefonoMapperRest.fromAdapterToDomain(request, person), Integer.parseInt(request.getOwnerId()));

            // Retornar la respuesta de acuerdo a la base de datos
            TelefonoResponse response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                    telefonoMapperRest.fromDomainToAdapterRestMaria(phone) :
                    telefonoMapperRest.fromDomainToAdapterRestMongo(phone);

            return ResponseEntity.ok(response);

        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(HttpStatus.BAD_REQUEST.toString(), "Invalid database option", LocalDateTime.now()));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Unexpected error while creating phone: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error", LocalDateTime.now()));
        }
    }



    public ResponseEntity<?> obtenerTelefono(String database, String number) throws NoExistException {
        try {
            setPhoneOutputPortInjection(database);
            Phone phone = phoneInputPort.findOne(number);
            TelefonoResponse response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                    telefonoMapperRest.fromDomainToAdapterRestMaria(phone) :
                    telefonoMapperRest.fromDomainToAdapterRestMongo(phone);
            return ResponseEntity.ok(response);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> actualizarTelefono(String database, String number, TelefonoRequest request) throws NoExistException {
        try {
            setPhoneOutputPortInjection(database);
            Optional<Phone> phoneOptional = Optional.ofNullable(phoneInputPort.findOne(number));
            if (phoneOptional.isEmpty()) {
                throw new NoExistException("The phone with number " + number + " does not exist in the database, cannot be updated");
            }

            Optional<Person> personOptional = Optional.ofNullable(personInputPort.findOne(Integer.valueOf(request.getOwnerId())));
            if (personOptional.isEmpty()) {
                throw new NoExistException("The person with id " + request.getOwnerId() + " does not exist in the database, cannot be updated");
            }

            Person person = personOptional.get();
            Phone updatedPhone = phoneInputPort.edit(number, telefonoMapperRest.fromAdapterToDomain(request, person), Integer.parseInt(request.getOwnerId()));

            TelefonoResponse response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                    telefonoMapperRest.fromDomainToAdapterRestMaria(updatedPhone) :
                    telefonoMapperRest.fromDomainToAdapterRestMongo(updatedPhone);

            return ResponseEntity.ok(response);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        } catch (NoExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), "ID of owner not found", LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error", LocalDateTime.now()));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> eliminarTelefono(String database, String number) throws NoExistException, InvalidOptionException {
        setPhoneOutputPortInjection(database);
        Optional<Phone> phone = Optional.ofNullable(phoneInputPort.findOne(number));
        if (phone.isEmpty()) {
            throw new NoExistException("The phone with number " + number + " does not exist in the database, cannot be deleted");
        }
        phoneInputPort.drop(number);
        return ResponseEntity.ok(new Response(HttpStatus.OK.toString(),
                "Phone with number " + number + " deleted successfully", LocalDateTime.now()));
    }

    public ResponseEntity<?> contarTelefonos(String database) {
        try {
            setPhoneOutputPortInjection(database);
            return ResponseEntity.ok(phoneInputPort.count());
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
