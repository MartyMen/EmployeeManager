package com.example.EmployeeApp.dbobjects;

import com.example.EmployeeApp.Validation.AgeRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "skillLevels")
public class Employees {
    @Id
    @Column(name = "employeeid", updatable = false, nullable = false, unique = true)
    private String employeeID;

    @Pattern(regexp = "^[a-zA-Z]*$", message = "Names should only contain letters")
    @NotNull
    @Column(name = "firstname")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]*$", message = "Names should only contain letters")
    @NotNull
    @Column(name = "lastname")
    private String lastName;

    @NotNull
    @Email(message = "Email should be valid")
    @Column(name = "email")
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "DOB")
    @NotNull
    @PastOrPresent
    @AgeRange(min = 16, max = 80)
    private LocalDate dob;

    @Column(name = "age")
    private int age;

    private boolean active;

    public boolean hasSkill(String skillId) {
        return this.skillLevels.stream()
                .anyMatch(skill -> skill.getSkillLevelID().equals(skillId));
    }

    @ManyToMany
    @JoinTable(
            name = "employee_skills",
            joinColumns = @JoinColumn(name = "EmployeeID"),
            inverseJoinColumns = @JoinColumn(name = "skill_Level_id"))
    private Set<SkillLevels> skillLevels = new HashSet<>();

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<SkillLevels> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(Set<SkillLevels> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public void calculateAndSetAge() {
        if (this.dob != null) {
            LocalDate dob = this.dob;
            LocalDate currentDate = LocalDate.now();
            this.age = Period.between(dob, currentDate).getYears();
        }
    }
}


