package com.bochkov.smallcraft.jpa.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest()
@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationNumberSeqRepositoryTest {


    @Autowired
    NotificationNumberSeqRepository repository;

    @Test
    public void nextValue() {
        Integer number = repository.nextValue();
        System.out.println(number);
    }

    @Test
    public void multiThreading() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(80);
        for (int t = 0; t < 1000; t++) {
            service.execute(() -> {
                for (int i = 0; i < 1000; i++) {
                    Integer number = repository.nextValue();
                    System.out.println(Thread.currentThread().getName() +" i="+ i + " number = " + number);
                    System.out.flush();
                }
            });
        }
        service.awaitTermination(5, TimeUnit.MINUTES);
    }

    @Test
    @Transactional
    public void currentValue() {
        Integer number = repository.currentValue();
        System.out.println(number);
    }
}