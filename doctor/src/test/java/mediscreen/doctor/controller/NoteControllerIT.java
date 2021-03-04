package mediscreen.doctor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediscreen.doctor.model.NoteDTO;
import mediscreen.doctor.model.NoteEntity;
import mediscreen.doctor.model.PatientNotesDTO;
import mediscreen.doctor.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mediscreen.doctor.controller.ExceptionManager.EXCEPTION_MANAGER_NOTE_NOT_FOUND;
import static mediscreen.doctor.model.NoteDTO.NOTE_NOT_BLANK_ERROR;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private void mockEntityFindAllByPatId(long patientId, int numberOfEntities) {
        List<NoteEntity> noteEntityList = Stream.generate(() -> NoteEntity.random(patientId))
                .limit(numberOfEntities).collect(Collectors.toList());
        when(repository.findAllByPatId(patientId)).thenReturn(noteEntityList);
    }

    private List<PatientNotesDTO> mockEntityFindAllWithPaging() {
        List<PatientNotesDTO> result = new ArrayList<>();
        List<NoteEntity> allNotes = new ArrayList<>();
        long patientId = nextInt(100, 200);
        for (int branch=0; branch< nextInt(3, 15) ; branch++) {
            List<NoteDTO> branchNotes = new ArrayList<>();
            patientId += nextInt(1, 20);
            for (int index=0; index < (branch + nextInt(2, 10)); index++) {
                NoteEntity noteEntity = NoteEntity.random(patientId);
                NoteDTO noteDTO = new NoteDTO(noteEntity);
                allNotes.add(noteEntity);
                branchNotes.add(noteDTO);
            }
            result.add(new PatientNotesDTO(patientId, branchNotes));
        }
        Page<NoteEntity> page = new PageImpl<>(allNotes);
        when(repository.findByELikeOrderByPatIdAsc(any(PageRequest.class), anyString())).thenReturn(page);
        return result;
    }

    private NoteEntity mockEntityCreate()  {
        NoteEntity note = NoteEntity.random();
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }

    private NoteEntity mockEntitySave(String noteId)  {
        NoteEntity note = NoteEntity.random();
        note.noteId = noteId;
        when(repository.save(any(NoteEntity.class))).thenReturn(note);
        return note;
    }

    private MockHttpServletRequestBuilder buildPutRequest(NoteEntity note) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(ENTITY_URL + "/" + note.noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NoteDTO(note)))
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildPostRequest(NoteEntity note) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(ENTITY_URL + "/patients/" + note.patId)
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
                .post(ENTITY_URL + "/patients/" + patientId + "/random/" + expectedNumberOfNotes);
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
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL + "/patients/" + patientId)).andReturn().getResponse();
        PatientNotesDTO result = objectMapper.readValue(response.getContentAsString(), PatientNotesDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(patientId, result.patId);
        assertEquals(numberOfEntities, result.noteDTOList.size());
    }

    @Test
    public void givenNoPatientId_whenGetListGroupedByPatientId_thenReturnsCorrectNoteTree() throws Exception {
        // GIVEN
        List<PatientNotesDTO> patientNotesDTOList = mockEntityFindAllWithPaging();
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get( ENTITY_URL)).andReturn().getResponse();
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String result = response.getContentAsString();
        patientNotesDTOList.forEach( patientNotesDTO -> {
            assertTrue(result.contains("\"patId\":" + patientNotesDTO.patId));
            patientNotesDTO.noteDTOList.forEach(note -> assertTrue(result.contains("\"e\":\"" + note.e + "\"")));
        });
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
        assertEquals(expected.noteId, result.noteId);
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

    @Test
    public void givenExistingNote_whenPut_thenReturnsCorrectNote() throws Exception {
        // GIVEN
        NoteEntity noteBefore = mockEntityFind(true);
        NoteEntity noteAfter = mockEntitySave(noteBefore.noteId);
        MockHttpServletRequestBuilder builder = buildPutRequest(noteAfter);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        NoteDTO result = objectMapper.readValue(response.getContentAsString(), NoteDTO.class);
        // THEN
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(noteAfter.noteId, result.noteId);
        assertEquals(noteAfter.e, result.e);
    }

    @Test
    public void givenExistingNote_whenPutWithEmptyContent_thenReturnsNoteBlankError() throws Exception {
        // GIVEN
        NoteEntity noteBefore = mockEntityFind(true);
        NoteEntity noteAfter = mockEntitySave(noteBefore.noteId);
        noteAfter.e = "";
        MockHttpServletRequestBuilder builder = buildPutRequest(noteAfter);
        // WHEN
        MockHttpServletResponse response = mockMvc.perform(builder).andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {});
        // THEN
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(1, errors.size());
        assertEquals(NOTE_NOT_BLANK_ERROR, errors.get(0));
    }
}
