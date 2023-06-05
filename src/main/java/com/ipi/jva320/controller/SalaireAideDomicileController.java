package com.ipi.jva320.controller;


import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.service.SalarieAideADomicileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Controller
public class SalaireAideDomicileController {


    @Autowired
    SalarieAideADomicileService salarieAideADomicileService;

    @GetMapping(value = "/")
    public String home(ModelMap model) {
        model.put("salarieCount", salarieAideADomicileService.countSalaries());
        return "home";
    }

    @GetMapping(value = "/salaries/{id}")
    public String salarie(ModelMap model, @PathVariable Long id) {
        model.put("salarie", salarieAideADomicileService.getSalarie(id));
        return "detail_Salarie";
    }

    @GetMapping(value = "/salaries/aide/new")
    public String newSalarie(ModelMap model) {
        return "detail_Salarie";
    }

    @PostMapping(value = "/salaries/save")
    public String createSalarie(SalarieAideADomicile salarie) throws SalarieException {
        salarieAideADomicileService.creerSalarieAideADomicile(salarie);
        return "redirect:/salaries/" + salarie.getId();
    }


    @PostMapping(value = "/salaries/{id}")
    public String updateSalarie(SalarieAideADomicile salarie) throws SalarieException {
        salarieAideADomicileService.updateSalarieAideADomicile(salarie);
        return "redirect:/salaries/" + salarie.getId();
    }

    @GetMapping(value = "/salaries")
    public String getSalaries(ModelMap model) {
        model.put("salaries", salarieAideADomicileService.getSalaries());
        return "list";
    }



    @GetMapping(value = "/salarie")
    public String searchSalaries(@RequestParam("nom") String nom, ModelMap model) {
        List<SalarieAideADomicile> salaries = salarieAideADomicileService.searchSalariesByNom(nom);
        if (salaries.isEmpty()) {
         //j'ai pensé à retourner une page personnalisée mais n'empêche j'ai commenté ça
        //throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            model.put("name", nom);
           return "error_r";
        }

        model.put("salaries", salaries);
        return "list";
    }


    @GetMapping(value = "/salaries/{id}/delete")
    public String deleteSalary(@PathVariable("id") long id) throws SalarieException {

        SalarieAideADomicile salary = salarieAideADomicileService.getSalarie(id);
        if (salary == null) {
            throw new SalarieException("Le salarié avec l'ID " + id + " n'a pas été trouvé.");
        }
        salarieAideADomicileService.deleteSalarieAideADomicile(id);

        return "redirect:/salaries";
    }


    @GetMapping("salaries/page/")
    public String displaySalariePagination(@RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestParam(value = "sortProperty", defaultValue = "id") String sortField,
                                           @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
                                           ModelMap model) {

        if (StringUtils.isEmpty(sortDirection)) {
            sortDirection = "ASC";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(sortDirection), sortField);
        Page<SalarieAideADomicile> salarieAideADomicilePage = salarieAideADomicileService.getPageSalaries(pageable, sortDirection, sortField);

        if (salarieAideADomicilePage.hasContent()) {
            model.addAttribute("salaries", salarieAideADomicilePage.getContent());
            Long nbSalarie = salarieAideADomicileService.countSalaries();
            model.addAttribute("nbSalarie", nbSalarie);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pas de salariés trouvés");
        }

        return "list";
    }



}
