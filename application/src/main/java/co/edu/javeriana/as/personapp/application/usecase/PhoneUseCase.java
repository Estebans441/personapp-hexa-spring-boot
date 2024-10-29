package co.edu.javeriana.as.personapp.application.usecase;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {
    private PhoneOutputPort phonePersistence;
    private PersonOutputPort personPersistence;
    public PhoneUseCase(@Qualifier("phoneOutPutAdapterMaria") PhoneOutputPort phonePersistence,
                        @Qualifier("personOutputAdapterMaria") PersonOutputPort personPersistence) {
        this.phonePersistence = phonePersistence;
        this.personPersistence = personPersistence;
    }
    @Override
    public void setPersistence(PhoneOutputPort phonePersistence, PersonOutputPort personPersistence) {
        this.phonePersistence = phonePersistence;
        this.personPersistence = personPersistence;
    }
    @Override
    public Phone create(Phone phone, int ccPerson) throws NoExistException {
        log.debug("Into create on Application Domain");
        Person person = personPersistence.findById(ccPerson);
        if (person == null) {
            log.error("The person with id " + ccPerson + " does not exist into db, cannot be created");
            throw new NoExistException("The person with id " + ccPerson + " does not exist into db, cannot be created");
        }
        phone.setOwner(person);
        return phonePersistence.save(phone);
    }
    @Override
    public Phone edit(String identification, Phone phone, int ccPerson) throws NoExistException {
        Phone oldPhone = phonePersistence.findById(identification);
        if (oldPhone != null)
            return phonePersistence.save(phone);
        throw new NoExistException(
                "The phone with id " + identification + " does not exist into db, cannot be edited");
    }
    @Override
    public Boolean drop(String identification) throws NoExistException {
        Phone oldPhone = phonePersistence.findById(identification);
        if (oldPhone != null)
            return phonePersistence.delete(identification);
        throw new NoExistException(
                "The phone with id " + identification + " does not exist into db, cannot be dropped");
    }
    @Override
    public Phone findOne(String identification) throws NoExistException {
        Phone oldPhone = phonePersistence.findById(identification);
        if (oldPhone != null)
            return oldPhone;
        throw new NoExistException(
                "The phone with id " + identification + " does not exist into db, cannot be found");
    }
    @Override
    public Integer count() {
        return phonePersistence.find().size();
    }

    @Override
    public List<Phone> findAll() {
        log.info("Output: " + phonePersistence.getClass());
        return phonePersistence.find();
    }
}


