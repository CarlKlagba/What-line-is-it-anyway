package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Music;
import com.mycompany.myapp.repository.MusicRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Music.
 */
@RestController
@RequestMapping("/api")
public class MusicResource {

    private final Logger log = LoggerFactory.getLogger(MusicResource.class);
        
    @Inject
    private MusicRepository musicRepository;
    
    /**
     * POST  /music : Create a new music.
     *
     * @param music the music to create
     * @return the ResponseEntity with status 201 (Created) and with body the new music, or with status 400 (Bad Request) if the music has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/music",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Music> createMusic(@Valid @RequestBody Music music) throws URISyntaxException {
        log.debug("REST request to save Music : {}", music);
        if (music.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("music", "idexists", "A new music cannot already have an ID")).body(null);
        }
        Music result = musicRepository.save(music);
        return ResponseEntity.created(new URI("/api/music/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("music", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /music : Updates an existing music.
     *
     * @param music the music to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated music,
     * or with status 400 (Bad Request) if the music is not valid,
     * or with status 500 (Internal Server Error) if the music couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/music",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Music> updateMusic(@Valid @RequestBody Music music) throws URISyntaxException {
        log.debug("REST request to update Music : {}", music);
        if (music.getId() == null) {
            return createMusic(music);
        }
        Music result = musicRepository.save(music);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("music", music.getId().toString()))
            .body(result);
    }

    /**
     * GET  /music : get all the music.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of music in body
     */
    @RequestMapping(value = "/music",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Music> getAllMusic() {
        log.debug("REST request to get all Music");
        List<Music> music = musicRepository.findAll();
        return music;
    }

    /**
     * GET  /music/:id : get the "id" music.
     *
     * @param id the id of the music to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the music, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/music/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Music> getMusic(@PathVariable String id) {
        log.debug("REST request to get Music : {}", id);
        Music music = musicRepository.findOne(id);
        return Optional.ofNullable(music)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /music/:id : delete the "id" music.
     *
     * @param id the id of the music to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/music/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMusic(@PathVariable String id) {
        log.debug("REST request to delete Music : {}", id);
        musicRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("music", id.toString())).build();
    }

}
