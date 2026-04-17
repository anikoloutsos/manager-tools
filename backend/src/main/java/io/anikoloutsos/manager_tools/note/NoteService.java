package io.anikoloutsos.manager_tools.note;

import io.anikoloutsos.manager_tools.note.dto.CreateNoteRequest;
import io.anikoloutsos.manager_tools.note.dto.NoteResponse;
import io.anikoloutsos.manager_tools.note.dto.UpdateNoteRequest;

import java.util.List;
import java.util.UUID;

public interface NoteService {

    List<NoteResponse> findAllByEngineerId(UUID engineerId);

    NoteResponse findById(UUID engineerId, UUID id);

    NoteResponse create(UUID engineerId, CreateNoteRequest request);

    void update(UUID engineerId, UUID id, UpdateNoteRequest request);

    void delete(UUID engineerId, UUID id);
}
