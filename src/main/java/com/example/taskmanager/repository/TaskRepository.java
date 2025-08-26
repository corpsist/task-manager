package com.example.taskmanager.repository;

import com.example.taskmanager.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



//Extends JPARepository: gives CRUD methods out of the box
@Repository
public interface TaskRepository extends JpaRepository<Task , Long>{ // we are handling task entities with Long primary keys
    // no code needed — you already get:
    // save(), findById(), findAll(), deleteById(), etc.

    // you can also define custom methods, e.g.:
    // List<Task> findByCompleted(boolean completed);

    //Pagination + filtering by completion
    Page<Task> findByCompleted(Boolean completed, Pageable pageable);

    //Filtering by title containing a keyword
    Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

}
