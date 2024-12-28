package pl.jakobczyk.gps.tracker.consumer.exception;

public class GpsSignalProcessingException extends RuntimeException {

    public GpsSignalProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
