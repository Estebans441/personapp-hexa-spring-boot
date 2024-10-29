package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Adapter
public class EstudioInputAdapterCli  {
    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private PersonaMapperCli personaMapperCli;

    private PersonInputPort personInputPort;

    @Autowired
    @Qualifier("profesionOutPutAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("profesionOutPutAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfesionMapperCli profesionMapperCli;

    private ProfessionInputPort professionInputPort;


    @Autowired
    @Qualifier("studyOutPutAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;

    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;

    @Autowired
    private EstudioMapperCli estudioMapperCli;

    private StudyInputPort studyInputPort;

    public void setStudyOutputPortInjection (String dbOption){
        if (dbOption.equalsIgnoreCase("MARIA")) {
            this.studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
            this.personInputPort = new PersonUseCase(personOutputPortMaria);
            this.professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase("MONGO")) {
            this.studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
            this.personInputPort = new PersonUseCase(personOutputPortMongo);
            this.professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
        } else {
            throw new IllegalArgumentException("Invalid database option: " + dbOption);
        }
    }

    public void historial(){
        log.info("Into historial EstudioEntity in Input Adapter");
        studyInputPort.findAll().stream()
                .map(estudioMapperCli::fromDomainToCli)
                .forEach(System.out::println);
    }

    public void create (int ccPerson, int idProf, String college, LocalDate date) throws NoExistException{
        PersonaModelCli personModel = personaMapperCli.fromDomainToBasicModelCli(personInputPort.findOne(ccPerson));
        ProfesionModelCli professionModel = profesionMapperCli.fromDomainToBasicModelCli(professionInputPort.findOne(idProf));

        if (personModel == null) {
            throw new NoExistException("The person with id " + ccPerson + " does not exist in the database, cannot create study.");
        }

        if (professionModel == null) {
            throw new NoExistException("The profession with id " + idProf + " does not exist in the database, cannot create study.");
        }

        // Create cli with the person and profession
        EstudioModelCli estudioModelCli = EstudioModelCli.builder()
                .person(personaMapperCli.fromBasicModelCliToDomain(personModel))
                .profession(profesionMapperCli.fromBasicModelCliToDomain(professionModel))
                .graduationDate(date)
                .universityName(college)
                .build();
        studyInputPort.create(estudioMapperCli.fromCliToDomain(estudioModelCli), ccPerson, idProf);
    }

    public void drop (int id, int ccPerson) throws NoExistException{
        Optional.ofNullable(studyInputPort.findOne(id, ccPerson))
                .orElseThrow(() -> new NoExistException("The study with id " + id + " does not exist in the database, cannot drop study."));
        studyInputPort.drop(id, ccPerson);
        System.out.println("Estudio eliminado con éxito.");
    }

    public void findOne (int id, int ccPerson) throws NoExistException{
        Optional.ofNullable(studyInputPort.findOne(id, ccPerson))
                .orElseThrow(() -> new NoExistException("The study with id " + id + " does not exist in the database, cannot find study."));
        System.out.println(studyInputPort.findOne(id, ccPerson));
    }

    public void count (){
        System.out.println(studyInputPort.count());
    }

    public void edit(int ccPerson, int idProf, String college, LocalDate date) throws NoExistException {
        PersonaModelCli personModel = personaMapperCli.fromDomainToBasicModelCli(personInputPort.findOne(ccPerson));
        ProfesionModelCli professionModel = profesionMapperCli.fromDomainToBasicModelCli(professionInputPort.findOne(idProf));

        if (personModel == null) {
            throw new NoExistException("The person with id " + ccPerson + " does not exist in the database, cannot edit study.");
        }

        if (professionModel == null) {
            throw new NoExistException("The profession with id " + idProf + " does not exist in the database, cannot edit study.");
        }

        EstudioModelCli estudioModelCli = EstudioModelCli.builder()
                .person(personaMapperCli.fromBasicModelCliToDomain(personModel))
                .profession(profesionMapperCli.fromBasicModelCliToDomain(professionModel))
                .graduationDate(date)
                .universityName(college)
                .build();

        Study updatedStudy = studyInputPort.edit(idProf, ccPerson, estudioMapperCli.fromCliToDomain(estudioModelCli));
        if (updatedStudy == null) {
            throw new NoExistException("Failed to update the study. No existing study found with person ID " + ccPerson + " and profession ID " + idProf);
        }

        System.out.println("Estudio actualizado con éxito.");
    }

}
