package co.edu.javeriana.as.personapp.application.usecase;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {
    private StudyOutputPort studyPersistence;
    private PersonOutputPort personPersistence;
    private ProfessionOutputPort professionPersistence;

    public StudyUseCase(@Qualifier("studyOutPutAdapterMaria") StudyOutputPort studyPersistence,
                        @Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence,
                        @Qualifier("profesionOutPutAdapterMaria") ProfessionOutputPort professionPersistence) {
        this.studyPersistence = studyPersistence;
        this.personPersistence = personPersistence;
        this.professionPersistence = professionPersistence;
    }
    @Override
    public void setPersistence(StudyOutputPort studyPersistence) {
        this.studyPersistence = studyPersistence;
    }
    @Override
    public Study create(Study study, int ccPerson, int idProfession) throws NoExistException {
        log.debug("Into create on Application Domain");
        Person person = personPersistence.findById(ccPerson);
        Profession profession = professionPersistence.findById(idProfession);
        if (person == null) {
            log.error("The person with id " + ccPerson + " does not exist into db, cannot be created");
            throw new NoExistException("The person with id " + ccPerson + " does not exist into db, cannot be created");
        }
        if (profession == null) {
            log.error("The profession with id " + idProfession + " does not exist into db, cannot be created");
            throw new NoExistException("The profession with id " + idProfession + " does not exist into db, cannot be created");
        }
        study.setPerson(person);
        study.setProfession(profession);
        return studyPersistence.save(study);
    }

    @Override
    public Study edit(Integer identification, Integer user_identificacion, Study study) {
        Study oldStudy = studyPersistence.findById(identification, user_identificacion);
        if (oldStudy != null)
            return studyPersistence.save(study);

        return null;
    }


    @Override
    public Boolean drop(Integer identification, Integer user_identificacion) {
        Study oldStudy = studyPersistence.findById(identification, user_identificacion);
        if (oldStudy != null)
            return studyPersistence.delete(identification, user_identificacion);
        return false;
    }

    @Override
    public Study findOne(Integer identification, Integer user_identificacion) {
        Study oldStudy = studyPersistence.findById(identification, user_identificacion);
        if (oldStudy != null)
            return oldStudy;
        return null;
    }

    @Override
    public Integer count() {
        return studyPersistence.find().size();
    }

    @Override
    public List<Study> findAll() {
        log.info("Output: " + studyPersistence.getClass());
        return studyPersistence.find();
    }
}
