package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Status;
import lombok.Data;

@Data
public class ClassifiedDto {
    private long id;
    private String title;
    private String detail;
    private Category category;
    private Status status;
}
