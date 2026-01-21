package com.ensolvers.notes_backend.controller;

import com.ensolvers.notes_backend.dto.NoteDTO;
import com.ensolvers.notes_backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:3000")
public class NoteController {
    
    @Autowired
    private NoteService noteService;
    
    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes(
        @RequestParam(required = false, defaultValue = "false") boolean archived,
        @RequestParam(required = false) Long categoryId
    ) {
        List<NoteDTO> notes = noteService.getAllNotes(archived, categoryId);
        return ResponseEntity.ok(notes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        try {
            NoteDTO note = noteService.getNoteById(id);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody NoteDTO noteDTO) {
        NoteDTO createdNote = noteService.createNote(noteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
        @PathVariable Long id,
        @RequestBody NoteDTO noteDTO
    ) {
        try {
            NoteDTO updatedNote = noteService.updateNote(id, noteDTO);
            return ResponseEntity.ok(updatedNote);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/archive")
    public ResponseEntity<NoteDTO> toggleArchive(@PathVariable Long id) {
        try {
            NoteDTO note = noteService.toggleArchive(id);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<NoteDTO> addCategory(
        @PathVariable Long noteId,
        @PathVariable Long categoryId
    ) {
        try {
            NoteDTO note = noteService.addCategory(noteId, categoryId);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<NoteDTO> removeCategory(
        @PathVariable Long noteId,
        @PathVariable Long categoryId
    ) {
        try {
            NoteDTO note = noteService.removeCategory(noteId, categoryId);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
