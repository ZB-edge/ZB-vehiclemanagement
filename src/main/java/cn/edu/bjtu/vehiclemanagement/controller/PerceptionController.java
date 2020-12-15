package cn.edu.bjtu.vehiclemanagement.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/vehicle")
@RestController
public class PerceptionController {

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
