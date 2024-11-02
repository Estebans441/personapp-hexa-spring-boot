package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {
    @Autowired
    @Qualifier("studyOutPutAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;
    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;
    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;
    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;
    @Autowired
    @Qualifier("profesionOutPutAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;
    @Autowired
    @Qualifier("profesionOutPutAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;
    @Autowired
    private EstudioMapperRest estudioMapperRest;
    private StudyInputPort studyInputPort;
    private PersonInputPort personInputPort;
    private ProfessionInputPort professionInputPort;
    private String setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
            return DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
            return DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }


    public List<EstudioResponse> historial(String database) {
        log.info("Into historial StudyEntity in Input Adapter");
        try {
            if (setStudyOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                return studyInputPort.findAll().stream()
                        .map(estudioMapperRest::fromDomainToAdapterRestMaria)
                        .collect(Collectors.toList());
            } else {
                return studyInputPort.findAll().stream()
                        .map(estudioMapperRest::fromDomainToAdapterRestMongo)
                        .collect(Collectors.toList());
            }
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    public ResponseEntity<?> crearEstudio(EstudioRequest request, String database) {
        try {
            if (request.getPersonId() == null || request.getProfessionId() == null ||
                    request.getGraduationDate() == null || request.getUniversityName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(HttpStatus.BAD_REQUEST.toString(),
                                "Study data is incomplete or null", LocalDateTime.now()));
            }
            setStudyOutputPortInjection(database);

            Person person = personInputPort.findOne(Integer.parseInt(request.getPersonId()));
            if (person == null) {
                throw new NoExistException("The person with id " + request.getPersonId() + " does not exist in the database, cannot create study.");
            }

            Profession profession = professionInputPort.findOne(Integer.parseInt(request.getProfessionId()));
            if (profession == null) {
                throw new NoExistException("The profession with id " + request.getProfessionId() + " does not exist in the database, cannot create study.");
            }

            Study existingStudy = studyInputPort.findOne(Integer.parseInt(request.getPersonId()), Integer.parseInt(request.getProfessionId()));
            if (existingStudy != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response(HttpStatus.CONFLICT.toString(),
                                "Person already has a study with the same profession", LocalDateTime.now()));
            }

            Study study = Study.builder()
                    .person(person)
                    .profession(profession)
                    .graduationDate(LocalDate.parse(request.getGraduationDate()))
                    .universityName(request.getUniversityName())
                    .build();
            studyInputPort.create(study, Integer.parseInt(request.getPersonId()), Integer.parseInt(request.getProfessionId()));

            EstudioResponse response;
            try {
                response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                        estudioMapperRest.fromDomainToAdapterRestMaria(study) :
                        estudioMapperRest.fromDomainToAdapterRestMongo(study);
            } catch (NullPointerException ex) {
                log.warn("NullPointerException during response mapping: ", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error mapping study response", LocalDateTime.now()));
            }
            return ResponseEntity.ok(response);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(HttpStatus.BAD_REQUEST.toString(), "Invalid database option", LocalDateTime.now()));
        } catch (NoExistException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(HttpStatus.NOT_FOUND.toString(), e.getMessage(), LocalDateTime.now()));
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format: " + e.getParsedString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(HttpStatus.BAD_REQUEST.toString(), "Invalid date format", LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Unexpected error while creating study: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> obtenerEstudio(String database, int ccPerson, int idProf) throws NoExistException {
        try {
            setStudyOutputPortInjection(database);
            Study study = studyInputPort.findOne(idProf, ccPerson);
            EstudioResponse response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                    estudioMapperRest.fromDomainToAdapterRestMaria(study) :
                    estudioMapperRest.fromDomainToAdapterRestMongo(study);
            return ResponseEntity.ok(response);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> actualizarEstudio(String database, int ccPerson, int idProf, EstudioRequest request) throws NoExistException {
        try {
            setStudyOutputPortInjection(database);

            Optional<Person> personOptional = Optional.ofNullable(personInputPort.findOne(Integer.valueOf(request.getPersonId())));
            Optional<Profession> professionOptional = Optional.ofNullable(professionInputPort.findOne(Integer.valueOf(request.getProfessionId())));

            if (personOptional.isEmpty() || professionOptional.isEmpty()) {
                throw new NoExistException("Person or Profession not found, cannot update study.");
            }
            Study updatedStudy = Study.builder()
                    .person(personOptional.get())
                    .profession(professionOptional.get())
                    .graduationDate(LocalDate.parse(request.getGraduationDate()))
                    .universityName(request.getUniversityName())
                    .build();
            studyInputPort.edit(idProf, ccPerson, updatedStudy);

            EstudioResponse response = database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ?
                    estudioMapperRest.fromDomainToAdapterRestMaria(updatedStudy) :
                    estudioMapperRest.fromDomainToAdapterRestMongo(updatedStudy);

            return ResponseEntity.ok(response);
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error", LocalDateTime.now()));
        }
    }
    public ResponseEntity<?> eliminarEstudio(String database, int ccPerson, int idProf) throws NoExistException, InvalidOptionException {
        setStudyOutputPortInjection(database);
        Optional<Study> study = Optional.ofNullable(studyInputPort.findOne(idProf, ccPerson));
        if (study.isEmpty()) {
            throw new NoExistException("The study with id " + idProf + " and person ID " + ccPerson + " does not exist, cannot be deleted");
        }
        studyInputPort.drop(idProf, ccPerson);
        return ResponseEntity.ok(new Response(HttpStatus.OK.toString(),
                "Study deleted successfully", LocalDateTime.now()));
    }
    public ResponseEntity<?> contarEstudios(String database) {
        try {
            setStudyOutputPortInjection(database);
            return ResponseEntity.ok(studyInputPort.count());
        } catch (InvalidOptionException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}