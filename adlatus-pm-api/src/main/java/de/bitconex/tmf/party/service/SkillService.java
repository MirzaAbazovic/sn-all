package de.bitconex.tmf.party.service;

import de.bitconex.tmf.party.models.Skill;

import java.util.List;

public interface SkillService {
    Skill createSkill(Skill skill);

    Skill getSkillById(Long id);

    List<Skill> getAllSkills();

    Skill updateSkill(Skill skill);

    void deleteSkill(Skill skill);
}