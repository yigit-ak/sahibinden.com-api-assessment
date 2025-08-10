package com.sahibinden.codecase.mapper;

import com.sahibinden.codecase.dto.ClassifiedDto;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomClassifiedMapperTest {

    private final CustomClassifiedMapper mapper = new CustomClassifiedMapper();

    @Test
    void toEntity_maps_basic_fields_only() {
        NewClassifiedDto dto = NewClassifiedDto.builder()
                .title("Some valid title")
                .detail("Some valid detail with enough length")
                .category(Category.SHOPPING)
                .build();

        Classified e = mapper.toEntity(dto);

        assertEquals("Some valid title", e.getTitle());
        assertEquals("Some valid detail with enough length", e.getDetail());
        assertEquals(Category.SHOPPING, e.getCategory());

        // Not set by mapper
        assertEquals(0L, e.getId());        // primitive long defaults to 0
        assertNull(e.getStatus());          // assigned later (@PrePersist or service)
        assertNull(e.getCreatedAt());       // JPA populates on persist
        assertNull(e.getUpdatedAt());       // JPA populates on update

    }

    @Test
    void toDto_maps_all_exposed_fields() {
        Classified entity = Classified.builder()
                .id(123L)
                .title("Title")
                .detail("Detail long enough for the entity")
                .category(Category.SHOPPING)
                .status(Status.ACTIVE)
                .build();

        ClassifiedDto dto = mapper.toDto(entity);

        assertEquals(123L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Detail long enough for the entity", dto.getDetail());
        assertEquals(Category.SHOPPING, dto.getCategory());
        assertEquals(Status.ACTIVE, dto.getStatus());
    }
}
