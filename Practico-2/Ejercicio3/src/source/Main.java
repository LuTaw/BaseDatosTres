package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	private static String USER = "root";
	private static String PASS = "root";

	public static void main(String[] args) {
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        String opcion = "";
        int resultado;
        try {
            while (true) {
                // Desplegar menú
                System.out.println("=== Menu Principal ===");
                System.out.println("1. Ingresar comando SQL (INSERT, UPDATE, DELETE)");
                System.out.println("2. Salir");
                System.out.print("Seleccione una opcion: \n");

                opcion = bufferReader.readLine();  // lee la opcion ingresada

                if ("1".equals(opcion)) {
                    System.out.print("Ingrese un comando: \n");
                    String comando = bufferReader.readLine();
                    
                    try {
                    	resultado = ejecutarQuery(comando);
                    	System.out.print("La cantidad de columnas afectadas es:" + resultado + "\n");
                        System.out.println("Usted ingreso: " + comando + "\n");
					} catch (SQLException exception) {
						System.out.print("Ocurrio un error ejecutando el comando: \n");
						System.out.print(exception.getMessage());
						exception.printStackTrace();
					}

                } else if ("2".equals(opcion)) {
                    System.out.println("Saliendo de la aplicacion...");
                    break;

                } else {
                    System.out.println("Opción inválida, intente de nuevo. \n ");
                }

                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private static int ejecutarQuery(String comando) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		String url = "jdbc:mysql://localhost:3306/Escuela";
		int resultado = 0;
		
		try {
			connection = DriverManager.getConnection(url, USER, PASS);
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			resultado = statement.executeUpdate(comando);            
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
		return resultado;
		
	}

}
