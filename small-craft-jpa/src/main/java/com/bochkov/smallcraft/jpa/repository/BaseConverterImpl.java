package com.bochkov.smallcraft.jpa.repository;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public class BaseConverterImpl implements BaseConverter {

    char[] chars = "0123456789абвгдеиклмнопрстуя".toUpperCase().toCharArray();

    BiMap<Character, Character> map = HashBiMap.create();

    @Override
    public String convert(Long number) {
        return Optional.ofNullable(number).map(this::engConvert).map(this::eng2rus).orElse(null);
    }

    public String engConvert(Long number) {
        return Optional.ofNullable(number).map(BigInteger::valueOf).map(n -> n.toString(chars.length)).orElse(null);
    }

    @Override
    public Long convert(String number) {
        return Optional.ofNullable(number)
                .map(this::rus2eng)
                .map(this::engConvert).orElse(null);
    }

    public Long engConvert(String number) {
        return Optional.ofNullable(number).map(str -> new BigInteger(str, chars.length)).map(BigInteger::longValue).orElse(null);
    }

    String rus2eng(String str) {
        String result = null;
        if (str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            str.chars().mapToObj(i -> Character.valueOf((char) i)).map(c -> map.getOrDefault(c, (char) c)).forEach(stringBuilder::append);
            result = stringBuilder.toString();
        }
        return result;
    }

    String eng2rus(String str) {
        String result = null;
        if (str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Map<Character, Character> map = this.map.inverse();
            str.chars().mapToObj(i -> Character.valueOf((char) i)).map(c -> map.getOrDefault(c, (char) c)).forEach(stringBuilder::append);
            result = stringBuilder.toString();
        }
        return result;
    }

    @PostConstruct
    public void postConstruct() {
        for (int i = 0; i < chars.length; i++) {
            char c1 = chars[i];
            char c2 = Character.forDigit(i, chars.length);
            map.put(c1, c2);
            //System.out.println(map);
        }
    }
}
