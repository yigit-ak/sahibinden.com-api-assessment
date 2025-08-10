package com.sahibinden.codecase.mapper;

import com.sahibinden.codecase.dto.ClassifiedDto;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.entity.Classified;

public interface ClassifiedMapper {

    Classified toEntity(NewClassifiedDto dto);

    ClassifiedDto toDto(Classified classified);

}