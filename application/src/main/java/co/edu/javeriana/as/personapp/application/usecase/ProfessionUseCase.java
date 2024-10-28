package co.edu.javeriana.as.personapp.application.usecase;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import java.util.List;

@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {
    private ProfessionOutputPort profesionPersistence;

    public ProfessionUseCase(@Qualifier("profesionOutPutAdapterMaria") ProfessionOutputPort profesionPersistence) {
        this.profesionPersistence = profesionPersistence;
    }
    @Override
    public void setPersistence (ProfessionOutputPort professionPersistance) {
        this.profesionPersistence = professionPersistance;
    }
    @Override
    public Profession create(Profession profession) {
        log.debug("Into create on Application Domain");
        return profesionPersistence.save(profession);
    }
    @Override
    public Profession edit(Integer identification, Profession profession) throws NoExistException {
        Profession oldProfession = profesionPersistence.findById(identification);
        if (oldProfession != null)
            return profesionPersistence.save(profession);
        throw new NoExistException("The profession with id " + identification + " does not exist into db, cannot be edited");
    }
    @Override
    public Boolean drop(Integer identification) throws NoExistException {
        Profession oldProfession = profesionPersistence.findById(identification);
        if (oldProfession != null)
            return profesionPersistence.delete(identification);
        throw new NoExistException("The profession with id " + identification + " does not exist into db, cannot be dropped");
    }
    @Override
    public List<Profession> findAll() {
        log.info("Output: " + profesionPersistence.getClass());
        return profesionPersistence.find();
    }
    @Override
    public Profession findOne(Integer identification) throws NoExistException {
        Profession profession = profesionPersistence.findById(identification);
        if (profession == null)
            throw new NoExistException("The profession with id " + identification + " does not exist into db");
        return profession;
    }
    @Override
    public Integer count() {
        return profesionPersistence.find().size();
    }
}
