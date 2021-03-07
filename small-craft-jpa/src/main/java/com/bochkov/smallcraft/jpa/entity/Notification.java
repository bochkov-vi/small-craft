package com.bochkov.smallcraft.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Column(name = "region")
    @CollectionTable(name = "notification_region", joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> regions;

    @ManyToOne
    @JoinColumn(name = "id_captain", nullable = false)
            @JsonDeserialize()
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

    String pier;

    Boolean canVoiceCall = false;

    @ElementCollection
    @Column(name = "activity")
    @CollectionTable(name = "notification_activity", joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> activities;

    String timeOfDay;

    Boolean tck; //technical means of verification

    @Override
    public String toString() {
        return Stream.of(number, boat, captain, unit).filter(Objects::nonNull).map(Objects::toString).collect(Collectors.joining(" "));
    }

    public Notification setActivity(String... activity) {
        if (activity != null) {
            setActivities(Sets.newHashSet(activity));
        }
        return this;
    }

    @JsonIgnore
    public boolean isValidExit(LocalDateTime dateTime) {
        return dateTime.isAfter(dateFrom.atStartOfDay()) && dateTime.isBefore(dateTo.atStartOfDay().plusDays(1));
    }

    @JsonIgnore
    public boolean isValidExit() {
        return isValidExit(LocalDateTime.now());
    }

    @JsonIgnore
    public boolean isExpiredDate(LocalDateTime dateTime) {
        return !dateTime.isBefore(dateTo.atStartOfDay().plusDays(1));
    }

    @JsonIgnore
    public boolean isExpired() {
        return isExpiredDate(LocalDateTime.now());
    }

}
