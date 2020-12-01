package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BoatNumberSeq {

    @Id
    Integer id;

    Integer number;

    public Integer nextValue() {
        return ++number;
    }

    public BoatNumberSeq increment() {
        nextValue();
        return this;
    }
}
