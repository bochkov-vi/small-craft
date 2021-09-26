package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
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

    @Column(name = "tail_number", unique = true, nullable = true)
    String tailNumber;

    String type;

    String model;

    @Column(nullable = true)
    LocalDate registrationDate;

    LocalDate expirationDate;

    Integer registrationNumber;

    Integer buildYear;

    String serialNumber;

    String pier;

    @Column(scale = 1, precision = 4)
    BigDecimal power;

    @ManyToOne
    @JoinColumn(name = "id_person", nullable = false)
    Person person;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    Unit unit;

    @Column(nullable = false, columnDefinition = "boolean not null default true")
    boolean notRegistable;

    @ManyToOne
    @JoinColumn(name = "id_legal_person")
    LegalPerson legalPerson;

    @Override
    public String toString() {
        return Stream.of(type, model, tailNumber, person, legalPerson, Optional.ofNullable(registrationNumber).map(n -> String.format("(%s)", n)).orElse(null)).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(" "));
    }

    public String getPersonAsString() {
        return Optional.ofNullable(legalPerson).map(lp -> String.format("%s (%s)", lp.toString(), getFio())).orElseGet(this::getFio);
    }


    public String getFio() {
        return Optional.ofNullable(person).map(Person::getFio).orElse(null);
    }
}
