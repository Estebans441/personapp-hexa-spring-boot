package co.edu.javeriana.as.personapp.mongo.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("telefono")
public class TelefonoDocument {
	@Id
	private String id;
	private String oper;
	@DocumentReference(lazy = true)
    private PersonaDocument primaryDuenio;
}
