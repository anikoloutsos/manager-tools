package io.anikoloutsos.manager_tools.engineer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "engineers")
public class Engineer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineerLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Squad squad;

    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    private BigDecimal hourlyRate;

    private Integer daysAtOffice;

    protected Engineer() {
    }

    public Engineer(String name, EngineerLevel level, Squad squad,
                    LocalDate hireDate, EmploymentType employmentType,
                    BigDecimal hourlyRate, Integer daysAtOffice) {
        this.name = name;
        this.level = level;
        this.squad = squad;
        this.hireDate = hireDate;
        this.employmentType = employmentType;
        this.hourlyRate = hourlyRate;
        this.daysAtOffice = daysAtOffice;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EngineerLevel getLevel() { return level; }
    public void setLevel(EngineerLevel level) { this.level = level; }

    public Squad getSquad() { return squad; }
    public void setSquad(Squad squad) { this.squad = squad; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public Integer getDaysAtOffice() { return daysAtOffice; }
    public void setDaysAtOffice(Integer daysAtOffice) { this.daysAtOffice = daysAtOffice; }
}
