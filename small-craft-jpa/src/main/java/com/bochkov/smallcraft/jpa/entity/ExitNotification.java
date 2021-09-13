package com.bochkov.smallcraft.jpa.entity;

import com.bochkov.smallcraft.jpa.repository.BaseConverter;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exit_notification")
public class ExitNotification extends AbstractAuditableEntity<Long> {

    @Id
    @Column(name = "id_exit_notification")
    @GeneratedValue(generator = "exit_notification_seq", strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime exitCallDateTime;

    LocalDateTime exitDateTime;

    LocalDateTime returnDateTime;

    LocalDateTime returnCallDateTime;

    LocalDateTime estimatedReturnDateTime;

    @ElementCollection
    @Column(name = "region")
    @CollectionTable(name = "exit_notification_region", joinColumns = @JoinColumn(name = "id_notification"))
    Set<String> regions;

    String pier;

    @ElementCollection
    @Column(name = "activity")
    @CollectionTable(name = "exit_notification_activity", joinColumns = @JoinColumn(name = "id_exit_notification"))
    Set<String> activities;

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

    public static ExitNotification of(Notification notification) {
        Optional<Notification> o = Optional.of(notification);
        ExitNotification r = new ExitNotification();
        r.putData(notification);
        return r;
    }

    public ExitNotification putData(Notification notification) {
        Optional<Notification> o = Optional.of(notification);
        setNotification(o.orElse(null));
        setRegions(o.map(Notification::getRegions).map(Sets::newHashSet).orElse(null));
        setActivities(o.map(Notification::getActivities).map(Sets::newHashSet).orElse(null));
        setBoat(o.map(Notification::getBoat).orElse(null));
        setCaptain(o.map(Notification::getBoat).map(Boat::getPerson).orElse(null));
        setPier(o.map(Notification::getPier).orElse(null));
        setUnit(o.map(Notification::getUnit).orElse(null));
        return this;
    }

    public String getCode() {
        return BaseConverter.convert(id);
    }

    public ExitNotification setCode(String code){
        this.id = BaseConverter.convert(code);
        return this;
    }
    @Override
    public String toString() {
        return String.format("Выход №%s",getCode());
    }
}
