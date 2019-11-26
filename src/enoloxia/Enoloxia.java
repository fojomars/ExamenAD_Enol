/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enoloxia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author oracle
 */
public class Enoloxia {

    public static Connection conexion = null;
    Connection conn;

    // Método para establecer una conexión
    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;

        conexion = DriverManager.getConnection(ulrjdbc);
        return conexion;
    }

    // Método para cerrar la conexión
    public static void closeConexion() throws SQLException {
        conexion.close();
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
        BufferedReader leer = new BufferedReader(new FileReader(new File("//home/oracle/Desktop/compartido/Examen3/analisis.txt")));
        getConexion();
        String f = "";
        while ((f = leer.readLine()) != null) {
            String[] campos = f.split(",");
            String num = campos[0];
            String tipo = campos[4];
            int acidez = Integer.parseInt(campos[1]);
            int cantidad = Integer.parseInt(campos[5]);
            int total = cantidad * 15;
            String dni = campos[6];

            String nomeUva = "";
            String tratacidez = "";
            int numAnalisis = 0;

            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("select nomeu,acidezmin,acidezmax from uvas where tipo = '" + tipo + "'");

            while (rs.next()) {
                nomeUva = rs.getString("nomeu");
                int acidezmin = rs.getInt("acidezmin");
                int acidezmax = rs.getInt("acidezmax");

                if (acidez > acidezmax) {
                    tratacidez = "Baixar acidez";
                } else if (acidez < acidezmin) {
                    tratacidez = "Subir acidez";
                } else {
                    tratacidez = "Acidez correcta";
                }
            }

            rs.close();
            rs = st.executeQuery("insert into xerado values('" + num + "', '" + nomeUva + "', '" + tratacidez + "', " + total + ")");

            Statement st2 = conexion.createStatement();
            ResultSet rs2 = st.executeQuery("select numerodeanalisis from clientes where dni = '" + dni + "'");

            while (rs2.next()) {
                numAnalisis = rs2.getInt("numerodeanalisis");

            }
            rs2.close();
            rs2 = st.executeQuery("update clientes set numerodeanalisis = '" + (numAnalisis + 1) + "' where dni = '" + dni + "'");

        }
    }

}
