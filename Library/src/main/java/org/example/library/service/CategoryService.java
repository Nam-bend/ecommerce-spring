package org.example.library.service;

import org.example.library.entity.Category;

import java.util.List;


public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category findByName(String name);

    boolean existsByName(String name);

    Category save(Category category);

    Category update(Category category);

    void deleteById(Long id);

    void enabledById(Long id);

    Category saveAndFlush(Category category);
}
