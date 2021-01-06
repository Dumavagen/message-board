package com.example.messageboard.controller;

import com.example.messageboard.model.Category;
import com.example.messageboard.model.Note;
import com.example.messageboard.model.User;
import com.example.messageboard.service.CategoryService;
import com.example.messageboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categories = categoryService.getAll();
        return !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return category != null
                ? new ResponseEntity<>(category, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category, Principal principal) {
        if (isAdmin(principal)) {
            categoryService.create(category);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping("{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        if (isAdmin(principal)) {
            categoryService.update(id, category);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Note> delete(@PathVariable Long id, Principal principal) {
        if (isAdmin(principal)) {
            categoryService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public boolean isAdmin(Principal principal) {
        if (userService.findByEmail(principal.getName()).getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }
}
