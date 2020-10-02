package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "year")
@AllArgsConstructor
@NoArgsConstructor
public class BoatNumberSeq {

    @Id
    Integer year;

    Integer number;

    public Integer nextValue() {
        return number++;
    }

    public BoatNumberSeq increment() {
        nextValue();
        return this;
    }
}
