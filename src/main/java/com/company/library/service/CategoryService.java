package com.company.library.service;

import com.company.library.model.Category;
import com.company.library.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    protected Category getCategoryById(String id){
        return categoryRepository.findById(id).orElseThrow();
    }

    protected Category getCategoryByName(String categoryName){
        return categoryRepository.getCategoryByName(categoryName);
    }

}
