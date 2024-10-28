package co.edu.javeriana.as.personapp.mongo.adapter;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudioRepositoryMongo;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {
    @Autowired
    private EstudioRepositoryMongo estudioRepositoryMongo;
    @Autowired
    private EstudiosMapperMongo estudiosMapperMongo;
    @Override
    public Study save(Study study) {
        log.debug("Into save on Adapter MongoDB");
        try{
            EstudiosDocument persistedEstudio = estudioRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
            return estudiosMapperMongo.fromAdapterToDomain(persistedEstudio);
        } catch (MongoWriteException e) {
            log.warn(e.getMessage());
            return study;
        }
    }
    @Override
    public Boolean delete (Integer identification, Integer idPerson) {
        log.debug("Into delete on Adapter MongoDB");
        EstudiosDocument studyToDelete = estudioRepositoryMongo.findByCcPerAndIdProf(idPerson, identification);
        if (studyToDelete != null) {
            estudioRepositoryMongo.delete(studyToDelete);
            return true;
        }
        return false;
    }
    @Override
    public Study findById(Integer identification, Integer idPerson) {
        log.debug("Into findById on Adapter MongoDB");
        EstudiosDocument study = estudioRepositoryMongo.findByCcPerAndIdProf(idPerson, identification);
        if (study != null)
            return estudiosMapperMongo.fromAdapterToDomain(study);
        return null;
    }
    @Override
    public List<Study> find() {
        log.debug("Into find on Adapter MongoDB");
        return estudioRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain).collect(Collectors.toList());
    }
}
