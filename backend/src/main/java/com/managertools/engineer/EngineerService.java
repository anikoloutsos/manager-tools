package com.managertools.engineer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EngineerService {

    private final EngineerRepository repository;

    public EngineerService(EngineerRepository repository) {
        this.repository = repository;
    }

    public List<Engineer> findAll() {
        return repository.findAll();
    }

    public Engineer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Engineer not found: " + id));
    }

    @Transactional
    public Engineer create(EngineerRequest request) {
        Engineer engineer = new Engineer(request.name(), request.role(), request.joinedAt());
        return repository.save(engineer);
    }

    @Transactional
    public Engineer update(Long id, EngineerRequest request) {
        Engineer engineer = findById(id);
        engineer.setName(request.name());
        engineer.setRole(request.role());
        engineer.setJoinedAt(request.joinedAt());
        return repository.save(engineer);
    }

    @Transactional
    public void delete(Long id) {
        Engineer engineer = findById(id);
        repository.delete(engineer);
    }
}
