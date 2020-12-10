package mediscreen.doctor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.repository.NoteRepository;
import mediscreen.doctor.service.NoteNotFoundException;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mediscreen.doctor.controller.ExceptionManager.EXCEPTION_MANAGER_NOTE_NOT_FOUND;
import static mediscreen.doctor.model.NoteDTO.NOTE_NOT_BLANK_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    private NoteEntity mockEntityFind(boolean exists)  {
        NoteEntity note = NoteEntity.random();
        Optional<NoteEntity> optional = exists ? Optional.of(note) : Optional.empty();
        when(repository.findById(note.noteId)).thenReturn(optional);
        return note;
    }

    private List<NoteEntity> mockEntityFindAllByPatId(long patientId, int numberOfEntities) {
        List<NoteEntity> noteEntityList = Stream.generate(() -> NoteEntity.random(patientId))
                .limit(numberOfEntities).collect(Collectors.toList());
        when(repository.findAllByPatId(patientId)).thenReturn(noteEntityList);
        return noteEntityList;
    }

    private List<NoteEntity> mockEntityFindAll(int numberOfEntities) {
        List<NoteEntity> noteEntityList = Stream.generate(NoteEntity::random)
                .limit(numberOfEntities).collect(Collectors.toList());
        when(repository.findAll()).thenReturn(noteEntityList);
        return noteEntityList;
    }

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
    public void givenExistingNote_whenGet_thenReturnsCorrectNote() throws Exception {
        // GIVEN
        NoteEntity note = mockEntityFind(true);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/" + note.noteId)).andReturn().getResponse();
        NoteDTO result = objectMapper.readValue(response.getContentAsString(), NoteDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(note.noteId, result.noteId);
        assertEquals(note.e, result.e);
    }

    @Test
    public void givenMissingNote_whenGet_thenThrowsNoteNotFoundException() throws Exception {
        // GIVEN
        NoteEntity note = mockEntityFind(false);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/" + note.noteId)).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(EXCEPTION_MANAGER_NOTE_NOT_FOUND, response.getContentAsString());
    }

    @Test
    public void givenPatientId_whenGetByPatientId_thenReturnsCorrectNoteList() throws Exception {
        // GIVEN
        long patientId = 123456;
        int numberOfEntities = 5;
        mockEntityFindAllByPatId(patientId, numberOfEntities);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/patient/" + patientId)).andReturn().getResponse();
        List<NoteDTO> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<NoteDTO>>() {});
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(numberOfEntities, result.size());
    }

    @Test
    public void givenNoPatientId_whenGetAll_thenReturnsCorrectNoteList() throws Exception {
        // GIVEN
        int numberOfEntities = 10;
        mockEntityFindAll(numberOfEntities);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL)).andReturn().getResponse();
        List<NoteDTO> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<NoteDTO>>() {});
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(numberOfEntities, result.size());
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
