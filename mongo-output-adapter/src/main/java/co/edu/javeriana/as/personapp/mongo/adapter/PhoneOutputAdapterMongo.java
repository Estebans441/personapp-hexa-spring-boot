package co.edu.javeriana.as.personapp.mongo.adapter;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter("phoneOutPutAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

    @Autowired
    private TelefonoRepositoryMongo telefonoRepositoryMongo;

    @Autowired
    private TelefonoMapperMongo telefonoMapperMongo;

    @Override
    public Phone save(Phone phone) {
        log.debug("Into save on Adapter MongoDB");
        try {
            TelefonoDocument telefonoDoc = telefonoMapperMongo.fromDomainToAdapter(phone);
            TelefonoDocument persistedPhone = telefonoRepositoryMongo.save(telefonoDoc);
            return telefonoMapperMongo.fromAdapterToDomain(persistedPhone);
        } catch (MongoWriteException e) {
            log.warn("Error saving document: {}", e.getMessage());
            return phone;
        }
    }

    @Override
    public Boolean delete(String identification) {
        log.debug("Into delete on Adapter MongoDB");
        telefonoRepositoryMongo.deleteById(identification);
        return telefonoRepositoryMongo.findById(identification).isEmpty();
    }

    @Override
    public Phone findById(String identification) {
        log.debug("Into findById on Adapter MongoDB");
        return telefonoRepositoryMongo.findById(identification)
                .map(telefonoMapperMongo::fromAdapterToDomain)
                .orElse(null);
    }

    @Override
    public List<Phone> find() {
        log.debug("Into find on Adapter MongoDB");
        return telefonoRepositoryMongo.findAll().stream()
                .map(telefonoMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
}
