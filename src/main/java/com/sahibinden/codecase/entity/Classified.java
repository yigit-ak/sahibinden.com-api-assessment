package com.sahibinden.codecase.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "classifieds")
public class Classified {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    @Size(min = 10, max = 50)
    @Pattern(regexp = "^[\\p{L}\\p{Nd}].*", message = "Title must start with a letter or digit")
    @Column(nullable = false, length = 50)
    private String title;

    @NotBlank
    @Size(min = 20, max = 200)
    @Column(nullable = false, length = 200)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Status status;

}
