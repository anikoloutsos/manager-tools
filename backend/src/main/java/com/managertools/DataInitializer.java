package com.managertools;

import com.managertools.engineer.Engineer;
import com.managertools.engineer.EngineerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final EngineerRepository engineerRepository;

    public DataInitializer(EngineerRepository engineerRepository) {
        this.engineerRepository = engineerRepository;
    }

    @Override
    public void run(String... args) {
        if (engineerRepository.count() > 0) {
            return;
        }

        List<Engineer> engineers = List.of(
                new Engineer("Alice Martin", "Senior Engineer", LocalDate.of(2020, 3, 1)),
                new Engineer("Bob Chen", "Engineer", LocalDate.of(2021, 6, 15)),
                new Engineer("Clara Diaz", "Senior Engineer", LocalDate.of(2019, 11, 1)),
                new Engineer("David Kim", "Lead Engineer", LocalDate.of(2018, 4, 20)),
                new Engineer("Eva Rossi", "Engineer", LocalDate.of(2022, 1, 10)),
                new Engineer("Frank Müller", "Senior Engineer", LocalDate.of(2020, 9, 5)),
                new Engineer("Grace Okonkwo", "Engineer", LocalDate.of(2023, 2, 28)),
                new Engineer("Hugo Silva", "Senior Engineer", LocalDate.of(2021, 3, 12)),
                new Engineer("Iris Nakamura", "Engineer", LocalDate.of(2022, 7, 18)),
                new Engineer("James O'Brien", "Senior Engineer", LocalDate.of(2019, 8, 3)),
                new Engineer("Katia Petrova", "Engineer", LocalDate.of(2023, 5, 22)),
                new Engineer("Luca Ferrari", "Lead Engineer", LocalDate.of(2017, 10, 14))
        );

        engineerRepository.saveAll(engineers);
    }
}
