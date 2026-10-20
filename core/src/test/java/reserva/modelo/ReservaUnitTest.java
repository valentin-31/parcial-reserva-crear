package reserva.modelo;

import org.junit.jupiter.api.Test;
import reserva.exception.InvalidReservaException;
import reserva.exception.ReservaAlreadyCancelledException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Restricciones del enunciado y test que las cubre:
 * - id obligatorio                      -> instance_idNull_throws...
 * - fecha obligatoria                   -> instance_fechaNull_throws...
 * - fecha no puede ser pasada (propia)  -> instance_fechaPasada_throws... / instance_fechaHoy_...
 * - email obligatorio y no vacío        -> instance_emailNull/Vacio/SoloEspacios_throws...
 * - cantidadPersonas mayor que cero     -> instance_cantidadPersonasCero/Negativa_throws...
 * - reserva nueva comienza ACTIVA       -> instance_datosValidos_...
 * - reserva cancelada no se re-cancela  -> cancelar_...
 */
public class ReservaUnitTest {

    private static final UUID ID_VALIDO = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String EMAIL_VALIDO = "cliente@correo.com";
    private static final LocalDate FECHA_VALIDA = LocalDate.now().plusYears(1);
    private static final int CANTIDAD_VALIDA = 4;

    // Probamos que con todos los datos válidos se crea la reserva, con sus getters y estado ACTIVA
    @Test
    void instance_datosValidos_creaReservaActiva() {
        // Arrange (datos válidos en las constantes)

        // Act
        Reserva reserva = Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA);

        // Assert
        assertNotNull(reserva);
        assertEquals(ID_VALIDO, reserva.getId());
        assertEquals(EMAIL_VALIDO, reserva.getEmailContacto());
        assertEquals(FECHA_VALIDA, reserva.getFecha());
        assertEquals(CANTIDAD_VALIDA, reserva.getCantidadPersonas());
        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
    }

    // Probamos que un id null lanza la excepción de reserva inválida
    @Test
    void instance_idNull_throwsInvalidReservaException() {
        // Arrange
        UUID id = null;

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(id, EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA));
    }

    // Probamos que una fecha null lanza la excepción de reserva inválida
    @Test
    void instance_fechaNull_throwsInvalidReservaException() {
        // Arrange
        LocalDate fecha = null;

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, EMAIL_VALIDO, fecha, CANTIDAD_VALIDA));
    }

    // Probamos que una fecha pasada (ayer) lanza la excepción de reserva inválida
    @Test
    void instance_fechaPasada_throwsInvalidReservaException() {
        // Arrange
        LocalDate ayer = LocalDate.now().minusDays(1);

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, EMAIL_VALIDO, ayer, CANTIDAD_VALIDA));
    }

    // Probamos el borde válido: una reserva para hoy se crea
    @Test
    void instance_fechaHoy_creaReserva() {
        // Arrange
        LocalDate hoy = LocalDate.now();

        // Act
        Reserva reserva = Reserva.instance(ID_VALIDO, EMAIL_VALIDO, hoy, CANTIDAD_VALIDA);

        // Assert
        assertEquals(hoy, reserva.getFecha());
    }

    // Probamos que un email null lanza la excepción de reserva inválida
    @Test
    void instance_emailNull_throwsInvalidReservaException() {
        // Arrange
        String email = null;

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, email, FECHA_VALIDA, CANTIDAD_VALIDA));
    }

    // Probamos que un email vacío ("") lanza la excepción de reserva inválida
    @Test
    void instance_emailVacio_throwsInvalidReservaException() {
        // Arrange
        String email = "";

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, email, FECHA_VALIDA, CANTIDAD_VALIDA));
    }

    // Probamos que un email solo con espacios ("   ") lanza la excepción de reserva inválida
    @Test
    void instance_emailSoloEspacios_throwsInvalidReservaException() {
        // Arrange
        String email = "   ";

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, email, FECHA_VALIDA, CANTIDAD_VALIDA));
    }

    // Probamos que cantidadPersonas = 0 lanza la excepción de reserva inválida
    @Test
    void instance_cantidadPersonasCero_throwsInvalidReservaException() {
        // Arrange
        int cantidad = 0;

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, cantidad));
    }

    // Probamos que cantidadPersonas negativa lanza la excepción de reserva inválida
    @Test
    void instance_cantidadPersonasNegativa_throwsInvalidReservaException() {
        // Arrange
        int cantidad = -1;

        // Act + Assert
        assertThrows(InvalidReservaException.class,
                () -> Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, cantidad));
    }

    // Probamos el borde válido: cantidadPersonas = 1 crea la reserva
    @Test
    void instance_cantidadPersonasUna_creaReserva() {
        // Arrange
        int cantidad = 1;

        // Act
        Reserva reserva = Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, cantidad);

        // Assert
        assertEquals(1, reserva.getCantidadPersonas());
    }

    // Probamos que cancelar una reserva ACTIVA la pasa a CANCELADA
    @Test
    void cancelar_reservaActiva_pasaACancelada() {
        // Arrange
        Reserva reserva = Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA);

        // Act
        reserva.cancelar();

        // Assert
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    // Probamos que cancelar una reserva ya CANCELADA lanza la excepción de reserva ya cancelada
    @Test
    void cancelar_reservaCancelada_throwsReservaAlreadyCancelledException() {
        // Arrange
        Reserva reserva = Reserva.instance(ID_VALIDO, EMAIL_VALIDO, FECHA_VALIDA, CANTIDAD_VALIDA);
        reserva.cancelar();

        // Act + Assert
        assertThrows(ReservaAlreadyCancelledException.class, reserva::cancelar);
    }
}
