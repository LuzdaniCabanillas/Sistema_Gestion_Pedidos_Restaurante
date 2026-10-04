
package service;

import controlador.AtencionMesaDAO;
import controlador.MesaDAO;
import java.time.LocalDateTime;
import java.util.List;
import modelo.AtencionMesa;
import modelo.Mesa;

public class AtencionMesaService {
    

    private AtencionMesaDAO atencionDAO;
    private MesaDAO mesaDAO;

    public AtencionMesaService() {

        atencionDAO = new AtencionMesaDAO();
        mesaDAO = new MesaDAO();
    }

    public boolean iniciarAtencion(int mesaId, int mozoId) {

        if (mesaId <= 0) {
            System.out.println(
                    "El ID de mesa no es válido."
            );
            return false;
        }

        if (mozoId <= 0) {
            System.out.println(
                    "El ID del mozo no es válido."
            );
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(mesaId);

        if (mesa == null) {

            System.out.println(
                    "La mesa no existe."
            );

            return false;
        }

        if (!mesa.estaDisponible()) {

            System.out.println(
                    "La mesa no está disponible."
            );

            return false;
        }

        List<AtencionMesa> atenciones =
                atencionDAO.listar();

        for (AtencionMesa atencion : atenciones) {

            if (atencion.getMesaId() == mesaId
                    && "ACTIVA".equals(
                            atencion.getEstado())) {

                System.out.println(
                        "La mesa ya tiene una atención activa."
                );

                return false;
            }
        }

        AtencionMesa atencion =
                new AtencionMesa();

        atencion.setMesaId(mesaId);
        atencion.setMozoId(mozoId);
        atencion.setFechaInicio(
                LocalDateTime.now()
        );
        atencion.setFechaFin(null);
        atencion.setEstado("ACTIVA");

        boolean registrada =
                atencionDAO.insertar(atencion);

        if (!registrada) {
            return false;
        }

        mesa.ocupar();

        return mesaDAO.actualizar(mesa);
    }

    public List<AtencionMesa> listarAtenciones() {

        return atencionDAO.listar();
    }

    public AtencionMesa buscarAtencion(int id) {

        return atencionDAO.buscarPorId(id);
    }

    public boolean finalizarAtencion(int id) {

        AtencionMesa atencion =
                atencionDAO.buscarPorId(id);

        if (atencion == null) {

            System.out.println(
                    "La atención no existe."
            );

            return false;
        }

        if (!"ACTIVA".equals(
                atencion.getEstado())) {

            System.out.println(
                    "La atención no está activa."
            );

            return false;
        }

        atencion.finalizarAtencion();

        boolean actualizada =
                atencionDAO.actualizar(atencion);

        if (!actualizada) {
            return false;
        }

        Mesa mesa =
                mesaDAO.buscarPorId(
                        atencion.getMesaId()
                );

        if (mesa != null) {

            mesa.liberar();

            mesaDAO.actualizar(mesa);
        }

        return true;
    }

    public boolean actualizarAtencion(
            int id,
            int mesaId,
            int mozoId) {

        AtencionMesa atencion =
                atencionDAO.buscarPorId(id);

        if (atencion == null) {

            System.out.println(
                    "La atención no existe."
            );

            return false;
        }

        if (mesaId <= 0 || mozoId <= 0) {
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(mesaId);

        if (mesa == null) {

            System.out.println(
                    "La mesa no existe."
            );

            return false;
        }

        atencion.setMesaId(mesaId);
        atencion.setMozoId(mozoId);

        return atencionDAO.actualizar(atencion);
    }

    public boolean eliminarAtencion(int id) {

        AtencionMesa atencion =
                atencionDAO.buscarPorId(id);

        if (atencion == null) {

            System.out.println(
                    "La atención no existe."
            );

            return false;
        }

        if ("ACTIVA".equals(
                atencion.getEstado())) {

            System.out.println(
                    "No se puede eliminar una atención activa."
            );

            return false;
        }

        return atencionDAO.eliminar(id);
    }
    
    public List<Mesa> listarMesas() {
        return mesaDAO.listar();
    }

    public Mesa buscarMesa(int mesaId) {
       return mesaDAO.buscarPorId(mesaId);
    }
}
