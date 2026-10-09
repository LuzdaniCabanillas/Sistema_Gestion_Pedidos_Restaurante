
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import modelo.AtencionMesa;
import utilitarios.ConexionMySQL;

public class AtencionMesaDAO {
    
    //insetar
    public boolean insertar(AtencionMesa atencion) {

        String sql = """
                INSERT INTO AtencionMesa
                (mesa_id, mozo_id, fecha_inicio, fecha_fin, estado)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, atencion.getMesaId());
            ps.setInt(2, atencion.getMozoId());

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(atencion.getFechaInicio())
            );

            if (atencion.getFechaFin() != null) {
                ps.setTimestamp(
                        4,
                        Timestamp.valueOf(atencion.getFechaFin())
                );
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }

            ps.setString(5, atencion.getEstado());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar atención: "
                    + e.getMessage()
            );

            return false;
        }
    }

    //Listar
    public List<AtencionMesa> listar() {

        List<AtencionMesa> lista = new ArrayList<>();

        String sql = """
                SELECT id, mesa_id, mozo_id,
                       fecha_inicio, fecha_fin, estado
                FROM AtencionMesa
                ORDER BY id
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                AtencionMesa atencion = new AtencionMesa();

                atencion.setId(rs.getInt("id"));
                atencion.setMesaId(rs.getInt("mesa_id"));
                atencion.setMozoId(rs.getInt("mozo_id"));

                Timestamp fechaInicio =
                        rs.getTimestamp("fecha_inicio");

                if (fechaInicio != null) {
                    atencion.setFechaInicio(
                            fechaInicio.toLocalDateTime()
                    );
                }

                Timestamp fechaFin =
                        rs.getTimestamp("fecha_fin");

                if (fechaFin != null) {
                    atencion.setFechaFin(
                            fechaFin.toLocalDateTime()
                    );
                }

                atencion.setEstado(
                        rs.getString("estado")
                );

                lista.add(atencion);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar atenciones: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    //buscar
    public AtencionMesa buscarPorId(int id) {

        String sql = """
                SELECT id, mesa_id, mozo_id,
                       fecha_inicio, fecha_fin, estado
                FROM AtencionMesa
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    AtencionMesa atencion =
                            new AtencionMesa();

                    atencion.setId(
                            rs.getInt("id")
                    );

                    atencion.setMesaId(
                            rs.getInt("mesa_id")
                    );

                    atencion.setMozoId(
                            rs.getInt("mozo_id")
                    );

                    Timestamp fechaInicio =
                            rs.getTimestamp("fecha_inicio");

                    if (fechaInicio != null) {
                        atencion.setFechaInicio(
                                fechaInicio.toLocalDateTime()
                        );
                    }

                    Timestamp fechaFin =
                            rs.getTimestamp("fecha_fin");

                    if (fechaFin != null) {
                        atencion.setFechaFin(
                                fechaFin.toLocalDateTime()
                        );
                    }

                    atencion.setEstado(
                            rs.getString("estado")
                    );

                    return atencion;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar atención: "
                    + e.getMessage()
            );
        }

        return null;
    }

    //actualizar
    public boolean actualizar(AtencionMesa atencion) {

        String sql = """
                UPDATE AtencionMesa
                SET mesa_id = ?,
                    mozo_id = ?,
                    fecha_inicio = ?,
                    fecha_fin = ?,
                    estado = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, atencion.getMesaId());
            ps.setInt(2, atencion.getMozoId());

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            atencion.getFechaInicio()
                    )
            );

            if (atencion.getFechaFin() != null) {

                ps.setTimestamp(
                        4,
                        Timestamp.valueOf(
                                atencion.getFechaFin()
                        )
                );

            } else {

                ps.setNull(
                        4,
                        java.sql.Types.TIMESTAMP
                );
            }

            ps.setString(
                    5,
                    atencion.getEstado()
            );

            ps.setInt(
                    6,
                    atencion.getId()
            );

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar atención: "
                    + e.getMessage()
            );

            return false;
        }
    }

    //eliminar
    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM AtencionMesa WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar atención: "
                    + e.getMessage()
            );

            return false;
        }
    }
}
