package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.WliiaApp;
import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.LineRepository;

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
 * Test class for the LineResource REST controller.
 *
 * @see LineResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WliiaApp.class)
@WebAppConfiguration
@IntegrationTest
public class LineResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_LINE = "AAAAA";
    private static final String UPDATED_LINE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_AUTHOR = "AAAAA";
    private static final String UPDATED_AUTHOR = "BBBBB";
    private static final String DEFAULT_FROM = "AAAAA";
    private static final String UPDATED_FROM = "BBBBB";

    private static final ZonedDateTime DEFAULT_ADD_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_ADD_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_ADD_DATE_STR = dateTimeFormatter.format(DEFAULT_ADD_DATE);

    @Inject
    private LineRepository lineRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restLineMockMvc;

    private Line line;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LineResource lineResource = new LineResource();
        ReflectionTestUtils.setField(lineResource, "lineRepository", lineRepository);
        this.restLineMockMvc = MockMvcBuilders.standaloneSetup(lineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        lineRepository.deleteAll();
        line = new Line();
        line.setLine(DEFAULT_LINE);
        line.setDescription(DEFAULT_DESCRIPTION);
        line.setAuthor(DEFAULT_AUTHOR);
        line.setFrom(DEFAULT_FROM);
        line.setAddDate(DEFAULT_ADD_DATE);
    }

    @Test
    public void createLine() throws Exception {
        int databaseSizeBeforeCreate = lineRepository.findAll().size();

        // Create the Line

        restLineMockMvc.perform(post("/api/lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(line)))
                .andExpect(status().isCreated());

        // Validate the Line in the database
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeCreate + 1);
        Line testLine = lines.get(lines.size() - 1);
        assertThat(testLine.getLine()).isEqualTo(DEFAULT_LINE);
        assertThat(testLine.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLine.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testLine.getFrom()).isEqualTo(DEFAULT_FROM);
        assertThat(testLine.getAddDate()).isEqualTo(DEFAULT_ADD_DATE);
    }

    @Test
    public void checkLineIsRequired() throws Exception {
        int databaseSizeBeforeTest = lineRepository.findAll().size();
        // set the field null
        line.setLine(null);

        // Create the Line, which fails.

        restLineMockMvc.perform(post("/api/lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(line)))
                .andExpect(status().isBadRequest());

        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllLines() throws Exception {
        // Initialize the database
        lineRepository.save(line);

        // Get all the lines
        restLineMockMvc.perform(get("/api/lines?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(line.getId())))
                .andExpect(jsonPath("$.[*].line").value(hasItem(DEFAULT_LINE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR.toString())))
                .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM.toString())))
                .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE_STR)));
    }

    @Test
    public void getLine() throws Exception {
        // Initialize the database
        lineRepository.save(line);

        // Get the line
        restLineMockMvc.perform(get("/api/lines/{id}", line.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(line.getId()))
            .andExpect(jsonPath("$.line").value(DEFAULT_LINE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR.toString()))
            .andExpect(jsonPath("$.from").value(DEFAULT_FROM.toString()))
            .andExpect(jsonPath("$.addDate").value(DEFAULT_ADD_DATE_STR));
    }

    @Test
    public void getNonExistingLine() throws Exception {
        // Get the line
        restLineMockMvc.perform(get("/api/lines/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateLine() throws Exception {
        // Initialize the database
        lineRepository.save(line);
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();

        // Update the line
        Line updatedLine = new Line();
        updatedLine.setId(line.getId());
        updatedLine.setLine(UPDATED_LINE);
        updatedLine.setDescription(UPDATED_DESCRIPTION);
        updatedLine.setAuthor(UPDATED_AUTHOR);
        updatedLine.setFrom(UPDATED_FROM);
        updatedLine.setAddDate(UPDATED_ADD_DATE);

        restLineMockMvc.perform(put("/api/lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLine)))
                .andExpect(status().isOk());

        // Validate the Line in the database
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeUpdate);
        Line testLine = lines.get(lines.size() - 1);
        assertThat(testLine.getLine()).isEqualTo(UPDATED_LINE);
        assertThat(testLine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLine.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testLine.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testLine.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
    }

    @Test
    public void deleteLine() throws Exception {
        // Initialize the database
        lineRepository.save(line);
        int databaseSizeBeforeDelete = lineRepository.findAll().size();

        // Get the line
        restLineMockMvc.perform(delete("/api/lines/{id}", line.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeDelete - 1);
    }
}
