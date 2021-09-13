package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    @Override
    public String toString() {
        return name;
    }
}
