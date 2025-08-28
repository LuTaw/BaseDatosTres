package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Main {

	private static String USER = "root";
	private static String PASS = "root";

	public static void main(String[] args) {
		Connection connection = null;
		Statement statement = null;
		String url = "jdbc:mysql://localhost:3306";
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        String opcion = "";
        try {
            while (true) {
                // Desplegar menú
                System.out.println("=== Menu Principal ===");
                System.out.println("1. Desplegar catalogo base de datos.");
                System.out.println("2. Salir");
                System.out.print("Seleccione una opcion: \n");

                opcion = bufferReader.readLine();  // lee la opcion ingresada

                if ("1".equals(opcion)) {
                    try {
                    	desplegarBaseDatos(connection, statement, url);
					} catch (SQLException exception) {
						System.out.print("Ocurrio un error al ejecutar el stored procedure: \n");
						System.out.print(exception.getMessage());
						exception.printStackTrace();
					}

                } else if ("2".equals(opcion)) {
                    System.out.println("Saliendo de la aplicacion...");
                    break;

                } else {
                    System.out.println("Opcion invalida, intente de nuevo. \n ");
                }

                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private static void desplegarBaseDatos(Connection connection, Statement statement, String url) throws SQLException {		
        try {
        	connection = DriverManager.getConnection(url, USER, PASS);
            // Se obtiene metadata de la conexión
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
        	// Mostrar menú de bases de datos
        	int index = 1;
        	Map<Integer, String> dbMap = new HashMap<>();
        	System.out.println("Bases de datos disponibles:");
        	while (resultSet.next()) {
        		String dbName = resultSet.getString("TABLE_CAT");
        		System.out.println(index + ") " + dbName);
        		dbMap.put(index, dbName);
        		index++;
        	}

        	// Leer opción del usuario
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        	int opcion = 0;
        	while (true) {
        		System.out.print("Ingrese el numero de la base de datos a usar: ");
        		try {
        			try {
						opcion = Integer.parseInt(br.readLine());
					} catch (IOException exception) {
						exception.printStackTrace();
					}
        			if (dbMap.containsKey(opcion)) {
        				break;
        			} else {
        				System.out.println("Opcion invalida. Intente nuevamente.");
        			}
        		} catch (NumberFormatException e) {
        			System.out.println("Ingrese un numero valido.");
        		}
        	}

        	String dbSeleccionada = dbMap.get(opcion);
        	System.out.println("Usando base de datos: " + dbSeleccionada);

        	// Crear nueva conexión a la base de datos seleccionada
        	String urlDb = "jdbc:mysql://localhost:3306/" + dbSeleccionada;
        	try (Connection connectionDbSeleccionada = DriverManager.getConnection(urlDb, USER, PASS)) {
                DatabaseMetaData metaDataDbSeleccionada = connectionDbSeleccionada.getMetaData();
        		System.out.println("Conexion exitosa a " + dbSeleccionada);
        		desplegarTablasDataBaseSeleccionada(metaDataDbSeleccionada);
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
	}

	private static void desplegarTablasDataBaseSeleccionada(DatabaseMetaData metaData) throws SQLException {
		ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
		while (tables.next()) {
		    String tableName = tables.getString("TABLE_NAME");
		    System.out.println("Tabla: " + tableName);
		    desplegarColumnasTabla(metaData, tableName);
		    desplegarClavesPrimarias(metaData, tableName);
		    desplegarClavesForaneas(metaData, tableName);
		}
	}

	private static void desplegarClavesForaneas(DatabaseMetaData metaData, String tableName) throws SQLException {
		ResultSet foreignKey = metaData.getImportedKeys(null, null, tableName);
		while (foreignKey.next()) {
		    String foreignKeyColumn = foreignKey.getString("FKCOLUMN_NAME");
		    String primaryKeyTable = foreignKey.getString("PKTABLE_NAME");
		    String primaryKeyColumn = foreignKey.getString("PKCOLUMN_NAME");
		    System.out.println("\tFOREIGN KEY: " + foreignKeyColumn + " -> " + primaryKeyTable + "(" + primaryKeyColumn + ")");
		}
	}

	private static void desplegarClavesPrimarias(DatabaseMetaData metaData, String tableName) throws SQLException {
		ResultSet primaryKey = metaData.getPrimaryKeys(null, null, tableName);
		System.out.print("\tPRIMARY KEY: ");
		while (primaryKey.next()) {
		    String primaryKeyColumn = primaryKey.getString("COLUMN_NAME");
		    System.out.print(primaryKeyColumn + " ");
		}
		System.out.println();
	}

	private static void desplegarColumnasTabla(DatabaseMetaData metaData, String tableName) throws SQLException {
		ResultSet columns = metaData.getColumns(null, null, tableName, "%");
		while (columns.next()) {
		    String columnName = columns.getString("COLUMN_NAME");
		    String dataType = columns.getString("TYPE_NAME");
		    int size = columns.getInt("COLUMN_SIZE");
		    String nullable = columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable ? "NULL" : "NOT NULL";
		    System.out.println("\t" + columnName + " " + dataType + "(" + size + ") " + nullable);
		}
	}

}