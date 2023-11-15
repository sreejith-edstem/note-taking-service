package notetakingapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notetakingapplication.constant.Folder;
import notetakingapplication.contract.request.NoteTakingRequest;
import notetakingapplication.model.Note;
import notetakingapplication.service.NoteTakingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5173/")
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteTakingController {
    private final NoteTakingService noteTakingService;

    @PostMapping
    public @ResponseBody Note addNotes(@Valid @RequestBody NoteTakingRequest request) {
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
    public @ResponseBody Note updateNoteById(
            @PathVariable long id, @Valid @RequestBody NoteTakingRequest request) {
        return noteTakingService.updateNoteById(id, request);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody long deleteNoteById(@PathVariable long id) {
        noteTakingService.deleteNoteById(id);
        return id;
    }

    @PutMapping("/toggleFavorite/{noteId}")
    public Note toggleFavorite(@PathVariable Long noteId) {
        Note isToggled = noteTakingService.toggleFavorite(noteId);
        return isToggled;
    }

    @GetMapping("/favorites")
    public @ResponseBody List<Note> getAllFavoriteNotes() {
        return noteTakingService.getAllFavoriteNotes();
    }

    @DeleteMapping("/toggleSoftDelete/{noteId}")
    public @ResponseBody long toggleSoftDelete(@PathVariable Long noteId) {
        noteTakingService.toggleSoftDelete(noteId);
        return noteId;
    }

    @GetMapping("/deleted")
    public @ResponseBody List<Note> getAllDeletedNotes() {
        return noteTakingService.getAllDeletedNotesSortedByUpdatedDate();
    }

    @GetMapping("/undeleted")
    public @ResponseBody List<Note> getAllUndeletedNotes() {
        return noteTakingService.getAllUndeletedNotesSortedByUpdatedDate();
    }

    @GetMapping("/byFolder/{folder}")
    public @ResponseBody List<Note> getAllNotesByFolder(@PathVariable Folder folder) {
        return noteTakingService.getAllNotesByFolder(folder);
    }

    @GetMapping("/search")
    public @ResponseBody List<Note> searchNotesByTitle(@RequestParam String title) {
        return noteTakingService.searchNotesByTitle(title);
    }

}
