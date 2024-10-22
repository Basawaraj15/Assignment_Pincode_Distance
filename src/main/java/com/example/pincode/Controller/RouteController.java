package com.example.pincode.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pincode.Entity.Route;
import com.example.pincode.Services.RouteService;




@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/getRoute")
    public ResponseEntity<Route> getRoute(@RequestParam String fromPincode, @RequestParam String toPincode) {
        Route route = routeService.getRoute(fromPincode, toPincode);
        return ResponseEntity.ok(route);
    }
}
