package es.uco.pw.displays.principal;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import es.uco.pw.displays.jugadores.mainJugadores;
import es.uco.pw.displays.pistas.mainPistas;
import es.uco.pw.displays.reservas.mainReservas;
import es.uco.pw.data.dao.ReservasDAO;
/**
 * Clase principal que gestiona la ejecución del sistema de gestión.
 */
public class mainPrincipal {

    /**
     * Imprime el menú principal del sistema.
     */
    public static void imprimirMenu() {
        System.out.println("=====================================");
        System.out.println("      Bienvenido al Sistema de Gestión");
        System.out.println("=====================================");
        System.out.println("1. Menú de Pistas");
        System.out.println("2. Menú de Reservas");
        System.out.println("3. Menú de Usuarios");
        System.out.println("0. Salir del programa");
        System.out.println("=====================================");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Método principal que inicia la ejecución del programa.
     *
     * @param args Los argumentos de línea de comandos (no utilizados).
     * @throws IOException    Si ocurre un error al cargar o guardar datos en los ficheros.
     * @throws ParseException Si ocurre un error de análisis de datos.
     */
    public static void main(String[] args) throws IOException, ParseException {
        Scanner sc = new Scanner(System.in);
        ReservasDAO reservasDAO = new ReservasDAO();
        
        int opcion;
        boolean continuar = true;

        while (continuar) {
            imprimirMenu(); // Mostrar el menú principal
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println("Accediendo al Menú de Pistas...");
                    mainPistas.main(sc);
                    break;
                case 2:
                    System.out.println("Accediendo al Menú de Reservas...");
                    mainReservas.main(sc, reservasDAO);
                    break;
                case 3:
                    System.out.println("Accediendo al Menú de Usuarios...");
                    mainJugadores.main(sc); // Pasar el Scanner al menú de Usuarios
                    break;
                case 0:
                    System.out.println("Saliendo del programa. Guardando datos en los ficheros...");
                    continuar = false; // Romper el bucle para salir
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        }

        sc.close(); // Cerrar el Scanner al final
    }
}

