package notetakingapplication.service;

import lombok.RequiredArgsConstructor;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.repository.NoteTakingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteTakingService {
    private final NoteTakingRepository noteTakingRepository;
    private final ModelMapper modelMapper;

    public long addNotes(NoteTakingRequest request) {
        Note note = noteTakingRepository.save(modelMapper.map(request, Note.class));
        return note.getId();
    }

    public List<Note> getAllNotesSortedByUpdatedDate() {
        return noteTakingRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Note getNoteById(long id) {
        Optional<Note> note = this.noteTakingRepository.findById(id);
        if (!note.isPresent()) {
            throw new RuntimeException("Note not found");
        }
        return note.get();
    }

    public Long updateNoteById(long id, NoteTakingRequest request) {
        Optional<Note> note = this.noteTakingRepository.findById(id);
        if (!note.isPresent()) {
            throw new RuntimeException("Note not found");
        } else {
            Note updatedNote = note.get();
            updatedNote = Note.builder()
                    .id(updatedNote.getId())
                    .createdAt(updatedNote.getCreatedAt())
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


    public boolean toggleFavorite(Long noteId) {
        Optional<Note> optionalNote = noteTakingRepository.findById(noteId);

        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            note.setFavourite(!note.isFavourite());
            noteTakingRepository.save(note);
            return true;
        } else {
            return false;
        }
    }


    public List<Note> getAllFavoriteNotes() {
        List<Note> allNotes = this.noteTakingRepository.findAll();
        List<Note> favoriteNotes = allNotes.stream()
                .filter(Note::isFavourite)
                .sorted(Comparator.comparing(Note::getUpdatedAt).reversed())
                .collect(Collectors.toList());
        return favoriteNotes;
    }

}
