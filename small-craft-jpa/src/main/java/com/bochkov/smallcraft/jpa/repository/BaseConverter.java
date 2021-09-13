package com.bochkov.smallcraft.jpa.repository;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public class BaseConverter {

    static char[] chars = "0123456789абвгдеиклмнопрстуя".toUpperCase().toCharArray();

    static BiMap<Character, Character> map = HashBiMap.create();

    static {
        for (int i = 0; i < chars.length; i++) {
            char c1 = chars[i];
            char c2 = Character.forDigit(i, chars.length);
            map.put(c1, c2);
            //System.out.println(map);
        }
    }


    public static String convert(Long number) {
        return Optional.ofNullable(number).map(BaseConverter::engConvert).map(BaseConverter::eng2rus).orElse(null);
    }

    public static String engConvert(Long number) {
        return Optional.ofNullable(number).map(BigInteger::valueOf).map(n -> n.toString(chars.length)).orElse(null);
    }


    public static Long convert(String number) {
        return Optional.ofNullable(number)
                .filter(str -> str.matches("\\d+"))
                .map(BaseConverter::rus2eng)
                .map(BaseConverter::engConvert).orElse(null);
    }

    public static Long engConvert(String number) {
        return Optional.ofNullable(number).map(str -> new BigInteger(str, chars.length)).map(BigInteger::longValue).orElse(null);
    }

    static String rus2eng(String str) {
        String result = null;
        if (str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            str.chars().mapToObj(i -> Character.valueOf((char) i)).map(c -> map.getOrDefault(c, (char) c)).forEach(stringBuilder::append);
            result = stringBuilder.toString();
        }
        return result;
    }

    static String eng2rus(String str) {
        String result = null;
        if (str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Map<Character, Character> map = BaseConverter.map.inverse();
            str.chars().mapToObj(i -> Character.valueOf((char) i)).map(c -> map.getOrDefault(c, (char) c)).forEach(stringBuilder::append);
            result = stringBuilder.toString();
        }
        return result;
    }


}
