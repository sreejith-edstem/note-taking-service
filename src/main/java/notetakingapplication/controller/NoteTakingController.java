package notetakingapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.service.NoteTakingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteTakingController {
    private final NoteTakingService noteTakingService;

    @PostMapping
    public @ResponseBody Long addNotes(@Valid @RequestBody NoteTakingRequest request) {
        return this.noteTakingService.addNotes(request);
    }

    @GetMapping
    public @ResponseBody List<Note> getAllNotes() {
        return noteTakingService.getAllNotesSortedByUpdatedDate();
    }

    @GetMapping("/{id}")
    public @ResponseBody Note getNoteById(@PathVariable Long id) {
        return this.noteTakingService.getNoteById(id);
    }

    @PutMapping("/{id}")
    public @ResponseBody Long updateNoteById(
            @PathVariable long id, @Valid @RequestBody NoteTakingRequest request) {
        return noteTakingService.updateNoteById(id, request);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody void deleteNoteById(@PathVariable long id) {
        noteTakingService.deleteNoteById(id);
    }

    @PutMapping("/{noteId}/favorite")
    public ResponseEntity<?> addNoteToFavorites(@PathVariable Long noteId) {
        boolean isAdded = noteTakingService.addNoteToFavorites(noteId);
        if (isAdded) {
            return ResponseEntity.ok().body("Note has been added to favorites.");
        } else {
            return ResponseEntity.badRequest().body("Note not found.");
        }
    }

    @PutMapping("/{noteId}/unfavorite")
    public ResponseEntity<?> removeNoteFromFavorites(@PathVariable Long noteId) {
        boolean isRemoved = noteTakingService.removeNoteFromFavorites(noteId);
        if (isRemoved) {
            return ResponseEntity.ok().body("Note has been removed from favorites.");
        } else {
            return ResponseEntity.badRequest().body("Note not found.");
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getAllFavoriteNotes() {
        List<Note> favoriteNotes = noteTakingService.getAllFavoriteNotes();
        return ResponseEntity.ok().body(favoriteNotes);
    }

}
