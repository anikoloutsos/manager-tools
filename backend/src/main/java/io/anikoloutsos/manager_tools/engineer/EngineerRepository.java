package io.anikoloutsos.manager_tools.engineer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EngineerRepository extends JpaRepository<Engineer, UUID> {
}
