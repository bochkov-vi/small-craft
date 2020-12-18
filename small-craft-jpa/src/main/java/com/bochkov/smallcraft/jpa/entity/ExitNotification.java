package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExitNotification extends AbstractEntity<Long> {

    @Id
    @Column(name = "id_exit_notification")
    @GeneratedValue(generator = "exit_notification_seq")
    Long id;

    LocalDateTime exitCallDateTime;

    LocalDateTime exitDateTime;

    LocalDateTime returnDateTime;

    LocalDateTime returnCallDateTime;

    @ElementCollection
    @CollectionTable(name = "exit_notification_region", joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> region;

    String pier;

    String activity;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    Unit unit;

    @ManyToOne
    @JoinColumn(name = "id_boat")
    Boat boat;

    @ManyToOne
    @JoinColumn(name = "id_notification")
    Notification notification;

    @ManyToOne
    @JoinColumn(name = "id_captain")
    Person captain;
}
