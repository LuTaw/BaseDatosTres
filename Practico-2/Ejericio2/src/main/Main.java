package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
	private static String USER = "root";
	private static String PASS = "root";
	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		String url = "jdbc:mysql://localhost:3306/";
		try {
			connection = DriverManager.getConnection(url, USER, PASS);
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			
			// creo la base de datos si no existe
			String sqlString = "CREATE DATABASE IF NOT EXISTS Escuela";
			statement.executeUpdate(sqlString);
            System.out.println("Base de datos 'Escuela' creada.");
            
            // selecciono la base de datos a utilizar
            statement.execute("USE Escuela");

            // creo la tabla personas si no existe
            sqlString = "CREATE TABLE IF NOT EXISTS Personas ("
                      		+ "cedula INT PRIMARY KEY, "
                      		+ "nombre VARCHAR(45), "
                      		+ "apellido VARCHAR(45))";
            statement.executeUpdate(sqlString);
            System.out.println("Tabla 'Personas' creada.");
            
            // creo la tabla maestras si no existe
            sqlString = "CREATE TABLE IF NOT EXISTS Maestras ("
                    		+ "cedula INT PRIMARY KEY, "
                    		+ "grupo VARCHAR(45), "
                    		+ "FOREIGN KEY (cedula) REFERENCES Personas(cedula))";
            statement.executeUpdate(sqlString);
            System.out.println("Tabla 'Maestras' creada.");
            
            // creo la tabla alumnos si no existe
            sqlString = "CREATE TABLE IF NOT EXISTS Alumnos ("
            				+ "cedula INT PRIMARY KEY, "
            				+ "cedulaMaestra INT, "
            				+ "FOREIGN KEY (cedula) REFERENCES Personas(cedula), "
            				+ "FOREIGN KEY (cedulaMaestra) REFERENCES Maestras(cedula))";
            statement.executeUpdate(sqlString);
            System.out.println("Tabla 'Alumnos' creada.");
            
            connection.commit(); 
            
		} catch (SQLException exception) {
			connection.rollback(); 
			exception.printStackTrace();
		}  finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}

}
