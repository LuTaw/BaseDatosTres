package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
	private static String USER = "root";
	private static String PASS = "root";
	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultado = null;
		String url = "jdbc:mysql://localhost:3306/Escuela";
		try {
			connection = DriverManager.getConnection(url, USER, PASS);
			statement = connection.createStatement();

            // Select maestra con mayor cantidad de alumnos            
            String sqlQuery = "SELECT *"
					        	+ "FROM Personas "
					        	+ "WHERE cedula = ( "
					        		+ "SELECT cedulaMaestra "
					        		+ "FROM Alumnos "
					        		+ "GROUP BY cedulaMaestra "
					        		+ "ORDER BY COUNT(*) DESC "
					        		+ "LIMIT 1 "
					        		+ "); ";
            resultado = statement.executeQuery(sqlQuery);
            while (resultado.next()) {
                int cedula = resultado.getInt("cedula");
                String nombre = resultado.getString("nombre");
                String apellido = resultado.getString("apellido");

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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}
}
