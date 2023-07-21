package com.example.EmployeeApp.dbobjects;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skilllevels")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "employees")
@EqualsAndHashCode(of = {"skill_Level_id", "skill_Name", "skill_description"})
public class SkillLevels {
    @Id
    @Column(name = "skill_level_id", updatable = false, nullable = false, unique = true)
    private String skillLevelID;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "skill_description")
    private String skillDescription;

    @ManyToMany(mappedBy = "skillLevels")
    private Set<Employees> employees = new HashSet<>();

    public Set<Employees> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employees> employees) {
        this.employees = employees;
    }

    public String getSkillLevelID() {
        return skillLevelID;
    }

    public void setSkillLevelID(String skillLevelID) {
        this.skillLevelID = skillLevelID;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillDescription() {
        return skillDescription;
    }

    public void setSkillDescription(String skillDescription) {
        this.skillDescription = skillDescription;
    }
}