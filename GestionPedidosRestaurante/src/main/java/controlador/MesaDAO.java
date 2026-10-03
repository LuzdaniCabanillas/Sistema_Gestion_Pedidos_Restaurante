
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Mesa;
import utilitarios.ConexionMySQL;

public class MesaDAO {
    
    //insertar
    public boolean insertar(Mesa mesa){
        
        String sql = "INSERT INTO Mesa (numero, capacidad, estado) "
                   + "VALUES (?, ?, ?)";
        
        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, mesa.getNumero());
            ps.setInt(2, mesa.getCapacidad());
            ps.setString(3, mesa.getEstado());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }
    
    //listar
    public List<Mesa> listar() {

        List<Mesa> lista = new ArrayList<>();

        String sql = "SELECT id, numero, capacidad, estado "
                   + "FROM Mesa ORDER BY numero";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Mesa mesa = new Mesa();

                mesa.setId(rs.getInt("id"));
                mesa.setNumero(rs.getInt("numero"));
                mesa.setCapacidad(rs.getInt("capacidad"));
                mesa.setEstado(rs.getString("estado"));

                lista.add(mesa);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
        }

        return lista;
    }
    
    
    //buscar por id-luz
    public Mesa buscarPorId(int id) {

        String sql = "SELECT id, numero, capacidad, estado "
                   + "FROM Mesa WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Mesa mesa = new Mesa();

                    mesa.setId(rs.getInt("id"));
                    mesa.setNumero(rs.getInt("numero"));
                    mesa.setCapacidad(rs.getInt("capacidad"));
                    mesa.setEstado(rs.getString("estado"));

                    return mesa;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar mesa: " + e.getMessage());
        }

        return null;
    }
    
    //actualizar
    public boolean actualizar(Mesa mesa) {

        String sql = "UPDATE Mesa "
                   + "SET numero = ?, capacidad = ?, estado = ? "
                   + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, mesa.getNumero());
            ps.setInt(2, mesa.getCapacidad());
            ps.setString(3, mesa.getEstado());
            ps.setInt(4, mesa.getId());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }
    
    //Eliminar
     public boolean eliminar(int id) {

        String sql = "DELETE FROM Mesa WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar mesa: " + e.getMessage());
            return false;
        }
    }
}
