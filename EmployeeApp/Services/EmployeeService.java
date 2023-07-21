package com.example.EmployeeApp.Services;

import com.example.EmployeeApp.Repository.EmployeeRepository;
import com.example.EmployeeApp.dbobjects.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository eRepo;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.eRepo = employeeRepository;
    }

    public List<Employees> findEmployeesBySkillId(String skillLevelID) {
        return eRepo.findAll().stream()
                .filter(employee -> employee.hasSkill(skillLevelID))
                .collect(Collectors.toList());
    }


}
