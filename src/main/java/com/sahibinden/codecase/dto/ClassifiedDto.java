package com.sahibinden.codecase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassifiedDto {
    private long id;
    private String title;
    private String detail;
    private String category;
    private String status;
}
