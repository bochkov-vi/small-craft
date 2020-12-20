package com.bochkov.smallcraft.jpa.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractEntity<Long> {

    @Id
    @Column(name = "id_notification")
    @GeneratedValue(generator = "notification_seq")
    Long id;

    @ElementCollection
    @CollectionTable(name = "notification_region", joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> region;

    @ManyToOne
    @JoinColumn(name = "id_captain", nullable = false)
    Person captain;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    Unit unit;

    @ManyToOne
    @JoinColumn(name = "id_boat", nullable = false)
    Boat boat;

    Integer year;

    Integer number;

    LocalDate date;

    LocalDate dateFrom;

    LocalDate dateTo;

    @ElementCollection
    @Column(name = "activity")
    @CollectionTable(joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> activities;

    String timeOfDay;

    Boolean tck; //technical means of verification

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("captain", captain)
                .add("boat", boat)
                .add("date", date)
                .toString();
    }

    public Notification setActivity(String... activity) {
        if (activity != null) {
            setActivities(Sets.newHashSet(activity));
        }
        return this;
    }

    public boolean isValidExit(LocalDateTime dateTime) {
        return dateTime.isAfter(dateFrom.atStartOfDay()) && dateTime.isBefore(dateTo.atStartOfDay().plusDays(1));
    }

}
