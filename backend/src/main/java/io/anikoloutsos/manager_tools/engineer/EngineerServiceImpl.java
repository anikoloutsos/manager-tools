package io.anikoloutsos.manager_tools.engineer;

import io.anikoloutsos.manager_tools.engineer.dto.CreateEngineerRequest;
import io.anikoloutsos.manager_tools.engineer.dto.EngineerResponse;
import io.anikoloutsos.manager_tools.engineer.dto.UpdateEngineerRequest;
import io.anikoloutsos.manager_tools.exception.EngineerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EngineerServiceImpl implements EngineerService {

    private final EngineerRepository repository;

    public EngineerServiceImpl(EngineerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EngineerResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EngineerResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EngineerNotFoundException(id));
    }

    @Override
    public EngineerResponse create(CreateEngineerRequest request) {
        Engineer engineer = new Engineer(
                request.name(), request.level(), request.squad(),
                request.hireDate(), request.employmentType(),
                request.hourlyRate(), request.daysAtOffice()
        );
        return toResponse(repository.save(engineer));
    }

    @Override
    public void update(UUID id, UpdateEngineerRequest request) {
        Engineer engineer = repository.findById(id)
                .orElseThrow(() -> new EngineerNotFoundException(id));
        engineer.setName(request.name());
        engineer.setLevel(request.level());
        engineer.setSquad(request.squad());
        engineer.setHireDate(request.hireDate());
        engineer.setEmploymentType(request.employmentType());
        engineer.setHourlyRate(request.hourlyRate());
        engineer.setDaysAtOffice(request.daysAtOffice());
        repository.save(engineer);
    }

    @Override
    public void delete(UUID id) {
        Engineer engineer = repository.findById(id)
                .orElseThrow(() -> new EngineerNotFoundException(id));
        repository.delete(engineer);
    }

    private EngineerResponse toResponse(Engineer e) {
        return new EngineerResponse(
                e.getId(), e.getName(), e.getLevel(), e.getSquad(),
                e.getHireDate(), e.getEmploymentType(),
                e.getHourlyRate(), e.getDaysAtOffice()
        );
    }
}
