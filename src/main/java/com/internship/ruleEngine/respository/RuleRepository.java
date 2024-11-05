package com.internship.ruleEngine.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.internship.ruleEngine.model.Rule;

@Repository
public interface RuleRepository extends JpaRepository<Rule,Long> {
	
	
}
