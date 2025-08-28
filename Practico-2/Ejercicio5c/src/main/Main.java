package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	private static String USER = "root";
	private static String PASS = "root";

	public static void main(String[] args) {
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        String opcion = "";
        try {
            while (true) {
                // Desplegar menú
                System.out.println("=== Menu Principal ===");
                System.out.println("1. Seleccione la opcion si desea ejecutar el stored procedure.");
                System.out.println("2. Salir");
                System.out.print("Seleccione una opcion: \n");

                opcion = bufferReader.readLine();  // lee la opcion ingresada

                if ("1".equals(opcion)) {
                    System.out.print("Ingrese un cedula: \n");
                    String cedula = bufferReader.readLine();
                    
                    try {
                    	ejecutarStoredProcedure(cedula);
					} catch (SQLException exception) {
						System.out.print("Ocurrio un error al ejecutar el stored procedure: \n");
						System.out.print(exception.getMessage());
						exception.printStackTrace();
					}

                } else if ("2".equals(opcion)) {
                    System.out.println("Saliendo de la aplicacion...");
                    break;

                } else {
                    System.out.println("Opción invalida, intente de nuevo. \n ");
                }

                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private static void ejecutarStoredProcedure(String cedula) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		String url = "jdbc:mysql://localhost:3306/Escuela";
		
		try {
			connection = DriverManager.getConnection(url, USER, PASS);
			connection.setAutoCommit(false);
			
			String storedProcedure = "{call Escuela.BorrarMaestra(?)}";
			CallableStatement callableStatement = connection.prepareCall(storedProcedure);
			callableStatement.setString (1, cedula);
			boolean hasResultSet = callableStatement.execute();
			int cantResultados = 0;
			
			while (hasResultSet) {
				cantResultados++;
				try (ResultSet resultSet = callableStatement.getResultSet()) {
			        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			        int columnas = resultSetMetaData.getColumnCount();
			        if (cantResultados == 1) {
			        	System.out.println("Resultado SELECT sobre Alumnos \n ");
			        }

			        if (cantResultados == 2) {
			        	System.out.println("Resultado SELECT sobre Maestras \n ");
			        }

			        if (cantResultados == 3) {
			        	System.out.println("Resultado SELECT sobre Personas \n ");
			        }

			        while (resultSet.next()) {
			            for (int i = 1; i <= columnas; i++) {
			                System.out.print(resultSet.getObject(i) + " ");
			            }
			            System.out.println();
			        }
			    }
	            System.out.println(); 
			    hasResultSet = callableStatement.getMoreResults(); // para avanzar al próximo SELECT si hay varios
			}

			
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
