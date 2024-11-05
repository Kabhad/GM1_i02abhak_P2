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

// Crear la conexion con la BBDD

public class DBConnection {

	protected Connection connection = null;

	public Connection getConnection(){
	    
	    Properties prop = new Properties();
		String filename = "config.properties";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			prop.load(reader);
			String url = prop.getProperty("url");
			String user = prop.getProperty("user");
			String password = prop.getProperty("password");
			
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = (Connection) DriverManager.getConnection(url, user, password);
			System.out.println("Database connection successfully opened!");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
			
		}catch (SQLException e) {
			System.err.println("		Connection to MySQL has failed!");
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			System.err.println("		JDBC Driver not found.");
			e.printStackTrace();
		}
		
		return this.connection;
	}

	public void closeConnection() {
		try {
			if(this.connection != null && !this.connection.isClosed()) {
				this.connection.close();
				//System.out.println("		Database connection successfully closed!");
			}
		} catch (SQLException e) {
			System.err.println("		Error while trying to close the connection.");
			e.printStackTrace();
		}
	}
}
