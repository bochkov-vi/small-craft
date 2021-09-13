package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LegalPersonRepository extends JpaRepository<LegalPerson, Long>, JpaSpecificationExecutor<LegalPerson> {

}
