package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.adapter.EstudioInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class EstudioMenu {
    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;
    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_VER_TODO = 1;
    private static final int OPCION_CREAR = 2;
    private static final int OPCION_BUSCAR = 3;
    private static final int OPCION_ELIMINAR = 4;
    private static final int OPCION_EDITAR = 5;
    private static final int OPCION_CONTAR = 6;

    public void iniciarMenu(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
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

    public void menuOpciones(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) throws InvalidOptionException {
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
                        crearEstudio(estudioInputAdapterCli, keyboard);
                        break;
                    case OPCION_BUSCAR:
                        buscarEstudio(estudioInputAdapterCli, keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        eliminarEstudio(estudioInputAdapterCli, keyboard);
                        break;
                    case OPCION_EDITAR:
                        editarEstudio(estudioInputAdapterCli, keyboard);
                        break;
                    case OPCION_CONTAR:
                        estudioInputAdapterCli.count();
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InputMismatchException e) {
                log.warn(e.getMessage());
                keyboard.next();
            }
        } while (!isValid);
    }

    private void crearEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
        try {
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
            keyboard.next();
        } catch (NoExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void buscarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
        try {
            System.out.print("Ingrese el ID de la profesión registrada para el estudio: ");
            int id = keyboard.nextInt();
            System.out.print("Ingrese el número de identificación de la persona: ");
            int ccPerson = keyboard.nextInt();
            estudioInputAdapterCli.findOne(id, ccPerson);
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            keyboard.next();
        } catch (NoExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
        try {
            System.out.print("Ingrese el número de identificación de la persona: ");
            int ccPerson = keyboard.nextInt();
            System.out.print("Ingrese el ID de la profesion: ");
            int id = keyboard.nextInt();
            estudioInputAdapterCli.drop(id, ccPerson);
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            keyboard.next();
        } catch (NoExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editarEstudio(EstudioInputAdapterCli estudioInputAdapterCli, Scanner keyboard) {
        try {
            System.out.print("Ingrese el ID de la profesion del estudio: ");
            int idProfession = keyboard.nextInt();

            System.out.print("Ingrese el número de identificación de la persona: ");
            int ccPerson = keyboard.nextInt();
            keyboard.nextLine();

            System.out.print("Ingrese el nuevo nombre de la universidad: ");
            String college = keyboard.nextLine();
            System.out.print("Ingrese la nueva fecha de graduación (yyyy-mm-dd): ");
            String date = keyboard.nextLine();
            estudioInputAdapterCli.edit(ccPerson, idProfession, college, LocalDate.parse(date));
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            keyboard.next();
        } catch (NoExistException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            log.warn("Error al editar el estudio.");
            System.out.println(e.getMessage());
        }
    }

    private void mostrarMenuOpciones() {
        System.out.println("----------------------");
        System.out.println(OPCION_VER_TODO + " para ver todos los estudios");
        System.out.println(OPCION_CREAR + " para crear un estudio");
        System.out.println(OPCION_BUSCAR + " para buscar un estudio");
        System.out.println(OPCION_ELIMINAR + " para eliminar un estudio");
        System.out.println(OPCION_EDITAR + " para editar un estudio");
        System.out.println(OPCION_CONTAR + " para contar los estudios");
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
            keyboard.next();
            return leerOpcion(keyboard);
        }
    }
}
