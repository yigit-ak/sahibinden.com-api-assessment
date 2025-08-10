package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDto {
    @NotNull
    private Status status;
}
