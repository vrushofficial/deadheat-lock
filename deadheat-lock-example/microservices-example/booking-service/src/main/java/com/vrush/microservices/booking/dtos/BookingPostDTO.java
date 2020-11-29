package com.vrush.microservices.booking.dtos;

import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.vrush.microservices.booking.annotations.ValidUUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPostDTO {

    @NotNull(message = "ID Room is required")
    @ValidUUID
    private String idRoom;

    @NotNull(message = "Start Date is required")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    private LocalDate endDate;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid Email")
    private String guestEmail;

    @NotNull(message = "Payment information is required")
    private String creditCardNumberEncrypted;

    @NotNull(message = "Payment information is required")
    private String expireDateEncrypted;

    @NotNull(message = "Payment information is required")
    private String ccvEncrypted;

    public UUID getIdRoom() {
        return UUID.fromString(idRoom);
    }
}
