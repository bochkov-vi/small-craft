package com.bochkov.smallcraft.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Data
@Embeddable
@EqualsAndHashCode(of = {"serial", "number"})
@Accessors(chain = true)
public class Passport implements Serializable {

    @Column(name = "passport_serial")
    String serial;

    @Column(name = "passport_number")
    String number;

    @Column(name = "passport_date")
    LocalDate date;

    @Column(name = "passport_data")
    String data;

    public String toString() {
        return MessageFormat.format("Паспорт {0} №{1} от {2}, {3}", serial, number, date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)), data);
    }
}
