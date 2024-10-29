package co.edu.javeriana.as.personapp.terminal.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesionModelCli {
    private Integer id;
    private String name;
    private String description;
}


