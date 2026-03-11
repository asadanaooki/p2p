package com.example.p2p.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.p2p.service.UnitService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/unit")
@AllArgsConstructor
public class UnitController {

    private UnitService unitService;

    @GetMapping
    public String showUnit(Model model, @RequestParam(required = false) Boolean status) {
        model.addAttribute("status", status);
        model.addAttribute("unitList", unitService.getUnitList(status));
        return "unit-list";
    }

}
