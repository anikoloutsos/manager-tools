package com.managertools.note;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByEngineerIdOrderByMeetingDateDesc(Long engineerId);

    List<Note> findByEngineerIdOrderByMeetingDateDesc(Long engineerId, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.engineer.id = :engineerId AND LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY n.meetingDate DESC")
    List<Note> searchByContent(@Param("engineerId") Long engineerId, @Param("query") String query);
}
