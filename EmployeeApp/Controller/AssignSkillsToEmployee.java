package com.example.EmployeeApp.Controller;

import com.example.EmployeeApp.Repository.EmployeeRepository;
import com.example.EmployeeApp.Repository.SkillsRepository;
import com.example.EmployeeApp.dbobjects.Employees;
import com.example.EmployeeApp.dbobjects.SkillLevels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AssignSkillsToEmployee {

    private final EmployeeRepository eRepo;
    private final SkillsRepository sRepo;

    @Autowired
    public AssignSkillsToEmployee(EmployeeRepository employeeRepository, SkillsRepository skillsRepository) {
        this.eRepo = employeeRepository;
        this.sRepo = skillsRepository;
    }

    @PostMapping("/employees/{employeeId}/skills")
    public String assignSkillsToEmployee(@PathVariable String employeeId, @RequestBody List<String> skillIds) {
        Employees employee = eRepo.findById(employeeId).orElseThrow(); // handle not foundire
        Set<SkillLevels> skills = sRepo.findAllById(skillIds).stream().collect(Collectors.toSet());
        employee.getSkillLevels().addAll(skills);
        eRepo.save(employee);
        return "Skills assigned to the employee successfully.";
    }
}