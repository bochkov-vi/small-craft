package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Boat extends AbstractEntity<Long> {

    @Id
    @Column(name = "id_boat")
    @GeneratedValue(generator = "boat_seq")
    Long id;

    @Column(name = "tail_number")
    String tailNumber;

    String type;

    String model;

    LocalDate registrationDate;

    LocalDate expirationDate;

    Integer registrationNumber;

    String pier;

    @ManyToOne
    @JoinColumn(name = "id_person", nullable = false)
    Person person;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    Unit unit;

    @ManyToOne
    @JoinColumn(name = "id_legal_person")
    LegalPerson legalPerson;

    @Override
    public String toString() {
        return Stream.of(type, model, tailNumber, person, legalPerson).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(" "));
    }
}
