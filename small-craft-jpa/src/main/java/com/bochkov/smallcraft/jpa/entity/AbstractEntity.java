package com.bochkov.smallcraft.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID> {

    @CreatedDate
    LocalDateTime createDate;

    @Override
    public boolean isNew() {
        return createDate == null;
    }
}
