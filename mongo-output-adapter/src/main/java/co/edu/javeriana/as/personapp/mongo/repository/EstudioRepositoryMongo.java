package co.edu.javeriana.as.personapp.mongo.repository;

import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EstudioRepositoryMongo extends MongoRepository<EstudiosDocument, String> {
    Optional<EstudiosDocument> findByPrimaryPersonaAndPrimaryProfesion(PersonaDocument primaryPersona, ProfesionDocument primaryProfesion);
}



