package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;


@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(generator = "person_seq")
    @Column(name = "id_person")
    Long id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String middleName;

    @Column(nullable = false)
    String lastName;

    @Column(nullable = false)
    String phone;

    String email;

    String address;

    @Embedded
    Passport passport;

    @OneToOne
    @JoinColumn(name = "id_legal_person")
    LegalPerson legalPerson;

    @Override
    public String toString() {
        return getFio() + Optional.ofNullable(legalPerson)
                .map(LegalPerson::getName)
                .map(name -> String.format(" (%s)", name))
                .orElse("");
    }

    public String getFio() {
        return lastName + " " + Optional.ofNullable(firstName)
                .map(s -> s.substring(0, 1) + ".")
                .map(fn -> Optional.ofNullable(middleName).map(s -> fn + s.substring(0, 1) + ".").orElse(null))
                .filter(Objects::nonNull)
                .orElse(null);
    }
}
