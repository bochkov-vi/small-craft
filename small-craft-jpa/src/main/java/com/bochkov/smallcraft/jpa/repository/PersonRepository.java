package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    @Query(value = "SELECT DISTINCT first_name FROM (SELECT p.first_name FROM person p WHERE p.first_name ILIKE :pattern ORDER BY position(:orig in first_name)) as t",
            countQuery = "SELECT COUNT(DISTINCT first_name) FROM person p WHERE p.first_name ILIKE :pattern AND :orig IS NOT NULL",
            nativeQuery = true)
    Page<String> findFirstNameByMask(@Param("pattern") String pattern, @Param("orig") String sort, Pageable pageable);

    default Page<String> findFirstNameByMask(String expr, Pageable pageable) {
        String like = Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%");
        String sort = Optional.ofNullable(expr).orElse("");
        return findFirstNameByMask(like, sort, pageable);
    }

    @Query(value = "SELECT DISTINCT n FROM (SELECT p.middle_name n FROM person p WHERE p.middle_name ILIKE :pattern ORDER BY position(:sort in middle_name)) as t",
            countQuery = "SELECT COUNT(DISTINCT p.middle_name) FROM person p WHERE p.middle_name ILIKE :pattern AND :sort IS NOT NULL",
            nativeQuery = true)
    Page<String> findMiddleNameByMask(@Param("pattern") String pattern, @Param("sort") String sort, Pageable pageable);

    default Page<String> findMiddleNameByMask(String expr, Pageable pageable) {
        return findMiddleNameByMask(Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%"), Optional.ofNullable(expr).orElse(""), pageable);
    }

    @Query(value = "SELECT p.* FROM person p LEFT JOIN notification n on p.id_person = n.id_person " +
            "LEFT JOIN boat b on p.id_person = b.id_person " +
            "LEFT JOIN legal_person lp on b.id_legal_person = lp.id_legal_person " +
            "WHERE last_name ILIKE :expr OR lp.name ILIKE :expr " +
            "OR lp.name ILIKE :expr " +
            "ORDER BY position(:sort in last_name), length(last_name), last_name, position(:sort in lp.name), length(lp.name),    lp.name", nativeQuery = true)
    Page<Person> findByMask(@Param("expr") String expr, @Param("sort") String sort, Pageable pageable);

    default Page<Person> findByMask(String expr, Pageable pageable) {
        return findByMask(Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%"), Optional.ofNullable(expr).orElse(""), pageable);
    }

    List<Person> findByPassportSerialAndPassportNumber(String serial, String number);

    List<Person> findByPhones(String phone);

    List<Person> findByPhonesIn(Collection<String> phones);

    @Query(value = "SELECT * FROM person o WHERE lower(o.first_name)=lower((?1)::::varchar) and lower(o.middle_name)=lower((?2)::::varchar) and lower(o.last_name)=lower((?3)::::varchar)", nativeQuery = true)
    List<Person> findAll(String firstName, String middleName, String lastName);
}
