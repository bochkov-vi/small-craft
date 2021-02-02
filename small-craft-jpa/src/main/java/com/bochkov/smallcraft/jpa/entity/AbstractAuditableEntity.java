package com.bochkov.smallcraft.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractAuditableEntity<ID extends Serializable> extends AbstractEntity<ID> {

    @CreatedBy
    String creator;

    @LastModifiedBy
    String modifier;

    @LastModifiedDate
    LocalDateTime modifyDate;
}
