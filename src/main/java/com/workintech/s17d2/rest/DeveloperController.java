package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.DeveloperTax;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final DeveloperTax taxable;

    public DeveloperController(DeveloperTax taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        this.developers = new HashMap<>();
    }

    @GetMapping("/developers")
    public ResponseEntity<List<Developer>> getAllDevelopers() {
        return new ResponseEntity<>(new ArrayList<>(developers.values()), HttpStatus.OK);
    }

    @GetMapping("/developers/{id}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable Integer id) {
        Developer developer = developers.get(id);
        if (developer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(developer, HttpStatus.OK);
    }

    @PostMapping("/developers")
    public ResponseEntity<Developer> createDeveloper(@RequestBody Developer developer) {
        Developer created;
        double salary = developer.getSalary();
        if (developer.getExperience() == Experience.JUNIOR) {
            double taxed = salary - (salary * taxable.getSimpleTaxRate() / 100);
            created = new JuniorDeveloper(developer.getId(), developer.getName(), taxed);
        } else if (developer.getExperience() == Experience.MID) {
            double taxed = salary - (salary * taxable.getMiddleTaxRate() / 100);
            created = new MidDeveloper(developer.getId(), developer.getName(), taxed);
        } else if (developer.getExperience() == Experience.SENIOR) {
            double taxed = salary - (salary * taxable.getUpperTaxRate() / 100);
            created = new SeniorDeveloper(developer.getId(), developer.getName(), taxed);
        } else {
            created = developer;
        }
        developers.put(created.getId(), created);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/developers/{id}")
    public ResponseEntity<Developer> updateDeveloper(@PathVariable Integer id, @RequestBody Developer developer) {
        if (!developers.containsKey(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        developers.put(id, developer);
        return new ResponseEntity<>(developer, HttpStatus.OK);
    }

    @DeleteMapping("/developers/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Integer id) {
        developers.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
