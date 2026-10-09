
package service;

import controlador.MesaDAO;
import java.util.List;
import modelo.Mesa;

public class MesaService {

    private MesaDAO mesaDAO;

    public MesaService() {
        mesaDAO = new MesaDAO();
    }

    //registrar
    public boolean registrarMesa(int numero, int capacidad) {
        //aqui van las validaciones
        if (numero <= 0) {
            System.out.println("El número de mesa debe ser mayor que 0.");
            return false;
        }

        if (capacidad <= 0) {
            System.out.println("La capacidad debe ser mayor que 0.");
            return false;
        }
        
        List<Mesa> mesas = mesaDAO.listar();

        for (Mesa mesa : mesas) {

            if (mesa.getNumero() == numero) {
                System.out.println("El número de mesa ya existe.");
                return false;
            }
        }

        Mesa mesa = new Mesa();

        mesa.setNumero(numero);
        mesa.setCapacidad(capacidad);
        mesa.setEstado("DISPONIBLE");

        return mesaDAO.insertar(mesa);
    }

    //listar
    public List<Mesa> listarMesas() {

        return mesaDAO.listar();
    }

    //buscar
    public Mesa buscarMesa(int id) {

        return mesaDAO.buscarPorId(id);
    }

    //actualizar
    public boolean actualizarMesa(int id, int numero, int capacidad,
                                  String estado) {

        //validaciones
        if (id <= 0) {
            return false;
        }

        if (numero <= 0) {
            return false;
        }

        if (capacidad <= 0) {
            return false;
        }

        if (!estado.equals("DISPONIBLE")
                && !estado.equals("OCUPADA")) {

            System.out.println("Estado de mesa no válido.");
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(id);

        if (mesa == null) {
            System.out.println("La mesa no existe.");
            return false;
        }

        List<Mesa> mesas = mesaDAO.listar();

        for (Mesa otraMesa : mesas) {

            if (otraMesa.getNumero() == numero
                    && otraMesa.getId() != id) {

                System.out.println("El número de mesa ya está siendo utilizado.");
                return false;
            }
        }

        mesa.setNumero(numero);
        mesa.setCapacidad(capacidad);
        mesa.setEstado(estado);

        return mesaDAO.actualizar(mesa);
    }

    //eliminar
    public boolean eliminarMesa(int id) {

        if (id <= 0) {
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(id);

        if (mesa == null) {
            System.out.println("La mesa no existe.");
            return false;
        }

        if ("OCUPADA".equals(mesa.getEstado())) {

            System.out.println(
                "No se puede eliminar una mesa ocupada."
            );

            return false;
        }

        return mesaDAO.eliminar(id);
    }

    //ocupar
    public boolean ocuparMesa(int id) {

        Mesa mesa = mesaDAO.buscarPorId(id);

        if (mesa == null) {
            System.out.println("La mesa no existe.");
            return false;
        }

        if (!mesa.estaDisponible()) {
            System.out.println("La mesa no está disponible.");
            return false;
        }

        mesa.ocupar();

        return mesaDAO.actualizar(mesa);
    }

    //liberar
    public boolean liberarMesa(int id) {

        Mesa mesa = mesaDAO.buscarPorId(id);

        if (mesa == null) {
            System.out.println("La mesa no existe.");
            return false;
        }

        mesa.liberar();

        return mesaDAO.actualizar(mesa);
    }
}