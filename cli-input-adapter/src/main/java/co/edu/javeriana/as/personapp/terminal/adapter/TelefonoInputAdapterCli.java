package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {
    @Autowired
    @Qualifier("phoneOutPutAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutPutAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;

    @Autowired
    private TelefonoMapperCli telefonoMapperCli;

    private PhoneInputPort phoneInputPort;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private PersonaMapperCli personaMapperCli;

    private PersonInputPort personInputPort;

    /**
     * Método actualizado para sincronizar `phoneInputPort` y `personInputPort`
     * con la misma base de datos según la opción seleccionada.
     */
    public void setPhoneOutputPortInjection(String dbOption) {
        if (dbOption.equalsIgnoreCase("MARIA")) {
            this.phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
            this.personInputPort = new PersonUseCase(personOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase("MONGO")) {
            this.phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
            this.personInputPort = new PersonUseCase(personOutputPortMongo);
        } else {
            throw new IllegalArgumentException("Invalid database option: " + dbOption);
        }
    }

    public void historial() {
        log.info("Into historial TelefonoEntity in Input Adapter");
        phoneInputPort.findAll().stream()
                .map(telefonoMapperCli::fromDomainToCli)
                .forEach(System.out::println);
    }

    public void create(String number, String company, int ownerId) throws NoExistException {
        // Verificación de existencia del dueño
        PersonaModelCli ownerModel = personaMapperCli.fromDomainToBasicModelCli(personInputPort.findOne(ownerId));
        if (ownerModel == null) {
            throw new NoExistException("The owner with id " + ownerId + " does not exist in the database, cannot create phone.");
        }

        // Crear y guardar el teléfono
        TelefonoModelCli telefonoModel = TelefonoModelCli.builder().num(number).oper(company).duenio(ownerModel).build();
        phoneInputPort.create(telefonoMapperCli.fromCliToDomain(telefonoModel), ownerId);
    }
}
