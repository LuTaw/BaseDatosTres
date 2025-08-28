package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
	private static String USER = "root";
	private static String PASS = "root";
	public static void main(String[] args) throws SQLException {
		int maxAlumnos = -1;
		int cedulaMaxAlumnos = 0;
		Connection connection = null;
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		String url = "jdbc:mysql://localhost:3306/Escuela";
		try {
			connection = DriverManager.getConnection(url, USER, PASS);
			connection.setAutoCommit(false);
			statement = connection.createStatement();

            // Select maestra con mayor cantidad de alumnos            
            String sqlQuery = "SELECT * "
								+ "FROM Maestras; ";
            ResultSet resultadoMaestras = statement.executeQuery(sqlQuery);
            String segundoSelect = "SELECT COUNT(*) "
            		+ "FROM Alumnos "
            		+ "WHERE cedula = ? ";
            preparedStatement = connection.prepareStatement(segundoSelect);
            while (resultadoMaestras.next()) {
                int cedula = resultadoMaestras.getInt("cedula");
                preparedStatement.setInt(1, cedula);
                ResultSet resultadoCantAlumnos = preparedStatement.executeQuery();
                while (resultadoCantAlumnos.next()) {
                	int cantActual = resultadoCantAlumnos.getInt(1);
                	if (cantActual > maxAlumnos) {
                		maxAlumnos = cantActual;
                		cedulaMaxAlumnos = cedula;
                	}
                }                
            }
            preparedStatement.close();
            String tercerSelect = "SELECT * "
				            		+ "FROM Personas "
				            		+ "WHERE cedula = ? ";
            preparedStatement = connection.prepareStatement(tercerSelect);
            preparedStatement.setInt(1, cedulaMaxAlumnos);
            ResultSet resultadoFinal = preparedStatement.executeQuery();

            connection.commit(); 

            while (resultadoFinal.next()) {
                int cedula = resultadoFinal.getInt("cedula");
                String nombre = resultadoFinal.getString("nombre");
                String apellido = resultadoFinal.getString("apellido");

                System.out.println("Cedula: " + cedula + 
                                   ", Nombre: " + nombre + 
                                   ", Apellido: " + apellido);
            }            

		} catch (SQLException exception) {
			exception.printStackTrace();
		}  finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}
}
