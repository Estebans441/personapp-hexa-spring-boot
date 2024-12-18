package co.edu.javeriana.as.personapp.application.port.in;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;

import java.util.List;
@Port
public interface PhoneInputPort {
    public void setPersistence (PhoneOutputPort phonePersistence, PersonOutputPort personPersistence);
    public Phone create(Phone phone, int ccPerson) throws NoExistException;
    public Phone edit(String identification, Phone phone, int ccPerson) throws NoExistException;
    public Boolean drop(String identification) throws NoExistException;
    public Phone findOne(String identification) throws NoExistException;
    public List<Phone> findAll();
    public Integer count();
}


