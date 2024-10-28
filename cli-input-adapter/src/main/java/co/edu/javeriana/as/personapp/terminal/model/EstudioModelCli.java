package co.edu.javeriana.as.personapp.terminal.model;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudioModelCli {
    private Person person;
    private Profession profession;
    private LocalDate graduationDate;
    private String universityName;

    @Override
    public String toString() {
        return "EstudioModelCli [person=" + person.getFirstName() + " " + person.getLastName() + ", profession=" + profession.getName()
                + ", graduationDate=" + graduationDate + ", universityName=" + universityName + "]";
    }
}