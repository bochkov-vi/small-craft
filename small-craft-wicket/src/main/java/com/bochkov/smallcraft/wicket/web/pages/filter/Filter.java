package com.bochkov.smallcraft.wicket.web.pages.filter;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class Filter<T> implements Serializable {

    public abstract Specification<T> specification();
}
