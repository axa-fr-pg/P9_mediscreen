package mediscreen.doctor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
                .content(objectMapper.writeValueAsString(note))
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildPostRandomRequest(int expectedNumberOfNotes) {
        return MockMvcRequestBuilders
                .post(ENTITY_URL + "/random/" + expectedNumberOfNotes);
    }

    private MockHttpServletRequestBuilder buildPostRandomRequest(long patientId, int expectedNumberOfNotes) {
        return MockMvcRequestBuilders
                .post(ENTITY_URL + "/patient/" + patientId + "/random/" + expectedNumberOfNotes);
    }

    @BeforeEach
    public void init() {
        reset(repository);
    }

    @Test
    public void givenBlankNote_whenPostByPatientId_thenReturnsNoteNotBlankError() throws Exception {
        // GIVEN
        NoteEntity note = mockEntityCreate();
        note.e = "";
        MockHttpServletRequestBuilder builder = buildPostRequest(note);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(NOTE_NOT_BLANK_ERROR, errors.get(0));
    }

    @Test
    public void givenPatientIdAndNewNote_whenPostByPatientId_thenReturnsCorrectNote() throws Exception {
        // GIVEN
        NoteEntity expected = mockEntityCreate();
        NoteEntity requested = new NoteEntity(0, new NoteDTO(expected));
        requested.noteId = "";
        MockHttpServletRequestBuilder builder = buildPostRequest(requested);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        NoteDTO result = objectMapper.readValue(response.getContentAsString(), NoteDTO.class);
        // THEN
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(expected.noteId.toString(), result.noteId);
        assertEquals(expected.e, result.e);
    }

    @Test
    public void givenRandomRequest_whenPostByPatientId_thenReturnsListOfCorrectSize() throws Exception {
        // GIVEN
        mockEntityCreate();
        long patientId = 123456;
        int expectedNumberOfNotes = 8;
        MockHttpServletRequestBuilder builder = buildPostRandomRequest(patientId, expectedNumberOfNotes);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<NoteDTO> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<NoteDTO>>() {});
        // THEN
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(expectedNumberOfNotes, result.size());
    }

    @Test
    public void givenRandomRequest_whenPostWithoutPatientId_thenReturnsListOfCorrectSize() throws Exception {
        // GIVEN
        mockEntityCreate();
        int expectedNumberOfNotes = 8;
        MockHttpServletRequestBuilder builder = buildPostRandomRequest(expectedNumberOfNotes);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<NoteDTO> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<NoteDTO>>() {});
        // THEN
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(expectedNumberOfNotes, result.size());
    }
}
