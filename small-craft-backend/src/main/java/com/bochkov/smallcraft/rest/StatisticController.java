package com.bochkov.smallcraft.rest;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.google.common.collect.Lists;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class StatisticController {


    BoatRepository boatRepository;


    UnitRepository unitRepository;

    public StatisticController(BoatRepository boatRepository, UnitRepository unitRepository) {
        this.boatRepository = boatRepository;
        this.unitRepository = unitRepository;
    }

    Specification<Boat> boatAdditionlSpecification(Long idUnit, boolean includeChilds) {
        Optional<Unit> unit = Optional.ofNullable(idUnit).flatMap(id -> unitRepository.findById(id));

        Specification<Boat> boatSpecification = null;
        if (includeChilds) {
            boatSpecification = unit.map(Lists::newArrayList).map(uList -> (Specification<Boat>) (r, q, b) -> r.get("unit").in(uList)).orElse(null);
        } else {
            boatSpecification = unit.map(u -> (Specification<Boat>) (r, q, b) -> b.equal(r.get("unit"), u)).orElse(null);
        }
        return boatSpecification;
    }

    ;

    @GetMapping(path = "/rest/registeredCount")
    public Long registeredCount(@RequestParam(name = "idUnit", required = false) Long idUnit, @RequestParam(name = "childs", defaultValue = "false", required = false) Boolean childs, @RequestParam(name = "date", required = false) LocalDate date) {
        return boatRepository.registeredCount(boatAdditionlSpecification(idUnit, childs), date);
    }

    @GetMapping(path = "/rest/registeredCount/{year}")
    public Long registeredCount(@RequestParam(name = "idUnit", required = false) Long idUnit, @RequestParam(name = "childs", defaultValue = "false", required = false) Boolean childs, @PathVariable(name = "year") Integer year) {
        return boatRepository.registeredCount(boatAdditionlSpecification(idUnit, childs), year);
    }

    @GetMapping(path = "/rest/unregisteredCount")
    public Long unregisteredCount(@RequestParam(name = "idUnit", required = false) Long idUnit, @RequestParam(name = "childs", defaultValue = "false", required = false) Boolean childs, @RequestParam(name = "date", required = false) LocalDate date) {
        return boatRepository.unregisteredCount(boatAdditionlSpecification(idUnit, childs), date);
    }

    @GetMapping(path = "/rest/unregisteredCount/{year}")
    public Long unregisteredCount(@RequestParam(name = "idUnit", required = false) Long idUnit, @RequestParam(name = "childs", defaultValue = "false", required = false) Boolean childs, @PathVariable(name = "year") Integer year) {
        return boatRepository.unregisteredCount(boatAdditionlSpecification(idUnit, childs), year);
    }
}
