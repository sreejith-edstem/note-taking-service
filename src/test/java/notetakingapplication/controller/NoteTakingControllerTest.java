package notetakingapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.service.NoteTakingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteTakingControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private NoteTakingService noteTakingService;

    @Test
    public void testAddNotes() throws Exception {
        NoteTakingRequest request = new NoteTakingRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        when(noteTakingService.addNotes(any(NoteTakingRequest.class))).thenReturn(1L);
        mockMvc.perform(post("/notes").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(noteTakingService, times(1)).addNotes(any(NoteTakingRequest.class));
    }

    @Test
    public void getAllNotesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/notes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetNoteById() throws Exception {
        Long noteId = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/notes/" + noteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateNoteById() throws Exception {
        Long id = 1L;
        NoteTakingRequest request = new NoteTakingRequest();

        when(noteTakingService.updateNoteById(id, request)).thenReturn(id);

        mockMvc.perform(
                        put("/notes/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"Title 1\", \"content\":\"Content 1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    public void testDeleteNoteById() throws Exception {
        Long id = 1L;

        doNothing().when(noteTakingService).deleteNoteById(id);

        mockMvc.perform(delete("/notes/" + id)).andExpect(status().isOk());
    }
    @Test
    public void testToggleFavorite() throws Exception {
        Long noteId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.put("/notes/toggleFavorite/" + noteId))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetAllFavoriteNotes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notes/favorites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
