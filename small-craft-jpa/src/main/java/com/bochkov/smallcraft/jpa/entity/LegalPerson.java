package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LegalPerson extends AbstractEntity<Long> {

    @Id
    @Column(name = "id_legal_person")
    @GeneratedValue(generator = "legal_person_seq")
    Long id;

    String name;

    String inn;

    String address;

    @OneToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id_person", nullable = false)
    Person person;

    @Override
    public String toString() {
        return String.format("%s (%s)", name, person);
    }
}
