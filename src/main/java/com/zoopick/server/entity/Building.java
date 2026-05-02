package com.zoopick.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buildings", schema = "zoopick")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 20, nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}
