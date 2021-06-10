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
public class NotificationNumberSeq {

    final static Integer segments = 5;

    final static Integer level = 0;

    @Id
    Integer year;

    Integer number;

    public synchronized Integer nextValue() {
        number++;
        while (number % segments != level) {
            number++;
        }
        return number;
    }

    public NotificationNumberSeq increment() {
        nextValue();
        return this;
    }
}
