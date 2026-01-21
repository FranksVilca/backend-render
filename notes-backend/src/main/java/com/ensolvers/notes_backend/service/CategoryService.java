package com.ensolvers.notes_backend.service;

import com.ensolvers.notes_backend.dto.CategoryDTO;
import com.ensolvers.notes_backend.model.Category;
import com.ensolvers.notes_backend.model.User;
import com.ensolvers.notes_backend.repository.CategoryRepository;
import com.ensolvers.notes_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

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

    public List<CategoryDTO> getAllCategories() {
        User currentUser = getCurrentUser();
        return categoryRepository.findByUser(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        User currentUser = getCurrentUser();

        if (categoryRepository.findByNameAndUser(categoryDTO.getName(), currentUser).isPresent()) {
            throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setUser(currentUser);

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public void deleteCategory(Long id) {
        User currentUser = getCurrentUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to category");
        }

        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
