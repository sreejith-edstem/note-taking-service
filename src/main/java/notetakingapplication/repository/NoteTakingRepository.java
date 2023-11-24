package notetakingapplication.repository;

import notetakingapplication.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteTakingRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByIsDeletedFalseOrderByUpdatedAtDesc();

    List<Note> findAllByOrderByUpdatedAtDesc();
}
