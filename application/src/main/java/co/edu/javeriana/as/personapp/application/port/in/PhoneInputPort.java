package co.edu.javeriana.as.personapp.application.port.in;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.domain.Phone;

public interface PhoneInputPort {
    public void setPersistence (PhoneOutputPort phonePersistence);
    public Phone create(Phone phone);
    public Phone edit(Integer identification, Phone phone);
    public Boolean drop(Integer identification);
    public Phone findOne(Integer identification);
    public Integer count();
}


