package reserva.exception;

public class ReservaAlreadyCancelledException extends RuntimeException {
    public ReservaAlreadyCancelledException(String message) {
        super(message);
    }
}
