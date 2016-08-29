package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Task;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Task entity.
 */
@SuppressWarnings("unused")
public interface TaskRepository extends MongoRepository<Task,String> {

}
