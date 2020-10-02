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
    @Column(name = "id_person")
    Long id;

    String name;

    String inn;

    String address;

    @OneToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id_person", insertable = false, updatable = false)
    Person person;
}
