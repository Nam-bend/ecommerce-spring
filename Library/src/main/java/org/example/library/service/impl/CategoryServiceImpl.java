package org.example.library.service.impl;

import org.example.library.entity.Category;
import org.example.library.repository.CategoryRepository;
import org.example.library.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category findByName(String name) {
        return repo.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repo.existsByName(name);
    }

    @Override
    public Category save(Category category) {
        // Validation
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }

        // Kiểm tra trùng tên
        if (existsByName(category.getName().trim())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại!");
        }

        // Tạo category mới
        Category newCategory = new Category();
        newCategory.setName(category.getName().trim());
        newCategory.set_activated(true);
        newCategory.set_deleted(false);

        return repo.save(newCategory);
    }

    @Override
    public Category update(Category category) {
        // Validation
        if (category.getId() == null) {
            throw new IllegalArgumentException("ID danh mục không được null!");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }

        // Lấy category hiện tại
        Category existing = repo.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + category.getId()));

        // Kiểm tra trùng tên (bỏ qua chính nó)
        String newName = category.getName().trim();
        Category duplicateCategory = findByName(newName);
        if (duplicateCategory != null && !duplicateCategory.getId().equals(category.getId())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại!");
        }

        // Cập nhật
        existing.setName(newName);
        // Giữ nguyên trạng thái activated và deleted, không update từ form

        return repo.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        Category category = repo.getById(id);
        category.set_deleted(true);
        category.set_activated(false);
        repo.save(category);
    }

    @Override
    public void enabledById(Long id) {
        Category category = repo.getById(id);
        category.set_activated(true);
        category.set_deleted(false);
        repo.save(category);
    }

    @Override
    public Category saveAndFlush(Category category) {
        Category saved = save(category);
        repo.flush();
        return saved;
    }
}