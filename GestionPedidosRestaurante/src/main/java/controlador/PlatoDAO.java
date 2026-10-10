package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Plato;
import utilitarios.ConexionMySQL;

public class PlatoDAO {

    //Insertar
    public boolean insertar(Plato plato) {

        String sql = """
                INSERT INTO Plato
                (nombre, descripcion, precio, categoria, estado,
                 stock, imagen)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, plato.getNombre());
            ps.setString(2, plato.getDescripcion());
            ps.setDouble(3, plato.getPrecio());
            ps.setString(4, plato.getCategoria());
            ps.setString(5, plato.getEstado());
            ps.setInt(6, plato.getStock());
            ps.setString(7, plato.getImagen());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar plato: "
                    + e.getMessage()
            );

            return false;
        }
    }

    //Listar
    public List<Plato> listar() {

        List<Plato> lista = new ArrayList<>();

        String sql = """
                SELECT id, nombre, descripcion, precio,
                       categoria, estado, stock, imagen
                FROM Plato
                ORDER BY nombre
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Plato plato = new Plato();

                plato.setId(rs.getInt("id"));
                plato.setNombre(rs.getString("nombre"));
                plato.setDescripcion(
                        rs.getString("descripcion")
                );
                plato.setPrecio(
                        rs.getDouble("precio")
                );
                plato.setCategoria(
                        rs.getString("categoria")
                );
                plato.setEstado(
                        rs.getString("estado")
                );
                plato.setStock(
                        rs.getInt("stock")
                );
                plato.setImagen(
                        rs.getString("imagen")
                );

                lista.add(plato);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar platos: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    //Buscar por Id
    public Plato buscarPorId(int id) {

        String sql = """
                SELECT id, nombre, descripcion, precio,
                       categoria, estado, stock, imagen
                FROM Plato
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Plato plato = new Plato();

                    plato.setId(
                            rs.getInt("id")
                    );

                    plato.setNombre(
                            rs.getString("nombre")
                    );

                    plato.setDescripcion(
                            rs.getString("descripcion")
                    );

                    plato.setPrecio(
                            rs.getDouble("precio")
                    );

                    plato.setCategoria(
                            rs.getString("categoria")
                    );

                    plato.setEstado(
                            rs.getString("estado")
                    );

                    plato.setStock(
                            rs.getInt("stock")
                    );

                    plato.setImagen(
                            rs.getString("imagen")
                    );

                    return plato;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar plato: "
                    + e.getMessage()
            );
        }

        return null;
    }

    //Buscar por Nombre
    public List<Plato> buscarPorNombre(String nombre) {

        List<Plato> lista = new ArrayList<>();

        String sql = """
                SELECT id, nombre, descripcion, precio,
                       categoria, estado, stock, imagen
                FROM Plato
                WHERE nombre LIKE ?
                ORDER BY nombre
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Plato plato = new Plato();

                    plato.setId(
                            rs.getInt("id")
                    );

                    plato.setNombre(
                            rs.getString("nombre")
                    );

                    plato.setDescripcion(
                            rs.getString("descripcion")
                    );

                    plato.setPrecio(
                            rs.getDouble("precio")
                    );

                    plato.setCategoria(
                            rs.getString("categoria")
                    );

                    plato.setEstado(
                            rs.getString("estado")
                    );

                    plato.setStock(
                            rs.getInt("stock")
                    );

                    plato.setImagen(
                            rs.getString("imagen")
                    );

                    lista.add(plato);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar platos por nombre: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    //Actualizar
    public boolean actualizar(Plato plato) {

        String sql = """
                UPDATE Plato
                SET nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    categoria = ?,
                    estado = ?,
                    stock = ?,
                    imagen = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, plato.getNombre());
            ps.setString(2, plato.getDescripcion());
            ps.setDouble(3, plato.getPrecio());
            ps.setString(4, plato.getCategoria());
            ps.setString(5, plato.getEstado());
            ps.setInt(6, plato.getStock());
            ps.setString(7, plato.getImagen());
            ps.setInt(8, plato.getId());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar plato: "
                    + e.getMessage()
            );

            return false;
        }
    }

    //Eliminar
    public boolean eliminar(int id) {

        String sql = "DELETE FROM Plato WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar plato: "
                    + e.getMessage()
            );

            return false;
        }
    }

}
