package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusLogDto {
    private Status status;
    private Instant createdAt;
}
