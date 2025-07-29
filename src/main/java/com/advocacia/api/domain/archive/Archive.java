package com.advocacia.api.domain.archive;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "archive")
public class Archive {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idArchive")
    private String id;
    private String name;
    private String linkArchive;
}