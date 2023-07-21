package com.example.EmployeeApp.Repository;

import com.example.EmployeeApp.dbobjects.SkillLevels;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillsRepository extends JpaRepository<SkillLevels, String>
{
    Optional<SkillLevels> findSkillsBySkillName(String skillName);
}

