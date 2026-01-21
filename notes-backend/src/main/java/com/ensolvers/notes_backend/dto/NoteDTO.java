package com.ensolvers.notes_backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<CategoryDTO> categories;
}
