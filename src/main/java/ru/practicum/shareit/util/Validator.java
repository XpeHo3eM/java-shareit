package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.model.StateType;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@UtilityClass
public class Validator {
    public void validateBooking(CreatingBookingDto booking) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (start.isEqual(end) || start.isAfter(end)) {
            throw new ValidationException("Начало бронирования должно быть раньше окончания");
        }
    }

    public void validateStatusType(String state) {
        try {
            StateType.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public void validatePage(long from, long size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException(String.format("Параметры страницы некорректны from: %s, size: %s", from, size));
        }
    }
}
