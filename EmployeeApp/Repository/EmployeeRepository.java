package com.example.EmployeeApp.Repository;

import com.example.EmployeeApp.dbobjects.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.events.Event;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, String>
{
    @Query("select e from Employees e join e.skillLevels s where s.skillLevelID = :skillId")
    List<Employees> findEmployeesBySkillId(@Param("skillId") String skillId);

    List<Employees> findBySkillLevelsSkillLevelID(String skillLevelID);
}


