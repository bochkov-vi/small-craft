package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    @Query("SELECT DISTINCT o FROM Account  o inner join o.tokens t WHERE KEY(t) =:series")
    Optional<Account> findByTokenSeries(String series);
}
