package es.uco.pw.data.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que maneja la conexión a la base de datos.
 * Permite establecer y cerrar la conexión con la base de datos definida en un archivo de configuración.
 */
public class DBConnection {

    /**
     * Objeto para mantener la conexión con la base de datos.
     */
    protected Connection connection = null;

    /**
     * Establece y retorna la conexión con la base de datos.
     * La configuración de la base de datos se lee desde un archivo llamado {@code config.properties}.
     * 
     * @return La conexión establecida con la base de datos.
     */
    public Connection getConnection() {
        Properties prop = new Properties();
        String filename = "config.properties";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            prop.load(reader);
            String url = prop.getProperty("url");
            String user = prop.getProperty("user");
            String password = prop.getProperty("password");

            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection to MySQL has failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            e.printStackTrace();
        }

        return this.connection;
    }

    /**
     * Cierra la conexión con la base de datos si está abierta.
     * 
     */
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while trying to close the connection.");
            e.printStackTrace();
        }
    }
}
