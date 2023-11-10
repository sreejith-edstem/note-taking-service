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

    public Note addNotes(NoteTakingRequest request) {
        Note note = modelMapper.map(request, Note.class);
        note = noteTakingRepository.save(note);
        return note;
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

    public Note updateNoteById(long id, NoteTakingRequest request) {
        Optional<Note> note = this.noteTakingRepository.findById(id);
        if (!note.isPresent()) {
            throw new RuntimeException("Note not found");
        } else {
            Note updatedNote = note.get();
            updatedNote = Note.builder()
                    .id(updatedNote.getId())
                    .createdAt(updatedNote.getCreatedAt())
                    .isFavourite(updatedNote.isFavourite())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .updatedAt(LocalDateTime.now())
                    .build();
            noteTakingRepository.save(updatedNote);
            return updatedNote;
        }
    }

    public long deleteNoteById(long id) {
        if (!noteTakingRepository.existsById(id)) {
            throw new RuntimeException("Note not found");
        }
        noteTakingRepository.deleteById(id);
        return id;
    }

    public Note toggleFavorite(Long noteId) {
        Optional<Note> optionalNote = noteTakingRepository.findById(noteId);

        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            Note updatedNote = Note.builder()
                    .id(note.getId())
                    .title(note.getTitle())
                    .content(note.getContent())
                    .createdAt(note.getCreatedAt())
                    .updatedAt(note.getUpdatedAt())
                    .isFavourite(!note.isFavourite())
                    .build();
            noteTakingRepository.save(updatedNote);
            return updatedNote;
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    public List<Note> getAllFavoriteNotes() {
        List<Note> allNotes = this.noteTakingRepository.findAll();
        List<Note> favoriteNotes = allNotes.stream()
                .filter(Note::isFavourite)
                .filter(note -> !note.isDeleted())
                .sorted(Comparator.comparing(Note::getUpdatedAt).reversed())
                .collect(Collectors.toList());
        return favoriteNotes;
    }

    public long toggleSoftDelete(Long noteId) {
        Optional<Note> optionalNote = noteTakingRepository.findById(noteId);

        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            Note updatedNote = Note.builder()
                    .id(note.getId())
                    .title(note.getTitle())
                    .content(note.getContent())
                    .createdAt(note.getCreatedAt())
                    .updatedAt(note.getUpdatedAt())
                    .isFavourite(note.isFavourite())
                    .isDeleted(!note.isDeleted())
                    .build();
            noteTakingRepository.save(updatedNote);
            return noteId;
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    public List<Note> getAllDeletedNotesSortedByUpdatedDate() {
        return noteTakingRepository.findAllByIsDeletedTrueOrderByUpdatedAtDesc();
    }

    public List<Note> getAllUndeletedNotesSortedByUpdatedDate() {
        return noteTakingRepository.findAllByIsDeletedFalseOrderByUpdatedAtDesc();
    }
}
