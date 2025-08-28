import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PruebaAccesoBD
{
	public static void main (String[] args)
	{
		try
		{
			Properties props = new Properties();
			try (InputStream input = PruebaAccesoBD.class.getClassLoader().getResourceAsStream("config.properties");) {
				props.load(input);
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			String driver = props.getProperty("driver");
			String url = props.getProperty("url");
			String user = props.getProperty("user");
			String password = props.getProperty("password");
			/* primer programa de prueba para ejemplo de acceso a MySQL desde Java */
			/* accede a una base de datos de MySQL llamada Prueba que contiene una tabla llamada Personas */
			/* dentro de dicha tabla hay una columna llamada nombre */

			/* 1. cargo dinamicamente el driver de MySQL */
			Class.forName(driver);

			/* 2. una vez cargado el driver, me conecto con la base de datos */
			Connection con = DriverManager.getConnection(url, user, password);

			/* 3. creo un PreparedStatement para insertar una persona en base de datos */
			String insert = "INSERT INTO Personas VALUES (?)";
			PreparedStatement pstmt = con.prepareStatement(insert);
			pstmt.setString(1, "Leandro");

			/* 4. ejecuto la sentencia de insercion y cierro el PreparedStatement */
			int cant = pstmt.executeUpdate();
			pstmt.close();
			System.out.print("Resultado de " + insert + ": ");
			System.out.println(cant + " filas afectadas");

			/* 5. creo un Statement para listar todas las personas de la base de datos */
			Statement stmt = con.createStatement();
			String query = "SELECT * FROM Personas";

			/* 6. ejecuto la consulta, listo las personas y cierro el ResultSet y el Statement */
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("Resultado de " + query);
			while (rs.next())
			{
				System.out.println("Nombre = " + rs.getString("nombre").trim());
			}
			rs.close();
			stmt.close();

			/* 7. por ultimo, cierro la conexion con la base de datos */
			con.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
	}
}
