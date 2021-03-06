package com.bochkov.smallcraft.jpa.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountRepositoryTest extends PersonRepositoryTest {

    @Autowired
    AccountRepository repository;

    @Test
    public void findTokens() {
        repository.findByTokenSeries("x");
    }
}
