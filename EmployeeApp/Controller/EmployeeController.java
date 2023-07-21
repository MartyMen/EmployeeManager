package com.example.EmployeeApp.Controller;

import com.example.EmployeeApp.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.example.EmployeeApp.Repository.EmployeeRepository;
import com.example.EmployeeApp.Repository.SkillsRepository;
import com.example.EmployeeApp.dbobjects.Employees;
import com.example.EmployeeApp.dbobjects.SkillLevels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository eRepo;
    @Autowired
    private SkillsRepository sRepo;
    @Autowired
    private EmployeeService eService;


    @GetMapping({"/showEmployees", "/list"})
    public ModelAndView showEmployees() {
        ModelAndView mav = new ModelAndView("list-employees");
        List<Employees> list = eRepo.findAll();
        mav.addObject("employees", list);
        return mav;
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(@Valid @ModelAttribute Employees employee, BindingResult result, @RequestParam(required = false) List<String> skillIds) {
        // Handle validation errors
        ModelAndView mav;
        if (result.hasErrors()) {
            // Return back to the form
            mav = new ModelAndView("add-employee-form");
            mav.addObject("employee", employee); // Populate the employee object again
        }
        if (employee.getEmployeeID() == null || employee.getEmployeeID().isEmpty()) {
            employee.setEmployeeID(UUID.randomUUID().toString());
        }
        if ("true".equals(String.valueOf(employee.isActive()))) {
            employee.setActive(true);
        } else if ("false".equals(String.valueOf(employee.isActive()))) {
            employee.setActive(false);
        }
        employee.calculateAndSetAge();
        // Check if skillIds is not null before processing
        if (skillIds != null) {
            Set<SkillLevels> skills = sRepo.findAllById(skillIds).stream().collect(Collectors.toSet());
            employee.setSkillLevels(skills);
        }
        eRepo.save(employee);
        return "redirect:/list";
    }

    @GetMapping("/addEmployee")
    public String showAddEmployeeForm(Model model) {
        Employees employee = new Employees();  // create new Employee object
        model.addAttribute("employee", employee);  // add to the model
        return "add-employee-form";  // return view name
    }

    @GetMapping("/addEmployeeForm")
    public ModelAndView addEmployeeForm() {
        ModelAndView mav = new ModelAndView("add-employee-form");
        Employees newEmployee = new Employees();
        mav.addObject("employee", newEmployee);
        mav.addObject("active", String.valueOf(newEmployee.isActive()));

        // Fetch all skills and add them to the model
        List<SkillLevels> allSkills = sRepo.findAll();
        System.out.println(allSkills);
        mav.addObject("allSkills", allSkills);

        return mav;
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam String employeeID) {
        ModelAndView mav = new ModelAndView("add-employee-form");
        Employees employee = eRepo.findById(employeeID).get();
        mav.addObject("employee", employee);
        mav.addObject("active", String.valueOf(employee.isActive()));

        // Fetch all skills and add them to the model
        List<SkillLevels> allSkills = sRepo.findAll();
        mav.addObject("allSkills", allSkills);

        // Add the employee's skills
        Set<SkillLevels> employeeSkills = employee.getSkillLevels();
        mav.addObject("employeeSkills", employeeSkills);

        return mav;
    }

    @GetMapping("/deleteEmployee")
    public String deleteEmployee(@RequestParam String employeeID) {
        eRepo.deleteById(employeeID);
        return "redirect:/list";
    }

    @GetMapping("/showEmployeesWithSkill")
    public String showEmployeesWithSkill(@RequestParam("skillID") String skillLevelID, Model model) {
        List<Employees> employeesWithSkill = eService.findEmployeesBySkillId(skillLevelID);
        model.addAttribute("employees", employeesWithSkill);
        return "list-employee-with-skill";
    }
}
