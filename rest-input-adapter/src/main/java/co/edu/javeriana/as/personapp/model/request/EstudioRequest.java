package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioRequest {
    private String personId;          // Solo el ID de la persona
    private String professionId;      // Solo el ID de la profesión
    private String graduationDate;
    private String universityName;
    private String database;
}
