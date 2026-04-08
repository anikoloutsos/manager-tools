package io.anikoloutsos.manager_tools.engineer;

import io.anikoloutsos.manager_tools.engineer.dto.CreateEngineerRequest;
import io.anikoloutsos.manager_tools.engineer.dto.EngineerResponse;
import io.anikoloutsos.manager_tools.engineer.dto.UpdateEngineerRequest;

import java.util.List;
import java.util.UUID;

public interface EngineerService {

    List<EngineerResponse> findAll();

    EngineerResponse findById(UUID id);

    EngineerResponse create(CreateEngineerRequest request);

    void update(UUID id, UpdateEngineerRequest request);

    void delete(UUID id);
}
