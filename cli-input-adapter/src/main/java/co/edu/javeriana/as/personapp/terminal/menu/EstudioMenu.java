package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class EstudioMenu {
    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;
    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_VER_TODO = 1;
    private static final int OPCION_CREAR = 2;

    public void iniciarMenu (EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
        boolean isValid = false;
        do {
            try {
                mostrarMenuMotorPersistencia();
                int opcion = leerOpcion(keyboard);
                switch (opcion) {
                    case OPCION_REGRESAR_MODULOS:
                        isValid = true;
                        break;
                    case PERSISTENCIA_MARIADB:
                        estudioInputAdapterCli.setStudyOutputPortInjection("MARIA");
                        menuOpciones(estudioInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        estudioInputAdapterCli.setStudyOutputPortInjection("MONGO");
                        menuOpciones(estudioInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InvalidOptionException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    public void menuOpciones (EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) throws InvalidOptionException {
        boolean isValid = false;
        do {
            try {
                mostrarMenuOpciones();
                int opcion = leerOpcion(keyboard);
                switch (opcion) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        isValid = true;
                        break;
                    case OPCION_VER_TODO:
                        estudioInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        try {
                            // Crear estudio
                            System.out.print("Ingrese el número de identificación de la persona: ");
                            int ccPerson = keyboard.nextInt();
                            System.out.print("Ingrese el número de identificación de la profesión: ");
                            int idProfession = keyboard.nextInt();
                            System.out.print("Ingrese el nombre de la universidad: ");
                            String college = keyboard.next();
                            System.out.print("Ingrese la fecha de graduación (yyyy-mm-dd): ");
                            String date = keyboard.next();
                            estudioInputAdapterCli.create(ccPerson, idProfession, college, LocalDate.parse(date));
                        } catch (InputMismatchException e) {
                            log.warn("Solo se permiten números.");
                        } catch (NoExistException e) {
                            System.out.println(e.getMessage());
                        }

                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InputMismatchException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    private void mostrarMenuOpciones() {
        System.out.println("----------------------");
        System.out.println(OPCION_VER_TODO + " para ver todos los estudios");
        System.out.println(OPCION_CREAR + " para crear un estudio");
        // implementar otras opciones
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("----------------------");
        System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
        System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
        System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
    }

    private int leerOpcion(Scanner keyboard) {
        try {
            System.out.print("Ingrese una opción: ");
            return keyboard.nextInt();
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            return leerOpcion(keyboard);
        }
    }

}
