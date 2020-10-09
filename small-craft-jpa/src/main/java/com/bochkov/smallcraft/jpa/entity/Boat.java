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
public class Boat extends AbstractEntity<Long> {

    @Id
    @Column(name = "id_boat")
    @GeneratedValue(generator = "boat_seq")
    Long id;

    @Column(name = "tail_number")
    String tailNumber;

    String type;

    String model;

    @ManyToOne
    @JoinColumn(name = "id_own")
    Person own;

    @Override
    public String toString() {
        return String.format("%s %s %s", type, id, own);
    }
}
