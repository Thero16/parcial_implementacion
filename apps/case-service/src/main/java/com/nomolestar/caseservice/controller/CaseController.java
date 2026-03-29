package com.nomolestar.caseservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cases")
public class CaseController {

    @GetMapping("/hola")
    public String holaMundo() {
        return "Hola Mundo";
    }
}
