package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.validation.NoBadWords;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewClassifiedDto {
    @NotBlank
    @Size(min = 10, max = 50)
    @Pattern(regexp = "^[\\p{L}\\p{Nd}].*", message = "Title must start with a letter or digit")
    @NoBadWords
    private final String title;

    @NotBlank
    @Size(min = 20, max = 200)
    @NoBadWords
    private final String detail;

    @NotNull
    private final String category;
}
