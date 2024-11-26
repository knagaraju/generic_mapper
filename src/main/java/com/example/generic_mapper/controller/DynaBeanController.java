package com.example.generic_mapper.controller;

import com.example.generic_mapper.service.DynaBeanService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DynaBeanController {
    private final DynaBeanService service;

    public DynaBeanController(DynaBeanService service) {
        this.service = service;
    }

    @PostMapping("/{table}")
    public ResponseEntity<Void> save(
            @PathVariable String table,
            @RequestBody Map<String, Object> data
    ) {
        try {
            service.save(table, data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{table}/{id}")
    public ResponseEntity<Void> update(
            @PathVariable String table,
            @PathVariable Object id,
            @RequestBody Map<String, Object> data
    ) {
        try {
            service.update(table, "id", id, data);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{table}")
    public ResponseEntity<List<Map<String, Object>>> getAll(
            @PathVariable String table
    ) {
        try {
            List<Map<String, Object>> results = service.findAll(table);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
