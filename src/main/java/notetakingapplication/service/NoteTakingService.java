package notetakingapplication.service;

import lombok.RequiredArgsConstructor;
import notetakingapplication.constant.Folder;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.repository.NoteTakingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
        Note note = this.noteTakingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return note;
    }

    public Note updateNoteById(long id, NoteTakingRequest request) {
        Note updatedNote = this.noteTakingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        updatedNote = Note.builder()
                .id(updatedNote.getId())
                .createdAt(updatedNote.getCreatedAt())
                .isFavourite(updatedNote.isFavourite())
                .folder(updatedNote.getFolder())
                .title(request.getTitle())
                .content(request.getContent())
                .updatedAt(LocalDateTime.now())
                .build();
        noteTakingRepository.save(updatedNote);
        return updatedNote;
    }

    public long deleteNoteById(long id) {
        if (!noteTakingRepository.existsById(id)) {
            throw new RuntimeException("Note not found");
        }
        noteTakingRepository.deleteById(id);
        return id;
    }

    public Note toggleFavorite(Long noteId) {
        Note note = this.noteTakingRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note = Note.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .folder(note.getFolder())
                .isFavourite(!note.isFavourite())
                .build();
        noteTakingRepository.save(note);
        return note;
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
        Note note = this.noteTakingRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note = Note.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .isFavourite(note.isFavourite())
                .folder(note.getFolder())
                .isDeleted(!note.isDeleted())
                .build();
        noteTakingRepository.save(note);
        return noteId;
    }

    public List<Note> getAllDeletedNotesSortedByUpdatedDate() {
        return noteTakingRepository.findAllByIsDeletedTrueOrderByUpdatedAtDesc();
    }

    public List<Note> getAllUndeletedNotesSortedByUpdatedDate() {
        return noteTakingRepository.findAllByIsDeletedFalseOrderByUpdatedAtDesc();
    }


    public List<Note> getAllNotesByFolder(Folder folder) {
        List<Note> allNotes = this.noteTakingRepository.findAll();
        List<Note> notesByFolder = allNotes.stream()
                .filter(note -> note.getFolder() == folder && !note.isDeleted())
                .collect(Collectors.toList());
        return notesByFolder;
    }

    public List<Note> getNotesByTitle(String title) {
        List<Note> allNotes = this.noteTakingRepository.findAll();
        List<Note> notesByTitle = allNotes.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(title.toLowerCase()) && !note.isDeleted())
                .collect(Collectors.toList());
        return notesByTitle;
    }

}
