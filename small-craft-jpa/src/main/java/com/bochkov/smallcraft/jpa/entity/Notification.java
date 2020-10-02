package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractEntity<NotificationPK> {

    @Id
    @GeneratedValue
    NotificationPK id;

    String region;

    @ManyToOne
    Person person;

    @ManyToOne
    Boat boat;

    LocalDate date;

    LocalDate dateFrom;

    LocalDate dateTo;

    String activity;

    String timeOfDay;

    Boolean tck; //technical means of verification
}
