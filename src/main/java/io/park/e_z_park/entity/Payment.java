package io.park.e_z_park.entity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;


@Document
@Getter
@Setter
public class Payment {

    @Id
    private String id;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal amount;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    @Size(max = 20)
    private String method;

    @Size(max = 100)
    private String transactionId;

    private LocalDateTime paidAt;

    @DocumentReference(lazy = true)
    @NotNull
    private Reservation reservation;

}
