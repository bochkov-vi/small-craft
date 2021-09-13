package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ExitNotificationRepositoryTest {

    @Autowired
    ExitNotificationRepository repository;

    char[] chars = "0123456789абвгдеиклмнопрстуя".toUpperCase().toCharArray();

    BiMap<Character, Character> map = HashBiMap.create();

    @Autowired
    UnitRepository unitRepository;

    @Test
    public void findDuplicates() {
        Long id = BaseConverter.convert("67");

        Notification notification = repository.findById(id).map(ExitNotification::getNotification).get();
        Assert.assertNotNull(notification);
        LocalDateTime d1 = LocalDateTime.now();
        LocalDateTime d2 = null;
        Specification specification = (r, q, b) -> {
            Predicate predicate = b.and(b.equal(r.get("notification"), notification),
                    b.lessThan(r.get("exitDateTime"), d2 != null ? d2 : LocalDateTime.now()),
                    b.greaterThan(b.coalesce(r.get("returnDateTime"), LocalDateTime.now()), d1));
            return predicate;
        };

        List<ExitNotification> list = repository.findAll(specification);
        Assert.assertFalse(list.isEmpty());

    }

    @Test
    public void findLongTimeExits() {
        Optional<Specification<ExitNotification>> longDateTimeSpecification = Optional.ofNullable(true).filter(aBoolean -> aBoolean).map(aBoolean -> {
                    return (Specification<ExitNotification>) (r, q, b) -> {
                        Predicate predicate = null;
                        Expression exitDateTime = b.function("date", LocalDate.class, r.get("exitDateTime"));
                        Expression returnDateTime = b.coalesce(b.function("date", LocalDate.class, r.get("returnDateTime")), b.currentDate());
                        Expression<Integer> diff = b.diff(returnDateTime, exitDateTime);
                        predicate = b.gt(diff, 0);
                        return predicate;
                    };
                }
        );
        List list = repository.findAll(longDateTimeSpecification.orElseGet(null));
        Assert.assertFalse(list.isEmpty());
    }


    @Test
    public void findByMask() {
        List list = repository.findAll(MaskableProperty.maskSpecification("тест", "boat.tailNumber", "captain.lastName", "boat.registrationNumber", "boat.model"));
        Assert.assertFalse(list.isEmpty());
    }

    @Before
    public void init() {
        for (int i = 0; i < chars.length; i++) {
            char c1 = chars[i];
            char c2 = Integer.toString(i, chars.length).charAt(0);
            map.put(c1, c2);
            //System.out.println(map);
        }
    }

    @Test
    public void convert() {
        repository.findAll().stream().forEach(en -> {
            Long id = en.getId();
            String str = BaseConverter.convert(id);
            System.out.println(str);
        });
    }

    @Test
    public void findTopExitNotificationOrderByModifyDate() {
        Optional<ExitNotification> o = repository.findLastModified();
        System.out.println(o.orElse(null));
    }



    @Test
    public void countOnExitForDay() {
        Unit unit = unitRepository.findById(1L).orElse(null);
        Long result = repository.countOnExitForDay(LocalDate.now(), unit, true);
        Assert.assertTrue(result>0);
    }
    @Test
    public void countTotalOnExit() {
        Unit unit = unitRepository.findById(1L).orElse(null);
        Long result = repository.countTotalOnExit( unit, true);
        Assert.assertTrue(result>0);
    }
    @Test
    public void countReturnsForDay() {
        Unit unit = unitRepository.findById(1L).orElse(null);
        Long result = repository.countReturnsForDay( LocalDate.now(),unit, true);
        Assert.assertTrue(result>0);
    }
    @Test
    public void countTotalOnExitLongTime() {
        Unit unit = unitRepository.findById(1L).orElse(null);
        Long result = repository.countTotalOnExitLongTime( LocalDate.now(),unit, true);
        Assert.assertTrue(result>0);
    }    }