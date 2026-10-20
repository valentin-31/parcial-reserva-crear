package reserva.usecase;

import reserva.exception.PersistenceException;
import reserva.exception.ReservationAlreadyExistsException;
import reserva.modelo.Reserva;
import reserva.output.ReservationRepository;

import java.time.LocalDate;
import java.util.UUID;

public class CreateReservationUseCase {

    private final ReservationRepository repository;

    public CreateReservationUseCase(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reserva createReservation(String emailContacto, LocalDate fecha, int cantidadPersonas) {
        Reserva reserva = Reserva.instance(UUID.randomUUID(), emailContacto, fecha, cantidadPersonas);
        if (repository.existsActiveByEmailContactoAndFecha(emailContacto, fecha)) {
            throw new ReservationAlreadyExistsException("Ya existe una reserva activa para ese correo y fecha");
        }
        if (!repository.saveReserva(reserva)) {
            throw new PersistenceException("No se pudo guardar la reserva");
        }
        return reserva;
    }
}
