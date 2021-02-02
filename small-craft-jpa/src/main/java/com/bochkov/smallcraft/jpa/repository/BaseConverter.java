package com.bochkov.smallcraft.jpa.repository;

public interface BaseConverter {

    String convert(Long number);

    Long convert(String number);
}
