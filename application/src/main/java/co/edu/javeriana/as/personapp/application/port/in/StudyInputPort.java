package co.edu.javeriana.as.personapp.application.port.in;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Study;

import java.util.List;

@Port
public interface StudyInputPort {
    public void setPersistence(StudyOutputPort studyPersistance);
    public Study create(Study study);
    public Study edit(Integer identification, Integer user_identificacion, Study study);
    public Boolean drop(Integer identification, Integer user_identificacion);
    public Study findOne(Integer identification, Integer user_identificacion);
    public List<Study> findAll();
    public Integer count();
}



