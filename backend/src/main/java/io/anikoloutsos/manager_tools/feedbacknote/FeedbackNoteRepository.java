package io.anikoloutsos.manager_tools.feedbacknote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackNoteRepository extends JpaRepository<FeedbackNote, UUID> {

    List<FeedbackNote> findAllByOrderByDateDesc();

    List<FeedbackNote> findByEngineerIdOrderByDateDesc(UUID engineerId);
}
