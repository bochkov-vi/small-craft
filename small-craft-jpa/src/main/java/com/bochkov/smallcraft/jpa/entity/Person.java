package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


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

    String firstName;

    String middleName;

    String lastName;

    String phone;

    String email;

    String address;

    @Embedded
    Passport passport;

    @OneToOne(mappedBy = "person")
    @Fetch(FetchMode.JOIN)
    LegalPerson legalPerson;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(firstName)
                .append(" ")
                .append(middleName)
                .append(" ")
                .append(lastName)
                .toString();
    }
}
