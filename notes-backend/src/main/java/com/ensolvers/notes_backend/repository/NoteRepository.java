package com.ensolvers.notes_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ensolvers.notes_backend.model.Note;
import com.ensolvers.notes_backend.model.User;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

        List<Note> findByUserAndArchivedOrderByUpdatedAtDesc(User user, Boolean archived);

        @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.categories c " +
                        "WHERE n.user = :user AND n.archived = :archived AND " +
                        "(:categoryId IS NULL OR c.id = :categoryId) " +
                        "ORDER BY n.updatedAt DESC")
        List<Note> findByUserAndArchivedAndCategory(
                        @Param("user") User user,
                        @Param("archived") Boolean archived,
                        @Param("categoryId") Long categoryId);

}
