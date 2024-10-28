package co.edu.javeriana.as.personapp.mongo.adapter;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter("profesionOutPutAdapterMongo")
public class ProfesionOutputAdapterMongo implements ProfessionOutputPort {
    @Autowired
    private ProfesionRepositoryMongo profesionRepositoryMongo;

    @Autowired
    private ProfesionMapperMongo profesionMapperMongo;

    @Override
    public Profession save(Profession profession) {
        log.debug("Into save on Adapter MongoDB");
        try {
            ProfesionDocument persistedProfession = profesionRepositoryMongo.save(profesionMapperMongo.fromDomainToAdapter(profession));
            return profesionMapperMongo.fromAdapterToDomain(persistedProfession);
        } catch (MongoWriteException e) {
            log.warn(e.getMessage());
            return profession;
        }
    }
    @Override
    public Boolean delete(Integer identification) {
        log.debug("Into delete on Adapter MongoDB");
        profesionRepositoryMongo.deleteById(identification);
        return profesionRepositoryMongo.findById(identification).isEmpty();
    }
    @Override
    public Profession findById(Integer identification) {
        log.debug("Into findById on Adapter MongoDB");
        if (profesionRepositoryMongo.findById(identification).isEmpty()) {
            return null;
        } else {
            return profesionMapperMongo.fromAdapterToDomain(profesionRepositoryMongo.findById(identification).get());
        }
    }
    @Override
    public List<Profession> find() {
        log.debug("Into find on Adapter MongoDB");
        return profesionRepositoryMongo.findAll().stream().map(profesionMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
}

