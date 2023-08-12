package ru.practicum.shareit.exception;

public class NotAvailableBookingException extends RuntimeException {
    public NotAvailableBookingException(String message) {
        super(message);
    }
}
