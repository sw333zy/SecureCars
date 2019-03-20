
package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CategoryRepository extends CrudRepository<Category, Long> {
//    Category findById(Long id);
//    ArrayList<Category> findByName(String categoryName);
}