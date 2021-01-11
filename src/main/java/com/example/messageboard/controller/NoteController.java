package com.example.messageboard.controller;

import com.example.messageboard.model.Note;
import com.example.messageboard.model.Status;
import com.example.messageboard.model.User;
import com.example.messageboard.service.NoteService;
import com.example.messageboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;

    @Autowired
    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<Note>> getAll(Principal principal) {
        List<Note> notes = null;
        if (principal == null || !isAdmin(principal)) {
            notes = noteService.getAll().stream()
                    .filter(n -> !n.getStatus()
                            .equals(Status.MODERATION))
                    .collect(Collectors.toList());
        } else {
            notes = noteService.getAll();
        }
        return !notes.isEmpty()
                ? new ResponseEntity<>(notes, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id, Principal principal) {
        Note note = null;
        if (principal == null || !isAdmin(principal)) {
            if (!noteService.getById(id).getStatus().equals(Status.MODERATION)) {
                note = noteService.getById(id);
            }
        } else {
            note = noteService.getById(id);
        }
        return note != null
                ? new ResponseEntity<>(note, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Note note, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        note.setUser_id(user.getId());
        note.setStatus(Status.MODERATION);
        noteService.create(note);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @RequestBody Note note, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Note currentNote = noteService.getById(id);

        if(!hasAccess(principal, currentNote.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        note.setUser_id(currentNote.getUser_id());
        noteService.update(id, note);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Note> delete(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Note note = noteService.getById(id);

        if (!hasAccess(principal, note.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        noteService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean hasAccess(Principal principal, Long noteId) {
        if (isAdmin(principal)) {
            return true;
        }
        try {
            User user = userService.findByEmail(principal.getName());
            return noteService.getById(noteId).getUser_id().equals(user.getId());
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    public boolean isAdmin(Principal principal) {
        if (userService.findByEmail(principal.getName()).getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

}
