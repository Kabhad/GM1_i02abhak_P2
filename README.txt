Requisitos para la Ejecución

    Java Runtime Environment (JRE) versión 17 o superior.
    Archivo JAR del programa: GM1_i02abhak.jar.

Comando para Ejecutar el Programa

bash

java -jar GM1_i02abhak.jar

El comando anterior ejecuta el programa en la terminal y abre el sistema de gestión.
Navegación del Sistema

Una vez ejecutado el programa, verás el menú principal que te ofrece las siguientes opciones:

=====================================
      Bienvenido al Sistema de Gestión
=====================================
1. Menú de Pistas
2. Menú de Reservas
3. Menú de Usuarios
0. Salir del programa
=====================================
Seleccione una opción:

Puedes seleccionar una opción del menú principal ingresando el número correspondiente seguido de Enter.
Menú de Pistas

=====================================
        GESTOR DE PISTAS
=====================================
1. Crear pista
2. Crear Material
3. Asociar material a pista disponible
4. Listar las pistas no disponibles
5. Buscar pistas disponibles
0. Volver al menú principal
=====================================
Seleccione una opción:

En este menú, puedes gestionar las pistas deportivas disponibles, crear materiales asociados y listar las pistas no disponibles.

    Opción 1: Crear pista
    Permite añadir una nueva pista al sistema.

    Opción 2: Crear Material
    Añade un nuevo material que puede ser asociado a las pistas.

    Opción 3: Asociar material a pista disponible
    Permite asignar un material a una pista disponible.

    Opción 4: Listar las pistas no disponibles
    Muestra las pistas que no están actualmente disponibles para reservas.

    Opción 5: Buscar pistas disponibles
    Busca y muestra las pistas disponibles para su reserva.

    Opción 0: Volver al menú principal
    Retorna al menú principal.

Menú de Reservas

=====================================
        GESTOR DE RESERVAS
=====================================
1. Hacer reserva individual
2. Hacer reserva con bono
3. Modificar reserva
4. Cancelar reserva
5. Consultar reservas futuras
6. Consultar reservas por día y pista
0. Volver al menú principal
=====================================
Seleccione una opción:

Aquí se gestionan las reservas de pistas. Algunas funciones destacadas son:

    Opción 1: Hacer reserva individual
    Permite realizar una reserva para una única sesión.

    Opción 2: Hacer reserva con bono
    Realiza una reserva utilizando un bono que incluye varias sesiones.

    Opción 3: Modificar reserva
    Permite modificar una reserva ya existente.

    Opción 4: Cancelar reserva
    Cancela una reserva previamente realizada.

    Opción 5: Consultar reservas futuras
    Muestra las reservas que están programadas para fechas futuras.

    Opción 6: Consultar reservas por día y pista
    Filtra las reservas en base a una fecha y una pista específica.

    Opción 0: Volver al menú principal
    Retorna al menú principal.

Menú de Usuarios

=====================================
      Bienvenido al Menú de Usuarios
=====================================
1. Alta de Usuario
2. Modificar Usuario
3. Listar Usuarios
4. Baja de Usuario
0. Volver al menú principal
=====================================
Seleccione una opción:

Aquí se gestionan los usuarios que pueden realizar reservas.

    Opción 1: Alta de Usuario
    Permite registrar un nuevo usuario en el sistema.

    Opción 2: Modificar Usuario
    Permite modificar los datos de un usuario existente.

    Opción 3: Listar Usuarios
    Muestra todos los usuarios registrados en el sistema.

    Opción 4: Baja de Usuario
    Elimina a un usuario del sistema.

    Opción 0: Volver al menú principal
    Retorna al menú principal.

Descuentos por Antigüedad

El sistema aplica automáticamente un descuento del 10% a los jugadores con más de dos años de antigüedad en las reservas individuales. Para las reservas con bonos, se aplica un descuento del 5%.
Guardado de Datos

Al salir del programa, los datos de las reservas y usuarios se guardan automáticamente.
Ejemplo de Ejecución:

$ java -jar GM1_i02abhak.jar
=====================================
      Bienvenido al Sistema de Gestión
=====================================
1. Menú de Pistas
2. Menú de Reservas
3. Menú de Usuarios
0. Salir del programa
=====================================
Seleccione una opción:

Para seleccionar una opción, simplemente ingresa el número correspondiente y presiona Enter