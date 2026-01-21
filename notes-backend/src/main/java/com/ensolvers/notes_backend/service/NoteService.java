package com.ensolvers.notes_backend.service;

import com.ensolvers.notes_backend.dto.CategoryDTO;
import com.ensolvers.notes_backend.dto.NoteDTO;
import com.ensolvers.notes_backend.model.Category;
import com.ensolvers.notes_backend.model.Note;
import com.ensolvers.notes_backend.model.User;
import com.ensolvers.notes_backend.repository.CategoryRepository;
import com.ensolvers.notes_backend.repository.NoteRepository;
import com.ensolvers.notes_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<NoteDTO> getAllNotes(Boolean archived, Long categoryId) {
        User currentUser = getCurrentUser();
        if (archived == null) {
            archived = false;
        }
        List<Note> notes;
        if (categoryId != null) {
            notes = noteRepository.findByUserAndArchivedAndCategory(currentUser, archived, categoryId);
        } else {
            notes = noteRepository.findByUserAndArchivedOrderByUpdatedAtDesc(currentUser, archived);
        }
        return notes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public NoteDTO getNoteById(Long id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        return convertToDTO(note);
    }

    public NoteDTO createNote(NoteDTO noteDTO) {
        User currentUser = getCurrentUser();

        Note note = new Note();
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setArchived(false);
        note.setUser(currentUser);

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    public NoteDTO updateNote(Long id, NoteDTO noteDTO) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());

        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public void deleteNote(Long id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        noteRepository.deleteById(id);
    }

    public NoteDTO toggleArchive(Long id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        note.setArchived(!note.isArchived());
        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public NoteDTO addCategory(Long noteId, Long categoryId) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        note.getCategories().add(category);
        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public NoteDTO removeCategory(Long noteId, Long categoryId) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        note.getCategories().removeIf(c -> c.getId().equals(categoryId));
        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setArchived(note.isArchived());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());

        Set<CategoryDTO> categoryDTOs = note.getCategories().stream()
                .map(this::convertCategoryToDTO)
                .collect(Collectors.toSet());
        dto.setCategories(categoryDTOs);

        return dto;
    }

    private CategoryDTO convertCategoryToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
