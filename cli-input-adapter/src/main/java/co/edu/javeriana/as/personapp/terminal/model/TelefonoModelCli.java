package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefonoModelCli {
    private String num;
    private String oper;
    private PersonaModelCli duenio;

    @Override
    public String toString() {
        return "TelefonoModelCli [num=" + num + ", oper=" + oper + ", duenio=" + getDuenio()
                .getNombre() + " " + getDuenio().getApellido() + "]";
    }
}
