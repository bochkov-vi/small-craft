package com.bochkov.smallcraft.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@Accessors(chain = true)
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RemberMeToken implements Serializable {

    private String tokenValue;

    private LocalDateTime dateTime;

    public Date getDate() {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public RemberMeToken setDate(Date date) {
        return setDateTime(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }
}
