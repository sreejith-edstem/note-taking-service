package notetakingapplication.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.repository.NoteTakingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteTakingService {
    private final NoteTakingRepository noteTakingRepository;
    private final ModelMapper modelMapper;

    public long addNotes(@Valid NoteTakingRequest request) {
        Note note = noteTakingRepository.save(modelMapper.map(request, Note.class));
        return note.getId();
    }

    public List<Note> getAllNotes() {
        Iterable<Note> movies = this.noteTakingRepository.findAll();
        List<Note> moviesList = new ArrayList<>();
        movies.forEach(moviesList::add);
        return moviesList;
    }

    public Note getNoteById(long id) {
        Optional<Note> note = this.noteTakingRepository.findById(id);
        if (!note.isPresent()) {
            throw new RuntimeException("Note not found");
        }
        return note.get();
    }

    public Long updateNoteById(long id, @Valid NoteTakingRequest request) {
        Optional<Note> note = this.noteTakingRepository.findById(id);
        if (!note.isPresent()) {
            throw new RuntimeException("Note not found");
        } else {
            Note updatedNote = note.get();
            updatedNote = Note.builder()
                    .id(updatedNote.getId())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .updatedAt(LocalDateTime.now())
                    .build();
            noteTakingRepository.save(updatedNote);
            return updatedNote.getId();
        }

    }

    public void deleteNoteById(long id) {
        if (!noteTakingRepository.existsById(id)) {
            throw new RuntimeException("Note not found");
        }
        noteTakingRepository.deleteById(id);
    }
}
