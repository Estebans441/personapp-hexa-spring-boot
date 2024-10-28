package co.edu.javeriana.as.personapp.application.usecase;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {
    private StudyOutputPort studyPersistence;

    public StudyUseCase(@Qualifier("studyOutPutAdapterMaria") StudyOutputPort studyPersistence) {
        this.studyPersistence = studyPersistence;
    }
    @Override
    public void setPersistence(StudyOutputPort studyPersistence) {
        this.studyPersistence = studyPersistence;
    }
    @Override
    public Study create(Study study) {
        log.debug("Into create on Application Domain");
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
