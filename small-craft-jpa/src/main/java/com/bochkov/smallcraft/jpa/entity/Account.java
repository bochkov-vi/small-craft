package com.bochkov.smallcraft.jpa.entity;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @ElementCollection
    @MapKeyColumn(name = "series")
    Map<String, RemberMeToken> tokens;


    public Account(String id) {
        this.id = id;
    }

    public Account setRole(String... roles) {
        setRoles(Lists.newArrayList(roles));
        return this;
    }

    public Account updateToken(String series, String tokenValue, Date lastUsed) {
        token(series).ifPresent(t -> t.setDate(lastUsed).setTokenValue(tokenValue));
        return this;
    }

    public Optional<RemberMeToken> token(String series) {
        return Optional.ofNullable(tokens).map(map -> map.get(series));
    }
}
