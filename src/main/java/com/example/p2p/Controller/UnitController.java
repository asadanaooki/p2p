package com.example.p2p.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.p2p.service.UnitService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/unit")
@AllArgsConstructor
public class UnitController {

    private UnitService unitService;

    @GetMapping
    public String showUnit(Model model) {
        model.addAttribute("unitList", unitService.getUnitList());
        return "unit-list";
    }

}
