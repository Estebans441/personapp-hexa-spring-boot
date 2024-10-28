package co.edu.javeriana.as.personapp.mongo.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper
public class TelefonoMapperMongo {

	@Autowired
	@Lazy
	private PersonaMapperMongo personaMapperMongo;

	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		PersonaDocument ownerDocument = personaMapperMongo.fromDomainToAdapter(phone.getOwner());
		return TelefonoDocument.builder()
				.id(phone.getNumber())
				.oper(phone.getCompany())
				.primaryDuenio(ownerDocument) // Asegura que el dueño no sea null
				.build();
	}

	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		return Phone.builder()
				.number(telefonoDocument.getId())
				.company(telefonoDocument.getOper())
				.owner(telefonoDocument.getPrimaryDuenio() != null ?
						personaMapperMongo.fromAdapterToDomain(telefonoDocument.getPrimaryDuenio()) : null)
				.build();
	}
}
