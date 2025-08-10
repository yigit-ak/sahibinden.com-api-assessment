package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(
        name = "StatusLogDto",
        description = "Single entry in the status change history of a classified."
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusLogDto {

    @Schema(
            description = "Status after the change.",
            example = "ACTIVE",
            allowableValues = {"PENDING_APPROVAL", "ACTIVE", "DEACTIVATED", "DUPLICATE"}
    )
    private Status status;

    @Schema(
            description = "Timestamp of the change (UTC).",
            type = "string",
            format = "date-time",
            example = "2025-08-10T16:14:32Z"
    )
    private Instant createdAt;
}
