package notetakingapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notetakingapplication.constant.Folder;
import notetakingapplication.model.Note;
import notetakingapplication.service.NoteTakingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void addNotesTest() throws Exception {
        String json = "{\"title\":\"Test Note\",\"content\":\"This is a test note.\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
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
    public void updateNoteByIdTest() throws Exception {
        String json = "{\"title\":\"Updated Note\",\"content\":\"This is an updated note.\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNoteByIdTest() throws Exception {
        long idToDelete = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/notes/" + idToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(Long.toString(idToDelete)));
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

    @Test
    public void toggleSoftDeleteTest() throws Exception {
        Long noteId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/notes/toggleSoftDelete/" + noteId))
                .andExpect(status().isOk())
                .andExpect(content().string(Long.toString(noteId)));
    }

    @Test
    public void getAllDeletedNotesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notes/deleted"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllUndeletedNotesTest() throws Exception {


        Note note1 = Note.builder().id(1L).content("Test Note 1").build();
        Note note2 = Note.builder().id(2L).content("Test Note 2").build();


        List<Note> expectedNotes = Arrays.asList(note1, note2);

        when(noteTakingService.getAllUndeletedNotesSortedByUpdatedDate()).thenReturn(expectedNotes);

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/undeleted"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }


    @Test
    public void testGetAllNotesByFolder() throws Exception {
        String folder = Folder.Personal.name();
        mockMvc.perform(MockMvcRequestBuilders.get("/notes/byFolder/" + folder))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    @Test
    public void searchNotesByTitleTest() throws Exception {
        String title = "Test Note";
        Note note1 = Note.builder().id(1L).title("Test Note 1").build();
        Note note2 = Note.builder().id(2L).title("Test Note 2").build();

        List<Note> expectedNotes = Arrays.asList(note1, note2);

        when(noteTakingService.searchNotesByTitle(title)).thenReturn(expectedNotes);

        mockMvc.perform(MockMvcRequestBuilders.get("/notes/search")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }


}
