package reserva.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reserva.exception.InvalidReservaException;
import reserva.exception.PersistenceException;
import reserva.exception.ReservationAlreadyExistsException;
import reserva.modelo.EstadoReserva;
import reserva.modelo.Reserva;
import reserva.output.ReservationRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Caminos de CreateReservationUseCase y test que los cubre:
 * - datos inválidos (no consulta ni guarda)   -> createReservation_datosInvalidos_...
 * - ya existe reserva activa (mismo email+fecha) -> createReservation_reservaDuplicada_...
 * - falla al guardar                          -> createReservation_falloAlGuardar_...
 * - registro exitoso                          -> createReservation_datosValidos_...
 */
@ExtendWith(MockitoExtension.class)
public class CreateReservationUseCaseTest {

    private static final String EMAIL_VALIDO = "cliente@correo.com";
    private static final LocalDate FECHA_VALIDA = LocalDate.now().plusYears(1);
    private static final int CANTIDAD_VALIDA = 4;

    @Mock
    private ReservationRepository repository;

    // Probamos que con datos inválidos se lanza la excepción de la entidad y nunca se consulta ni se guarda
    @Test
    void createReservation_datosInvalidos_throwsInvalidReservaExceptionYNoGuarda() {
        // Arrange
        CreateReservationUseCase useCase = new CreateReservationUseCase(repository);
        String emailInvalido = "";

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> useCase.createReservation(emailInvalido, FECHA_VALIDA, CANTIDAD_VALIDA));
        verify(repository, never()).existsActiveByEmailContactoAndFecha(anyString(), any(LocalDate.class));
        verify(repository, never()).saveReserva(any(Reserva.class));
    }

    // Probamos que si ya existe una reserva activa para el mismo email y fecha se lanza ReservationAlreadyExistsException y no se guarda
    @Test
    void createReservation_reservaDuplicada_throwsReservationAlreadyExistsExceptionYNoGuarda() {
        // Arrange
        CreateReservationUseCase useCase = new CreateReservationUseCase(repository);
        when(repository.existsActiveByEmailContactoAndFecha(EMAIL_VALIDO, FECHA_VALIDA)).thenReturn(true);

        // Act + Assert
        assertThrows(ReservationAlreadyExistsException.class,
                () -> useCase.createReservation(EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA));
        verify(repository, never()).saveReserva(any(Reserva.class));
    }

    // Probamos que si el repositorio no puede guardar se lanza PersistenceException
    @Test
    void createReservation_falloAlGuardar_throwsPersistenceException() {
        // Arrange
        CreateReservationUseCase useCase = new CreateReservationUseCase(repository);
        when(repository.existsActiveByEmailContactoAndFecha(EMAIL_VALIDO, FECHA_VALIDA)).thenReturn(false);
        when(repository.saveReserva(any(Reserva.class))).thenReturn(false);

        // Act + Assert
        assertThrows(PersistenceException.class,
                () -> useCase.createReservation(EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA));
    }

    // Probamos que con datos válidos y sin duplicado se guarda y se devuelve la reserva creada, ACTIVA
    @Test
    void createReservation_datosValidos_guardaYDevuelveReservaActiva() {
        // Arrange
        CreateReservationUseCase useCase = new CreateReservationUseCase(repository);
        when(repository.existsActiveByEmailContactoAndFecha(EMAIL_VALIDO, FECHA_VALIDA)).thenReturn(false);
        when(repository.saveReserva(any(Reserva.class))).thenReturn(true);

        // Act
        Reserva resultado = useCase.createReservation(EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA);

        // Assert
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(repository).saveReserva(captor.capture());
        assertSame(captor.getValue(), resultado);
        assertNotNull(resultado.getId());
        assertEquals(EMAIL_VALIDO, resultado.getEmailContacto());
        assertEquals(FECHA_VALIDA, resultado.getFecha());
        assertEquals(CANTIDAD_VALIDA, resultado.getCantidadPersonas());
        assertEquals(EstadoReserva.ACTIVA, resultado.getEstado());
    }
}
