package com.sahibinden.codecase.entity;

import com.sahibinden.codecase.utility.DuplicateKey;
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

    @Column(name = "duplicate_key", nullable = false, length = 43)
    private String duplicateKey;

    // Recompute only when inputs are available
    private void refreshDuplicateKeyIfReady() {
        if (category != null && title != null && detail != null) {
            this.duplicateKey = DuplicateKey.of(this);
        }
    }

    // Override setters that affect the key
    public void setTitle(String title) {
        this.title = title;
        refreshDuplicateKeyIfReady();
    }

    public void setDetail(String detail) {
        this.detail = detail;
        refreshDuplicateKeyIfReady();
    }

    public void setCategory(Category category) {
        this.category = category;
        refreshDuplicateKeyIfReady();
    }

    /* ------------ Builder customization ------------ */

    @PrePersist
    @PreUpdate
    void prePersistOrUpdate() {
        // default status if caller didn’t set
        if (status == null) {
            status = (category == Category.SHOPPING) ? Status.ACTIVE : Status.PENDING_APPROVAL;
        }
        // ensure DB has the right fingerprint
        refreshDuplicateKeyIfReady();
    }

    /* ------------ JPA lifecycle safety net ------------ */

    // Lombok will generate fields for the builder; we override build() to finalize the key.
    public static class ClassifiedBuilder {
        public Classified build() {
            Classified c = new Classified();
            c.id = this.id;
            c.title = this.title;
            c.detail = this.detail;
            c.category = this.category;
            c.status = this.status;
            // If caller didn’t supply a key, compute it now (when inputs are present)
            c.refreshDuplicateKeyIfReady();
            return c;
        }
    }

}
