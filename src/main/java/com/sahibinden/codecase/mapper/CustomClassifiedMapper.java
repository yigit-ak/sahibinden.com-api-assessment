package com.sahibinden.codecase.mapper;

import com.sahibinden.codecase.dto.ClassifiedDto;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;
import org.springframework.stereotype.Component;

@Component
public class CustomClassifiedMapper implements ClassifiedMapper {

    @Override
    public Classified toEntity(NewClassifiedDto dto) {
        return Classified.builder()
                .title(dto.getTitle())
                .detail(dto.getDetail())
                .category(Category.valueOf(dto.getCategory()))
                .build();
    }

    @Override
    public ClassifiedDto toDto(Classified classified) {
        return ClassifiedDto.builder()
                .id(classified.getId())
                .title(classified.getTitle())
                .detail(classified.getDetail())
                .category(classified.getCategory().name())
                .status(classified.getStatus().name())
                .build();
    }
}
