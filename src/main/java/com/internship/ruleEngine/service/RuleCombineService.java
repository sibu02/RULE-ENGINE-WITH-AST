package com.internship.ruleEngine.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.internship.ruleEngine.model.ASTNode;

@Service
public class RuleCombineService {
	
	public ASTNode combineRules(List<ASTNode> astNodes,String operator) {
		ASTNode combinedNode = astNodes.get(0);
		
		for(int i=1;i<astNodes.size();i++) {
			ASTNode newNode = new ASTNode();
			newNode.setType("OPERATOR");
			newNode.setLeft(combinedNode);
			newNode.setRight(astNodes.get(i));
			newNode.setValue(operator);
			combinedNode = newNode;
		}
		return combinedNode;
	}
}
