package co.edu.javeriana.as.personapp.mariadb.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;

@Mapper
public class TelefonoMapperMaria {
	@Autowired
	private PersonaMapperMaria personaMapperMaria;
	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		telefonoEntity.setDuenio(mapOwnerWithoutPhones(phone.getOwner()));
		return telefonoEntity;
	}

	public TelefonoEntity fromDomainToAdapterWithoutOwner(Phone phone) {
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		return telefonoEntity;
	}
	private PersonaEntity mapOwnerWithoutPhones(Person owner) {
		return owner != null ? personaMapperMaria.fromDomainToAdapter(owner) : new PersonaEntity();
	}
	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		phone.setCompany(telefonoEntity.getOper());
		Person owner = personaMapperMaria.fromAdapterToDomain(telefonoEntity.getDuenio());
		phone.setOwner(owner);
		return phone;
	}
	public Phone fromAdapterToDomainWithoutOwner(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		phone.setCompany(telefonoEntity.getOper());
		return phone;
	}
}


