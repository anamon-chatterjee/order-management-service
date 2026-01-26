package com.example.oms.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;
}
