package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class ProfesionMenu {
    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;

    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_VER_TODO = 1;
    private static final int OPCION_CREAR = 2;
    private static final int OPCION_BUSCAR = 3;
    private static final int OPCION_EDITAR = 4;
    private static final int OPCION_ELIMINAR = 5;
    private static final int OPCION_CONTAR = 6;

    public void iniciarMenu(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
                        profesionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
                        menuOpciones(profesionInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        profesionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
                        menuOpciones(profesionInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InvalidOptionException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    private void menuOpciones(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
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
                        profesionInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        crearProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_BUSCAR:
                        buscarProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_EDITAR:
                        editarProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        eliminarProfesion(profesionInputAdapterCli, keyboard);
                        break;
                    case OPCION_CONTAR:
                        profesionInputAdapterCli.count();
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InputMismatchException e) {
                log.warn("Solo se permiten números.");
                keyboard.next(); // Limpiar la entrada inválida
            } catch (NoExistException e) {
                System.out.println(e.getMessage());
            }
        } while (!isValid);
    }

    private void crearProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) {
        System.out.print("Ingrese el id de la profesión: ");
        int id = keyboard.nextInt();
        keyboard.nextLine(); // Limpiar buffer de entrada
        System.out.print("Ingrese el nombre de la profesión: ");
        String name = keyboard.nextLine();
        System.out.print("Ingrese la descripción de la profesión: ");
        String description = keyboard.nextLine();

        profesionInputAdapterCli.create(id, name, description);
        System.out.println("Profesión creada con éxito.");
    }

    private void buscarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) throws NoExistException {
        System.out.print("Ingrese el id de la profesión: ");
        int id = keyboard.nextInt();

        profesionInputAdapterCli.findOne(id);
    }

    private void editarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) throws NoExistException {
        System.out.print("Ingrese el id de la profesión a editar: ");
        int id = keyboard.nextInt();
        keyboard.nextLine(); // Limpiar buffer de entrada
        System.out.print("Ingrese el nuevo nombre de la profesión: ");
        String name = keyboard.nextLine();
        System.out.print("Ingrese la nueva descripción de la profesión: ");
        String description = keyboard.nextLine();

        profesionInputAdapterCli.edit(id, name, description);
    }

    private void eliminarProfesion(ProfesionInputAdapterCli profesionInputAdapterCli, Scanner keyboard) throws NoExistException {
        System.out.print("Ingrese el id de la profesión a eliminar: ");
        int id = keyboard.nextInt();
        profesionInputAdapterCli.drop(id);
    }

    private void mostrarMenuOpciones() {
        System.out.println("----------------------");
        System.out.println(OPCION_VER_TODO + " para ver todas las profesiones");
        System.out.println(OPCION_CREAR + " para crear una profesión");
        System.out.println(OPCION_BUSCAR + " para buscar una profesión");
        System.out.println(OPCION_EDITAR + " para editar una profesión");
        System.out.println(OPCION_ELIMINAR + " para eliminar una profesión");
        System.out.println(OPCION_CONTAR + " para contar todas las profesiones");
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
            keyboard.next(); // Limpiar la entrada inválida
            return leerOpcion(keyboard);
        }
    }
}
