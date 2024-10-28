package co.edu.javeriana.as.personapp.terminal.adapter;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {
    @Autowired
    @Qualifier("profesionOutPutAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("profesionOutPutAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfesionMapperCli profesionMapperCli;
    private ProfessionInputPort professionInputPort;

    public void setProfessionOutputPortInjection (String dbOptions) throws InvalidOptionException {
        if (dbOptions.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
        } else if (dbOptions.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOptions);
        }
    }

    public void historial() {
        log.info("Into historial ProfesionEntity in Input Adapter");
        professionInputPort.findAll().stream()
            .map(profesionMapperCli::fromDomainToBasicModelCli)
            .forEach(System.out::println);
    }

    public void create (int id, String name, String description) {
        log.info("Into create ProfesionEntity in Input Adapter");
        ProfesionModelCli profesionModelCli = new ProfesionModelCli();
        profesionModelCli.setId(id);
        profesionModelCli.setDescription(description);
        profesionModelCli.setName(name);
        professionInputPort.create(profesionMapperCli.fromBasicModelCliToDomain(profesionModelCli));
    }
}
