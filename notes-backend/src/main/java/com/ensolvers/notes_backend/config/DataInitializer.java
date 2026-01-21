package com.ensolvers.notes_backend.config;

import com.ensolvers.notes_backend.model.Note;
import com.ensolvers.notes_backend.model.User;
import com.ensolvers.notes_backend.repository.NoteRepository;
import com.ensolvers.notes_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin;
        if (!userRepository.findByUsername("admin").isPresent()) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin = userRepository.save(admin);
            System.out.println("Default admin user created (username: admin, password: admin)");
        } else {
            admin = userRepository.findByUsername("admin").get();
        }

        if (!userRepository.findByUsername("user").isPresent()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            userRepository.save(user);
            System.out.println("Default user created (username: user, password: user)");
        }

        List<Note> allNotes = noteRepository.findAll();
        for (Note note : allNotes) {
            if (note.getUser() == null) {
                note.setUser(admin);
                noteRepository.save(note);
                System.out.println("Assigned orphaned note '" + note.getTitle() + "' to admin user");
            }
        }
    }
}
