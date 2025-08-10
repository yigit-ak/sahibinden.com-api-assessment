package com.sahibinden.codecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(
        name = "ClassifiedDto",
        description = "Read model for a classified ad."
)
@Data
@Builder
public class ClassifiedDto {

    @Schema(description = "Unique identifier of the classified.", example = "123", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Title of the classified.", example = "iPhone 13 Pro Max 128GB")
    private String title;

    @Schema(description = "Detailed description.", example = "Excellent condition, single owner, original box and charger included.")
    private String detail;

    @Schema(
            description = "Category (enum as string).",
            example = "SHOPPING",
            allowableValues = {"AUTOMOTIVE", "REAL_ESTATE", "SHOPPING", "OTHER"}
    )
    private String category;

    @Schema(
            description = "Current moderation/publication status (enum as string).",
            example = "PENDING_APPROVAL",
            allowableValues = {"PENDING_APPROVAL", "ACTIVE", "DEACTIVATED", "DUPLICATE"},
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String status;
}
