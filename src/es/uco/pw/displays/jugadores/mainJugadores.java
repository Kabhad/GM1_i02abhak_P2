package es.uco.pw.displays.jugadores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.data.dao.JugadoresDAO;

/**
 * Clase principal que implementa un menú de gestión de jugadores.
 * Permite realizar operaciones como alta, modificación, baja y listado de jugadores
 * mediante el uso de la clase JugadoresDAO.
 */
public class mainJugadores {

    /**
     * Imprime el menú principal de opciones para la gestión de jugadores.
     */
    public static void imprimirMenu() {
        System.out.println("=====================================");
        System.out.println("      Bienvenido al Menú de Usuarios");
        System.out.println("=====================================");
        System.out.println("1. Alta de Usuario");
        System.out.println("2. Modificar Usuario");
        System.out.println("3. Listar Usuarios");
        System.out.println("4. Baja de Usuario");
        System.out.println("0. Volver al menú principal");
        System.out.println("=====================================");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Método principal que ejecuta el menú de gestión de jugadores.
     * Las opciones incluyen alta, modificación, baja y listado de jugadores.
     *
     * @param sc El objeto Scanner utilizado para leer las entradas del usuario.
     */
    public static void main(Scanner sc) {
        JugadoresDAO gestor = new JugadoresDAO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int opcion;

        do {
            imprimirMenu();
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1: // Alta de jugador
                    System.out.println("Iniciando proceso de alta de jugador...");
                    try {
                        System.out.print("Ingrese nombre y apellidos: ");
                        String nombreApellidos = sc.nextLine();

                        System.out.print("Ingrese fecha de nacimiento (yyyy-MM-dd): ");
                        String fechaNacimientoStr = sc.nextLine();
                        Date fechaNacimiento = dateFormat.parse(fechaNacimientoStr);

                        System.out.print("Ingrese el correo electrónico: ");
                        String correo = sc.nextLine();

                        JugadorDTO nuevoJugador = new JugadorDTO();
                        nuevoJugador.setNombreApellidos(nombreApellidos);
                        nuevoJugador.setFechaNacimiento(fechaNacimiento);
                        nuevoJugador.setCorreoElectronico(correo);

                        // Recoger la respuesta del gestor y mostrarla
                        String resultado = gestor.altaJugador(nuevoJugador);
                        System.out.println(resultado);

                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto. Intente nuevamente.");
                    }
                    break;

                case 2: // Modificar jugador
                    System.out.println("Iniciando proceso de modificación de jugador...");
                    System.out.print("Ingrese el correo electrónico del jugador a modificar: ");
                    String correoModificar = sc.nextLine();

                    System.out.print("Ingrese el nuevo nombre y apellidos: ");
                    String nuevoNombre = sc.nextLine();

                    System.out.print("Ingrese la nueva fecha de nacimiento (yyyy-MM-dd): ");
                    String nuevaFechaNacimientoStr = sc.nextLine();
                    Date nuevaFechaNacimiento;
                    try {
                        nuevaFechaNacimiento = dateFormat.parse(nuevaFechaNacimientoStr);
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
                        break;
                    }

                    System.out.print("Ingrese el nuevo correo electrónico: ");
                    String nuevoCorreo = sc.nextLine();

                    // Recoger la respuesta del gestor y mostrarla
                    String resultadoModificacion = gestor.modificarJugador(correoModificar, nuevoNombre, nuevaFechaNacimiento, nuevoCorreo);
                    System.out.println(resultadoModificacion);
                    break;

                case 3: // Listar jugadores
                    String resultadoListado = gestor.listarJugadores();
                    System.out.println(resultadoListado);
                    break;

                case 4: // Baja de jugador
                    System.out.println("Iniciando proceso de baja de jugador...");
                    System.out.print("Ingrese el correo electrónico del jugador a dar de baja: ");
                    String correoBaja = sc.nextLine();

                    // Llamar a la función de bajaJugador del gestor y mostrar el resultado
                    String resultadoBaja = gestor.bajaJugador(correoBaja);
                    System.out.println(resultadoBaja);
                    break;

                case 0: // Salir
                    System.out.println("Volviendo al menú principal...");
                    break;

                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        } while (opcion != 0);
    }
}
