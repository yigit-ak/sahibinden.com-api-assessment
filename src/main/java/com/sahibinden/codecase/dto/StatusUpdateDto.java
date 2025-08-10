package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Schema(
        name = "StatusUpdateDto",
        description = "Payload to update the status of a classified."
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDto {

    @Schema(
            description = "New status to set.",
            example = "ACTIVE",
            required = true,
            allowableValues = {"PENDING_APPROVAL", "ACTIVE", "DEACTIVATED", "DUPLICATE"}
    )
    @NotNull
    private Status status;
}
