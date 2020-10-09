package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    @Query(value = "SELECT p.first_name FROM person p WHERE p.first_name ILIKE :pattern ORDER BY position(:sort in first_name) LIMIT 10", nativeQuery = true)
    List<String> findFirstNameByMask(@Param("pattern") String pattern, @Param("sort") String sort);

    default List<String> findFirstNameByMask(String expr) {
        return findFirstNameByMask(Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%"), Optional.ofNullable(expr).orElse(""));
    }

    @Query(value = "SELECT p.middle_name FROM person p WHERE p.middle_name ILIKE :pattern ORDER BY position(:sort in middle_name) LIMIT 10", nativeQuery = true)
    List<String> findMiddleNameByMask(@Param("pattern") String pattern, @Param("sort") String sort);

    default List<String> findMiddleNameByMask(String expr) {
        return findMiddleNameByMask(Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%"), Optional.ofNullable(expr).orElse(""));
    }

    @Query(value = "SELECT p.* FROM person p LEFT JOIN legal_person lp on p.id_person = lp.id_person WHERE last_name ILIKE :expr OR lp.name ILIKE :expr ORDER BY position(:sort in last_name), length(last_name), last_name, position(:sort in lp.name), length(lp.name),    lp.name", nativeQuery = true)
    Page<Person> findByMask(@Param("expr") String expr, @Param("sort") String sort, Pageable pageable);

    default Page<Person> findByMask(String expr, Pageable pageable) {
        return findByMask(Optional.ofNullable(expr).map(str -> String.format("%%%s%%", str)).orElse("%"), Optional.ofNullable(expr).orElse(""), pageable);
    }

    Optional<Person> findByPassportSerialAndPassportNumber(String serial, String number);
}
