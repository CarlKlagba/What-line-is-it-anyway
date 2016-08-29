package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Music;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Music entity.
 */
@SuppressWarnings("unused")
public interface MusicRepository extends MongoRepository<Music,String> {

}
