package com.example.EmployeeApp.Controller;

import com.example.EmployeeApp.Repository.EmployeeRepository;
import com.example.EmployeeApp.Repository.SkillsRepository;
import com.example.EmployeeApp.dbobjects.Employees;
import com.example.EmployeeApp.dbobjects.SkillLevels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class SkillsController {

    @Autowired
    private SkillsRepository sRepo;

    @GetMapping("/skills")
    public ModelAndView showSkills()
    {
        ModelAndView mav = new ModelAndView("list-skills");
        List<SkillLevels> list = sRepo.findAll();
        System.out.println("Number of skills: " + list.size());  // logging
        mav.addObject("skillLevels",list);
        return mav;
    }

    @PostMapping("/saveSkill")
    public String saveSkills(@ModelAttribute("skillLevels") SkillLevels skill)
    {
        if (skill.getSkillLevelID() == null || skill.getSkillLevelID().isEmpty())
        {
            skill.setSkillLevelID(UUID.randomUUID().toString());
        }
        sRepo.save(skill);  // Save the skill
        return "redirect:/skills";  // Redirect back to /skills
    }

    @GetMapping("/addSkillsForm")
    public ModelAndView addSkillsForm()
    {
        ModelAndView mav = new ModelAndView("add-skill-form");
        SkillLevels skills = new SkillLevels();
        mav.addObject("skillLevels", skills);
        return mav;
    }

    @GetMapping("/showSkillsUpdate")
    public ModelAndView showSkillsUpdateForm(@RequestParam String skillID)
    {
        ModelAndView mav = new ModelAndView("add-skill-form");
        SkillLevels skills = sRepo.findById(skillID).get();
        mav.addObject("skillsLevels", skills);
        return mav;
    }

    @GetMapping("/deleteSkill")
    public String deleteSkill(@RequestParam String skillID)
    {
        sRepo.deleteById(skillID);
        return "redirect:/skills";
    }
}