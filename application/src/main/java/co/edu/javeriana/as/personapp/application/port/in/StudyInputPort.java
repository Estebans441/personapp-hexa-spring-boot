package co.edu.javeriana.as.personapp.application.port.in;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

import java.util.List;

@Port
public interface StudyInputPort {
    public void setPersistence(StudyOutputPort studyPersistance);
    public Study create(Study study, int ccPerson, int idProfession) throws NoExistException;
    public Study edit(Integer identification, Integer user_identificacion, Study study) throws NoExistException;
    public Boolean drop(Integer identification, Integer user_identificacion) throws NoExistException;
    public Study findOne(Integer identification, Integer user_identificacion) throws NoExistException;
    public List<Study> findAll();
    public Integer count();
}



