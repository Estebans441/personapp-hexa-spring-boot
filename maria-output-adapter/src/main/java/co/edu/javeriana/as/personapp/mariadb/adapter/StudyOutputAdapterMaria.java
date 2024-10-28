package co.edu.javeriana.as.personapp.mariadb.adapter;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudioRepositoryMaria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.domain.Study;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter("studyOutPutAdapterMaria")
@Transactional
public class StudyOutputAdapterMaria implements StudyOutputPort {
    @Autowired
    private EstudioRepositoryMaria estudioRepositoryMaria;
    @Autowired
    private EstudiosMapperMaria estudiosMapperMaria;
    @Override
    public Study save(Study study) {
        log.debug("Into save on Adapter MariaDB");
        EstudiosEntity persistedEstudio = estudioRepositoryMaria.save(estudiosMapperMaria.fromDomainToAdapter(study));
        return estudiosMapperMaria.fromAdapterToDomain(persistedEstudio);
    }
    @Override
    public Boolean delete(Integer identification, Integer idPerson) {
        log.debug("Into delete on Adapter MariaDB");
        EstudiosEntityPK estudiosEntityPK = new EstudiosEntityPK();
        estudiosEntityPK.setIdProf(identification);
        estudiosEntityPK.setCcPer(idPerson);
        estudioRepositoryMaria.deleteById(estudiosEntityPK);
        return estudioRepositoryMaria.findById(estudiosEntityPK).isEmpty();
    }
    @Override
    public Study findById(Integer identification, Integer idPerson) {
        log.debug("Into findById on Adapter MariaDB");
        EstudiosEntityPK estudiosEntityPK = new EstudiosEntityPK();
        estudiosEntityPK.setIdProf(identification);
        estudiosEntityPK.setCcPer(idPerson);
        if (estudioRepositoryMaria.findById(estudiosEntityPK).isEmpty()) {
            return null;
        } else {
            return estudiosMapperMaria.fromAdapterToDomain(estudioRepositoryMaria.findById(estudiosEntityPK).get());
        }
    }
    @Override
    public List<Study> find() {
        log.debug("Into find on Adapter MariaDB");
        return estudioRepositoryMaria.findAll().stream().map(estudiosMapperMaria::fromAdapterToDomain).collect(Collectors.toList());
    }
}
