package co.edu.javeriana.as.personapp.mongo.adapter;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudioRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {
    @Autowired
    private EstudioRepositoryMongo estudioRepositoryMongo;
    @Autowired
    private EstudiosMapperMongo estudiosMapperMongo;

    @Autowired
    private PersonaRepositoryMongo personaRepositoryMongo;
    @Autowired
    private PersonaMapperMongo personaMapperMongo;

    @Autowired
    private ProfesionRepositoryMongo profesionRepositoryMongo;
    @Autowired
    private ProfesionMapperMongo profesionMapperMongo;

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
        Optional<PersonaDocument> persona = personaRepositoryMongo.findById(idPerson);
        Optional<ProfesionDocument> profesion = profesionRepositoryMongo.findById(identification);

        if (persona.isPresent() && profesion.isPresent()) {
            EstudiosDocument estudio = estudioRepositoryMongo.findByPrimaryPersonaAndPrimaryProfesion(persona.get(), profesion.get()).orElse(null);
            if (estudio != null) {
                estudioRepositoryMongo.delete(estudio);
                return true;
            }
        }
        return false;
    }
    @Override
    public Study findById(Integer identification, Integer idPerson) {
        log.debug("Into findById on Adapter MongoDB");
        Optional<PersonaDocument> persona = personaRepositoryMongo.findById(idPerson);
        Optional<ProfesionDocument> profesion = profesionRepositoryMongo.findById(identification);
        if (persona.isPresent() && profesion.isPresent()) {
            EstudiosDocument estudio = estudioRepositoryMongo.findByPrimaryPersonaAndPrimaryProfesion(persona.get(), profesion.get()).orElse(null);
            if (estudio != null) {
                return estudiosMapperMongo.fromAdapterToDomain(estudio);
            }
        }
        return null;
    }
    @Override
    public List<Study> find() {
        log.debug("Into find on Adapter MongoDB");
        return estudioRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomainBasic).collect(Collectors.toList());
    }
}
