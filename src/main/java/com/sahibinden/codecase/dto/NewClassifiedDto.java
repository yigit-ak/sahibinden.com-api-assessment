package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.validation.NoBadWords;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@Schema(
        name = "NewClassifiedDto",
        description = "Payload to create a new classified ad."
)

public class NewClassifiedDto {

    @Schema(
            description = "Short, descriptive title of the classified.",
            example = "iPhone 13 Pro Max 128GB",
            minLength = 10, maxLength = 50
    )
    @NotBlank
    @Size(min = 10, max = 50)
    @Pattern(regexp = "^[\\p{L}\\p{Nd}].*", message = "Title must start with a letter or digit")
    @NoBadWords
    private final String title;

    @Schema(
            description = "Detailed description of the item/service.",
            example = "Excellent condition, single owner, original box and charger included.",
            minLength = 20, maxLength = 200
    )
    @NotBlank
    @Size(min = 20, max = 200)
    @NoBadWords
    private final String detail;

    @Schema(
            description = "Category of the classified (enum as string).",
            example = "SHOPPING",
            allowableValues = {"AUTOMOTIVE","REAL_ESTATE","SHOPPING","OTHER"},
            required = true
    )
    @NotNull
    private final String category;
}
