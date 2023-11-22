package notetakingapplication.service;

import notetakingapplication.constant.Folder;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.repository.NoteTakingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoteTakingServiceTest {
    private NoteTakingRepository noteTakingRepository;
    private ModelMapper modelMapper;
    private NoteTakingService noteTakingService = new NoteTakingService(null, null);

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        noteTakingRepository = Mockito.mock(NoteTakingRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);
        noteTakingService = new NoteTakingService(noteTakingRepository, modelMapper);
    }

    @Test
    public void testAddNotes() {
        NoteTakingRequest request = new NoteTakingRequest();

        Note note = new Note();

        when(modelMapper.map(request, Note.class)).thenReturn(note);
        when(noteTakingRepository.save(note)).thenReturn(note);

        Note result = noteTakingService.addNotes(request);

        assertEquals(note, result);
    }

    @Test
    public void testGetAllNotes() {
        Note note1 = new Note();
        Note note2 = new Note();
        List<Note> expectedNotes = Arrays.asList(note1, note2);

        when(noteTakingRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(expectedNotes);

        List<Note> actualNotes = noteTakingService.getAllNotesSortedByUpdatedDate();

        assertEquals(expectedNotes, actualNotes);
    }

    @Test
    public void testGetNoteById() {
        long id = 1L;
        Note note = new Note();
        when(noteTakingRepository.findById(id)).thenReturn(Optional.of(note));
        Note result = noteTakingService.getNoteById(id);
        verify(noteTakingRepository, times(1)).findById(id);
        assertEquals(note.getTitle(), result.getTitle());
        assertEquals(note.getContent(), result.getContent());
    }

    @Test
    public void testGetNoteById_NotFound() {
        long id = 1L;
        when(noteTakingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> noteTakingService.getNoteById(id));
        verify(noteTakingRepository, times(1)).findById(id);
    }

    @Test
    public void testUpdateNoteById() {
        long id = 1L;
        NoteTakingRequest request = new NoteTakingRequest();

        Note note = new Note(1L, "Old Title", "Old Content", LocalDateTime.now(), LocalDateTime.now(), false, false, Folder.valueOf("Personal"));
        when(noteTakingRepository.findById(id)).thenReturn(Optional.of(note));

        Note updatedNote = noteTakingService.updateNoteById(id, request);

        assertEquals(request.getTitle(), updatedNote.getTitle());
        assertEquals(request.getContent(), updatedNote.getContent());
        verify(noteTakingRepository, times(1)).findById(id);
        verify(noteTakingRepository, times(1)).save(any(Note.class));
    }

    @Test
    public void testDeleteNoteById() {
        long id = 1L;

        when(noteTakingRepository.existsById(id)).thenReturn(true);
        noteTakingService.deleteNoteById(id);

        verify(noteTakingRepository, times(1)).existsById(id);
        verify(noteTakingRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteNoteById_NotFound() {
        long id = 1L;

        when(noteTakingRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> noteTakingService.deleteNoteById(id));
        verify(noteTakingRepository, times(1)).existsById(id);
    }

    @Test
    public void testToggleFavorite() {
        Long noteId = 1L;

        Note note = new Note(1L, "Title", "Content", LocalDateTime.now(), LocalDateTime.now(), false, false, Folder.valueOf("Personal"));
        when(noteTakingRepository.findById(noteId)).thenReturn(Optional.of(note));

        Note updatedNote = noteTakingService.toggleFavorite(noteId);

        assertEquals(!note.isFavourite(), updatedNote.isFavourite());
        verify(noteTakingRepository, times(1)).findById(noteId);
        verify(noteTakingRepository, times(1)).save(any(Note.class));
    }

    @Test
    public void testToggleSoftDelete() {
        Long noteId = 1L;

        Note note = new Note(1L, "Title", "Content", LocalDateTime.now(), LocalDateTime.now(), false, false, Folder.valueOf("Personal"));
        when(noteTakingRepository.findById(noteId)).thenReturn(Optional.of(note));

        Long returnedNoteId = noteTakingService.toggleSoftDelete(noteId);

        assertEquals(noteId, returnedNoteId);
        verify(noteTakingRepository, times(1)).findById(noteId);
        verify(noteTakingRepository, times(1)).save(any(Note.class));
    }

    @Test
    public void testGetAllFavoriteNotes() {
        Note note1 = Note.builder()
                .isFavourite(true)
                .isDeleted(false)
                .title("Title1")
                .updatedAt(LocalDateTime.now())
                .build();

        Note note2 = Note.builder()
                .isFavourite(false)
                .isDeleted(false)
                .title("Title2")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        List<Note> allNotes = Arrays.asList(note1, note2);

        when(noteTakingRepository.findAll()).thenReturn(allNotes);

        List<Note> result = noteTakingService.getAllFavoriteNotes("Title1");

        assertEquals(1, result.size());

        assertTrue(result.get(0).getTitle().toLowerCase().contains("title1".toLowerCase()));
    }


    @Test
    public void testGetAllDeletedNotesSortedByUpdatedDate() {
        Note note1 = Note.builder()
                .isDeleted(true)
                .title("Title1")
                .updatedAt(LocalDateTime.now())
                .build();

        Note note2 = Note.builder()
                .isDeleted(true)
                .title("Title2")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Note note3 = Note.builder()
                .isDeleted(false)
                .title("Title3")
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();

        List<Note> allNotes = Arrays.asList(note1, note2, note3);

        when(noteTakingRepository.findAll()).thenReturn(allNotes);

        List<Note> result = noteTakingService.getAllDeletedNotesSortedByUpdatedDate("Title1");

        assertEquals(1, result.size());

        assertTrue(result.get(0).getTitle().toLowerCase().contains("title1".toLowerCase()));
        assertTrue(result.get(0).isDeleted());
    }


    @Test
    public void testGetAllUnDeletedNotesSortedByUpdatedDate() {
        Note note = new Note();
        when(noteTakingRepository.findAllByIsDeletedFalseOrderByUpdatedAtDesc()).thenReturn(Collections.singletonList(note));

        List<Note> notes = noteTakingService.getAllUndeletedNotesSortedByUpdatedDate();

        assertFalse(notes.isEmpty());
        verify(noteTakingRepository, times(1)).findAllByIsDeletedFalseOrderByUpdatedAtDesc();
    }

    @Test
    public void testGetAllNotesByFolder() {
        Folder folder = Folder.Personal;

        Note note1 = Note.builder()
                .title("Title1")
                .folder(folder)
                .isDeleted(false)
                .build();

        Note note2 = Note.builder()
                .title("Title2")
                .folder(folder)
                .isDeleted(true)
                .build();

        List<Note> allNotes = Arrays.asList(note1, note2);

        when(noteTakingRepository.findAll()).thenReturn(allNotes);

        List<Note> result = noteTakingService.getAllNotesByFolder(folder, "Title1");

        assertEquals(1, result.size());

        assertTrue(result.get(0).getTitle().toLowerCase().contains("title1".toLowerCase()));
        assertEquals(folder, result.get(0).getFolder());
        assertFalse(result.get(0).isDeleted());
    }


    @Test
    public void testSearchNotesByTitle() {
        String title = "Test Title";
        List<Note> allNotes = new ArrayList<>();
        when(noteTakingRepository.findAll()).thenReturn(allNotes);
        List<Note> actualNotes = noteTakingService.searchNotesByTitle(title);
        List<Note> expectedNotes = allNotes.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(title.toLowerCase()) && !note.isDeleted())
                .collect(Collectors.toList());
        assertEquals(expectedNotes, actualNotes);
        Mockito.verify(noteTakingRepository).findAll();
    }
}
