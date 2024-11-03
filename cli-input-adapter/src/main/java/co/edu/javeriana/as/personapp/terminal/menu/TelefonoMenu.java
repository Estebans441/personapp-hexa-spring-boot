package co.edu.javeriana.as.personapp.terminal.menu;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class TelefonoMenu {
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

    public void iniciarMenu(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
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
                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MARIA");
                        menuOpciones(telefonoInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MONGO");
                        menuOpciones(telefonoInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InvalidOptionException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    public void menuOpciones(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) throws InvalidOptionException {
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
                        telefonoInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        crearTelefono(telefonoInputAdapterCli, keyboard);
                        break;
                    case OPCION_BUSCAR:
                        buscarTelefono(telefonoInputAdapterCli, keyboard);
                        break;
                    case OPCION_EDITAR:
                        editarTelefono(telefonoInputAdapterCli, keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        eliminarTelefono(telefonoInputAdapterCli, keyboard);
                        break;
                    case OPCION_CONTAR:
                        telefonoInputAdapterCli.count();
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

    private void crearTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
        try {
            keyboard.nextLine();
            System.out.print("Ingrese el número de teléfono: ");
            String number = keyboard.nextLine();
            System.out.print("Ingrese la compañía: ");
            String company = keyboard.nextLine();
            System.out.print("Ingrese el ID del dueño del teléfono: ");
            int owner = keyboard.nextInt();
            keyboard.nextLine(); // Limpiar buffer de entrada

            telefonoInputAdapterCli.create(number, company, owner);
            System.out.println("Teléfono creado con éxito.");
        } catch (InputMismatchException e) {
            log.warn("Solo se permiten números.");
            keyboard.next();
        } catch (NoExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void buscarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) throws NoExistException {
        System.out.print("Ingrese el número de teléfono a buscar: ");
        String number = keyboard.next();

        telefonoInputAdapterCli.findOne(number);
    }

    private void editarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) throws NoExistException {
        keyboard.nextLine();
        System.out.print("Ingrese el número de teléfono a editar: ");
        String number = keyboard.nextLine();
        System.out.print("Ingrese la nueva compañía: ");
        String company = keyboard.nextLine();
        System.out.print("Ingrese el nuevo ID del dueño del teléfono: ");
        int owner = keyboard.nextInt();
        keyboard.nextLine(); // Limpiar buffer de entrada

        telefonoInputAdapterCli.edit(number, company, owner);
    }

    private void eliminarTelefono(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) throws NoExistException {
        keyboard.nextLine();
        System.out.print("Ingrese el número de teléfono a eliminar: ");
        String number = keyboard.nextLine();

        telefonoInputAdapterCli.drop(number);
    }

    private void mostrarMenuOpciones() {
        System.out.println("----------------------");
        System.out.println(OPCION_VER_TODO + " para ver todos los teléfonos");
        System.out.println(OPCION_CREAR + " para crear un teléfono");
        System.out.println(OPCION_BUSCAR + " para buscar un teléfono");
        System.out.println(OPCION_EDITAR + " para editar un teléfono");
        System.out.println(OPCION_ELIMINAR + " para eliminar un teléfono");
        System.out.println(OPCION_CONTAR + " para contar todos los teléfonos");
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
