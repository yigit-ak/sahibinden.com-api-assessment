package com.sahibinden.codecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(
        name = "ClassifiedStatusCounts",
        description = "Aggregated counts of classifieds per status."
)
@Data
@Builder
public class ClassifiedStatusCounts {

    @Schema(description = "Number of ACTIVE classifieds.", example = "42")
    long active;

    @Schema(description = "Number of DEACTIVATED classifieds.", example = "7")
    long deactivated;

    @Schema(description = "Number of PENDING_APPROVAL classifieds.", example = "13")
    long pending;

    @Schema(description = "Number of DUPLICATE classifieds detected.", example = "3")
    long duplicate;
}
