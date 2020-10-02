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
public class Boat extends AbstractEntity<String> {

    @Id
    @Column(name = "tail_number")
    String id;

    String type;

    String model;

    @ManyToOne
    @JoinColumn(name = "id_person")
    Person person;
}
