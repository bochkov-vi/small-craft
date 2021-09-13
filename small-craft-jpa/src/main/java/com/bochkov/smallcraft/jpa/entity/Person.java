package com.bochkov.smallcraft.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;


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

    @Column(name = "phone", nullable = false)
    @ElementCollection
    @CollectionTable(name = "person_phone", joinColumns = @JoinColumn(name = "id_person", referencedColumnName = "id_person"))
    Set<String> phones;

    String email;

    String address;

    @Embedded
    Passport passport;

    LocalDate birthDate;

    @Override
    public String toString() {
        return getFio();
    }

    @JsonIgnore
    public String getFio() {
        return lastName + " " + Optional.ofNullable(firstName)
                .map(s -> s.charAt(0) + ".")
                .map(fn -> Optional.ofNullable(middleName).map(s -> fn + s.charAt(0) + ".").orElse(null))
                .orElse(null);
    }

    @JsonIgnore
    public String getFullFio() {
        return String.format("%s %s %s",lastName,firstName,middleName);
    }

    public Person setPhone(String... s) {
        return setPhones(Sets.newLinkedHashSet(Lists.newArrayList(s)));
    }


}
