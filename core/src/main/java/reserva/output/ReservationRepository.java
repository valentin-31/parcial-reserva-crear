package reserva.output;

import reserva.modelo.Reserva;

import java.time.LocalDate;

public interface ReservationRepository {

    boolean existsActiveByEmailContactoAndFecha(String emailContacto, LocalDate fecha);

    boolean saveReserva(Reserva reserva);
}
