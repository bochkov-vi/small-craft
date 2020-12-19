package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractEntity<String> {
    @Id
    String id;

    @ElementCollection
    List<String> roles;

    @ManyToOne
    @JoinColumn(name = "id_unit")
    Unit unit;

    String password;
}
