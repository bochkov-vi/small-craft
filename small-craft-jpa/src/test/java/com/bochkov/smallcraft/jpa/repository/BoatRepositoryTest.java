package com.bochkov.smallcraft.jpa.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BoatRepositoryTest {

    @Autowired
    BoatRepository repository;

    @Test
    public void findPierByMask() {
       repository.findPierByMask("Фреза", PageRequest.of(1,10));
    }

   /* @Test
    public void doInitSeq(){
        Integer number = repository.doInitSeq();
        Assert.assertNotNull(number);
        Assert.assertTrue(number>0);
    }*/
}