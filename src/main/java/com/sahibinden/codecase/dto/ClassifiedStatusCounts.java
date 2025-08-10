package com.sahibinden.codecase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassifiedStatusCounts {
    long active;
    long deactivated;
    long pending;
    long duplicate;
}
