package notetakingapplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.repository.NoteTakingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

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
        request.setTitle("Test Title");
        request.setContent("Test Content");
        Note note = new Note();
        when(modelMapper.map(request, Note.class)).thenReturn(note);
        when(noteTakingRepository.save(note)).thenReturn(note);
        Long resultId = noteTakingService.addNotes(request);
        verify(modelMapper, times(1)).map(request, Note.class);
        verify(noteTakingRepository, times(1)).save(note);
        assertEquals(note.getId(), resultId);
    }

    @Test
    public void testGetAllNotes() {
        Note note = new Note();
        Note note1 = new Note();
        List<Note> notes = Arrays.asList(note, note1);
        when(noteTakingRepository.findAll()).thenReturn(notes);
        List<Note> result = noteTakingService.getAllNotes();
        verify(noteTakingRepository, times(1)).findAll();
        assertEquals(notes.size(), result.size());
        assertEquals(notes.get(0).getTitle(), result.get(0).getTitle());
        assertEquals(notes.get(1).getTitle(), result.get(1).getTitle());
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
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        Note note =
                Note.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(noteTakingRepository.findById(id)).thenReturn(Optional.of(note));
        when(noteTakingRepository.save(any(Note.class))).thenReturn(note);

        Long resultId = noteTakingService.updateNoteById(id, request);

        verify(noteTakingRepository, times(1)).findById(id);
        verify(noteTakingRepository, times(1)).save(any(Note.class));
        assertEquals(note.getId(), resultId);
    }

    @Test
    public void testUpdateNoteById_NotFound() {
        long id = 1L;
        NoteTakingRequest request = new NoteTakingRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        when(noteTakingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> noteTakingService.updateNoteById(id, request));
        verify(noteTakingRepository, times(1)).findById(id);
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
}
