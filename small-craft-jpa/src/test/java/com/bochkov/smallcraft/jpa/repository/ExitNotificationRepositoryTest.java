package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ExitNotificationRepositoryTest {

    @Autowired
    ExitNotificationRepository repository;

    char[] chars = "0123456789абвгдеиклмнопрстуя".toUpperCase().toCharArray();

    BiMap<Character, Character> map = HashBiMap.create();

    @Before
    public void init() {
        for (int i = 0; i < chars.length; i++) {
            char c1 = chars[i];
            char c2 = Integer.toString(i, chars.length).charAt(0);
            map.put(c1,c2);
            System.out.println(map);
        }
    }

    @Test
    public void convert() {
        repository.findAll().stream().forEach(en -> {
            Long id = en.getId();
            String str = repository.convert(id);
            System.out.println(str);
        });
    }

    @Test
    public void findTopExitNotificationOrderByModifyDate(){
        Optional<ExitNotification> o = repository.findLastModified();
        System.out.println(o.orElse(null));
    }
}