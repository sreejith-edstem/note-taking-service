package notetakingapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.service.NoteTakingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class NoteTakingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private NoteTakingService noteTakingService;

    @Test
    public void testAddNotes() throws Exception{
        NoteTakingRequest request = new NoteTakingRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        when(noteTakingService.addNotes(any(NoteTakingRequest.class))).thenReturn(1L);
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(noteTakingService, times(1)).addNotes(any(NoteTakingRequest.class));
    }
    @Test
    public void getAllNotesTest() throws Exception {
        Note note1 = new Note(1L, "Title 1", "Content 1", LocalDateTime.now(), LocalDateTime.now());
        Note note2 = new Note(2L, "Title 2", "Content 2", LocalDateTime.now(), LocalDateTime.now());
        List<Note> notes = Arrays.asList(note1, note2);

        when(noteTakingService.getAllNotes()).thenReturn(notes);

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id': 1, 'title': 'Title 1', 'content': 'Content 1'}," +
                        " {'id': 2, 'title': 'Title 2', 'content': 'Content 2'}]"));
    }
    @Test
    public void testGetNoteById() throws Exception{
        Long id = 1L;
        Note note = new Note(id, "Title 1", "Content 1", LocalDateTime.now(), LocalDateTime.now());

        when(noteTakingService.getNoteById(id)).thenReturn(note);

        mockMvc.perform(get("/notes/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id': 1, 'title': 'Title 1', 'content': 'Content 1'}"));
    }
    @Test
    public void testUpdateNoteById() throws Exception{
        Long id = 1L;
        NoteTakingRequest request = new NoteTakingRequest();

        when(noteTakingService.updateNoteById(id, request)).thenReturn(id);

        mockMvc.perform(put("/notes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Title 1\", \"content\":\"Content 1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
    @Test
    public void testDeleteNoteById() throws Exception{
        Long id = 1L;

        doNothing().when(noteTakingService).deleteNoteById(id);

        mockMvc.perform(delete("/notes/" + id))
                .andExpect(status().isOk());
    }
}
