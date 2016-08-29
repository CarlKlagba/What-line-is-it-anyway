package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.WliiaApp;
import com.mycompany.myapp.domain.Music;
import com.mycompany.myapp.repository.MusicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MusicResource REST controller.
 *
 * @see MusicResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WliiaApp.class)
@WebAppConfiguration
@IntegrationTest
public class MusicResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_LINK = "AAAAA";
    private static final String UPDATED_LINK = "BBBBB";

    private static final ZonedDateTime DEFAULT_ADD_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_ADD_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_ADD_DATE_STR = dateTimeFormatter.format(DEFAULT_ADD_DATE);

    @Inject
    private MusicRepository musicRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMusicMockMvc;

    private Music music;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MusicResource musicResource = new MusicResource();
        ReflectionTestUtils.setField(musicResource, "musicRepository", musicRepository);
        this.restMusicMockMvc = MockMvcBuilders.standaloneSetup(musicResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        musicRepository.deleteAll();
        music = new Music();
        music.setName(DEFAULT_NAME);
        music.setDescription(DEFAULT_DESCRIPTION);
        music.setLink(DEFAULT_LINK);
        music.setAddDate(DEFAULT_ADD_DATE);
    }

    @Test
    public void createMusic() throws Exception {
        int databaseSizeBeforeCreate = musicRepository.findAll().size();

        // Create the Music

        restMusicMockMvc.perform(post("/api/music")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(music)))
                .andExpect(status().isCreated());

        // Validate the Music in the database
        List<Music> music = musicRepository.findAll();
        assertThat(music).hasSize(databaseSizeBeforeCreate + 1);
        Music testMusic = music.get(music.size() - 1);
        assertThat(testMusic.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMusic.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMusic.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testMusic.getAddDate()).isEqualTo(DEFAULT_ADD_DATE);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = musicRepository.findAll().size();
        // set the field null
        music.setName(null);

        // Create the Music, which fails.

        restMusicMockMvc.perform(post("/api/music")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(music)))
                .andExpect(status().isBadRequest());

        List<Music> music = musicRepository.findAll();
        assertThat(music).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllMusic() throws Exception {
        // Initialize the database
        musicRepository.save(music);

        // Get all the music
        restMusicMockMvc.perform(get("/api/music?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(music.getId())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
                .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE_STR)));
    }

    @Test
    public void getMusic() throws Exception {
        // Initialize the database
        musicRepository.save(music);

        // Get the music
        restMusicMockMvc.perform(get("/api/music/{id}", music.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(music.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.addDate").value(DEFAULT_ADD_DATE_STR));
    }

    @Test
    public void getNonExistingMusic() throws Exception {
        // Get the music
        restMusicMockMvc.perform(get("/api/music/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateMusic() throws Exception {
        // Initialize the database
        musicRepository.save(music);
        int databaseSizeBeforeUpdate = musicRepository.findAll().size();

        // Update the music
        Music updatedMusic = new Music();
        updatedMusic.setId(music.getId());
        updatedMusic.setName(UPDATED_NAME);
        updatedMusic.setDescription(UPDATED_DESCRIPTION);
        updatedMusic.setLink(UPDATED_LINK);
        updatedMusic.setAddDate(UPDATED_ADD_DATE);

        restMusicMockMvc.perform(put("/api/music")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMusic)))
                .andExpect(status().isOk());

        // Validate the Music in the database
        List<Music> music = musicRepository.findAll();
        assertThat(music).hasSize(databaseSizeBeforeUpdate);
        Music testMusic = music.get(music.size() - 1);
        assertThat(testMusic.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMusic.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMusic.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testMusic.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
    }

    @Test
    public void deleteMusic() throws Exception {
        // Initialize the database
        musicRepository.save(music);
        int databaseSizeBeforeDelete = musicRepository.findAll().size();

        // Get the music
        restMusicMockMvc.perform(delete("/api/music/{id}", music.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Music> music = musicRepository.findAll();
        assertThat(music).hasSize(databaseSizeBeforeDelete - 1);
    }
}
