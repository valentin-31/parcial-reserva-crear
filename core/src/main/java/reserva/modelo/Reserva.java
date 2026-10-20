package reserva.modelo;

import reserva.exception.InvalidReservaException;
import reserva.exception.ReservaAlreadyCancelledException;

import java.time.LocalDate;
import java.util.UUID;

public class Reserva {

    private final UUID id;
    private final String emailContacto;
    private final LocalDate fecha;
    private final int cantidadPersonas;
    private EstadoReserva estado;

    private Reserva(UUID id, String emailContacto, LocalDate fecha, int cantidadPersonas) {
        this.id = id;
        this.emailContacto = emailContacto;
        this.fecha = fecha;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = EstadoReserva.ACTIVA;
    }

    public static Reserva instance(UUID id, String emailContacto, LocalDate fecha, int cantidadPersonas) {
        if (id == null) {
            throw new InvalidReservaException("El id es obligatorio");
        }
        if (fecha == null) {
            throw new InvalidReservaException("La fecha es obligatoria");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new InvalidReservaException("La fecha no puede ser pasada");
        }
        if (emailContacto == null || emailContacto.isBlank()) {
            throw new InvalidReservaException("El email de contacto es obligatorio");
        }
        if (cantidadPersonas <= 0) {
            throw new InvalidReservaException("La cantidad de personas debe ser mayor que cero");
        }
        return new Reserva(id, emailContacto, fecha, cantidadPersonas);
    }

    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA) {
            throw new ReservaAlreadyCancelledException("La reserva ya está cancelada");
        }
        estado = EstadoReserva.CANCELADA;
    }

    public UUID getId() {
        return id;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
