package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Line;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Line entity.
 */
@SuppressWarnings("unused")
public interface LineRepository extends MongoRepository<Line,String> {

}
