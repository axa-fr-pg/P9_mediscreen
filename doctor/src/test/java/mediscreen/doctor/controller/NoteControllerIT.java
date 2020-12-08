package mediscreen.doctor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldInitializationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static mediscreen.doctor.model.NoteDTO.NOTE_NOT_BLANK_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    NoteRepository repository;

    private final static String ENTITY_URL = "/notes";

    private NoteEntity mockEntityCreate()  {
        NoteEntity note = NoteEntity.random();
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }

    private MockHttpServletRequestBuilder buildPostRequest(NoteEntity note) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(ENTITY_URL + "/patient/" + note.patId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NoteDTO("", note.e)))
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    public void init() {
        reset(repository);
    }

    @Test
    public void givenBlankNote_whenPostByPatientId_thenReturnsNoteNotBlankError() throws Exception {
        // GIVEN
        NoteEntity request = mockEntityCreate();
        request.e = "";
        MockHttpServletRequestBuilder builder = buildPostRequest(request);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(NOTE_NOT_BLANK_ERROR, errors.get(0));
    }



}
