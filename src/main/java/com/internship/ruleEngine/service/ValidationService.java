package com.internship.ruleEngine.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.internship.ruleEngine.model.ASTNode;

@Service
public class ValidationService {
	
	public boolean validateRule(String rule) {
		
		if(!validateParenthesis(rule)) {
			throw new IllegalArgumentException("Unbalanced parentheses in rule: " + rule);
		}
		if(!validateOperator(rule)) {
			throw new IllegalArgumentException("Invalid operator usage in rule: " + rule);
		}
		return true;
	}

	private boolean validateOperator(String rule) {
		String[] split = rule.split(" ");
		for(int i=0;i<split.length;i++) {
			String curr = split[i];
			if(curr.equals("AND") || curr.equals("OR")) {
				if(i == 0 || i == split.length-1)return false;
				if(split[i-1].equals("(") || split[i+1].equals(")"))return false;
				if(split[i-1].equals("AND") || split[i+1].equals("AND"))return false;
				if(split[i-1].equals("OR") || split[i+1].equals("OR"))return false;
			}
		}
		return true;
	}

	private boolean validateParenthesis(String rule) {
		int count = 0;
		for(char ch : rule.toCharArray()) {
			if(ch == '(')count++;
			else if(ch == ')')count--;
			if(count < 0)return false;
		}
		return count == 0;
	}
	
	public void validateUserData(Map<String,Object> userData,ASTNode astNode) {
		if(astNode == null)return;
		
		if("OPERAND".equals(astNode.getType())) {
			String attribute = astNode.getValue().split(" ")[0];
			if(!userData.containsKey(attribute)) {
				 throw new IllegalArgumentException("Missing required attribute: " + attribute);
			}
		}
		validateUserData(userData,astNode.getLeft());
		validateUserData(userData,astNode.getRight());
	}
}
