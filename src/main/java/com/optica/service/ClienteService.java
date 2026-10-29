package com.optica.service;

import com.optica.dao.ApartadoDAO;
import com.optica.dao.AroDAO;
import com.optica.dao.ClienteDAO;
import com.optica.dao.MedicionDAO;
import com.optica.dto.MedicionRequest;
import com.optica.exception.ConflictoException;
import com.optica.exception.NegocioException;
import com.optica.exception.RecursoNoEncontradoException;
import com.optica.model.Apartado;
import com.optica.model.Aro;
import com.optica.model.Cliente;
import com.optica.model.EstadoApartado;
import com.optica.model.Medicion;
import org.springframework.stereotype.Service;

import java.util.List;

/** Operaciones sobre clientes: perfil, mediciones y apartados de aros. */
@Service
public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final MedicionDAO medicionDAO;
    private final ApartadoDAO apartadoDAO;
    private final AroDAO aroDAO;

    public ClienteService(ClienteDAO clienteDAO, MedicionDAO medicionDAO, ApartadoDAO apartadoDAO, AroDAO aroDAO) {
        this.clienteDAO = clienteDAO;
        this.medicionDAO = medicionDAO;
        this.apartadoDAO = apartadoDAO;
        this.aroDAO = aroDAO;
    }

    public Cliente obtener(Long clienteId) {
        return clienteDAO.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", clienteId));
    }

    public List<Cliente> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return clienteDAO.findAll();
        }
        return clienteDAO.buscar(texto);
    }

    public List<Medicion> mediciones(Long clienteId) {
        obtener(clienteId);
        return medicionDAO.findByCliente(clienteId);
    }

    /** El vendedor registra la receta que luego el cliente consulta en su historial. */
    public Medicion registrarMedicion(Long clienteId, Long vendedorId, MedicionRequest req) {
        obtener(clienteId);
        Medicion m = req.toModel();
        m.setClienteId(clienteId);
        m.setVendedorId(vendedorId);
        return medicionDAO.save(m);
    }

    public List<Apartado> apartados(Long clienteId) {
        return apartadoDAO.findByCliente(clienteId);
    }

    public Apartado apartarAro(Long clienteId, Long aroId) {
        Aro aro = aroDAO.findById(aroId)
                .filter(Aro::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aro", aroId));
        if (aro.getStock() <= 0) {
            throw new NegocioException("El aro seleccionado no tiene stock disponible");
        }
        if (apartadoDAO.findPendiente(clienteId, aroId).isPresent()) {
            throw new ConflictoException("Ya tienes este aro apartado");
        }
        Apartado ap = new Apartado();
        ap.setClienteId(clienteId);
        ap.setAroId(aroId);
        ap.setEstado(EstadoApartado.PENDIENTE);
        apartadoDAO.save(ap);
        ap.setAro(aro);
        return ap;
    }

    public void cancelarApartado(Long clienteId, Long apartadoId) {
        Apartado ap = apartadoDAO.findById(apartadoId)
                .filter(a -> a.getClienteId().equals(clienteId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Apartado", apartadoId));
        if (ap.getEstado() != EstadoApartado.PENDIENTE) {
            throw new NegocioException("Solo se pueden cancelar apartados pendientes");
        }
        apartadoDAO.actualizarEstado(apartadoId, EstadoApartado.CANCELADO);
    }
}
