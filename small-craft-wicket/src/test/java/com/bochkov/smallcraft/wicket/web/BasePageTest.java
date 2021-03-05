package com.bochkov.smallcraft.wicket.web;

import com.bochkov.smallcraft.wicket.WicketSpringBootApplication;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import org.junit.Test;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ReflectionUtils;

import java.util.Set;

@SpringBootTest
@ContextConfiguration(classes = WicketSpringBootApplication.class)
public class BasePageTest {

    @Test
    public void findTblePageClasses() {
        Reflections reflections = new Reflections("com.bochkov.smallcraft");
        Set<Class<? extends CrudTablePage>> classes = reflections.getSubTypesOf(CrudTablePage.class);
        System.out.println(classes);
    }
}