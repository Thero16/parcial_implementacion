package com.nomolestar.peopleservice.model;

import com.nomolestar.peopleservice.enums.PersonRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonRole role;

    @Column()
    private Integer age;

    @Column()
    private String description;

    @Column(nullable = false)
    private Integer caseId;
}
