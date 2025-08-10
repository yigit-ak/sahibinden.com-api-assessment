package com.sahibinden.codecase.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "status_changes", indexes = {
        @Index(name = "idx_classified_id", columnList = "classified_id"),
        @Index(name = "idx_status_timestamp", columnList = "createdAt")
})
public class StatusLog {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classified_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Classified classified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
