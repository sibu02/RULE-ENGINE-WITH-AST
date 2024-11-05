package com.internship.ruleEngine.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.internship.ruleEngine.model.ASTNode;

@Service
public class RuleEvaluateService {

	public boolean evaluateAst(Map<String, Object> userData, ASTNode node) {

		if (node == null)
			return false;

		if (node.getType().equals("OPERATOR")) {
			boolean leftEval = evaluateAst(userData, node.getLeft());
			boolean rightEval = evaluateAst(userData, node.getRight());

			if (node.getValue().equals("AND")) {
				return leftEval && rightEval;
			} else if (node.getValue().equals("OR")) {
				return leftEval || rightEval;
			}
		}

		if (node.getType().equals("OPERAND")) {
			return evaluateCondition(userData, node.getValue());
		}

		return false;

	}

	private boolean evaluateCondition(Map<String, Object> userData, String condition) {
		String conditions[] = condition.split(" ");
		if (conditions.length < 3)
			return false;

		String attribute = conditions[0];
		String operator = conditions[1];
		String value = conditions[2];

		Object userVal = userData.get(attribute);
		if (userVal instanceof Integer) {
			return evaluateIntCondition((Integer) userVal, operator, Integer.valueOf(value));
		} else if (userVal instanceof String) {
			return evaluateStringCondition((String) userVal, operator, value.replace("'", ""));
		}
		return false;
	}

	private boolean evaluateStringCondition(String userValue, String operator, String value) {
		switch (operator) {
		case "=":
			return userValue.equals(value);
		case "!=":
			return !userValue.equals(value);
		default:
			return false;
		}
	}

	private boolean evaluateIntCondition(Integer userValue, String operator, Integer value) {
		switch (operator) {
		case ">":
			return userValue > value;
		case "<":
			return userValue < value;
		case ">=":
			return userValue >= value;
		case "<=":
			return userValue <= value;
		case "=":
			return userValue == value;
		case "!=":
			return userValue != value;
		default:
			return false;
		}
	}

}
