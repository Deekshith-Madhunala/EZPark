package io.park.e_z_park.controller;

import io.park.e_z_park.service.ParkingLLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parking-llm")
public class ParkingLLMController {

    @Autowired
    private ParkingLLMService llmService;

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody Map<String, String> body) {
        try {
            String query = body.get("query");
            String answer = llmService.askParkingLLM(query);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
