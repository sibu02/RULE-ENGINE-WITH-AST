package com.internship.ruleEngine.service;

import org.springframework.stereotype.Service;

import com.internship.ruleEngine.model.ASTNode;

@Service
public class RuleModifierService {
	
	public ASTNode modifyOperator(ASTNode node,String oldOperator,String newOperator) {
		if (node == null) return null;

        if ("OPERATOR".equals(node.getType()) && node.getValue().equals(oldOperator)) {
        	node.setValue(newOperator);
        }

        node.setLeft(modifyOperator(node.getLeft(), oldOperator, newOperator));
        node.setRight(modifyOperator(node.getRight(), oldOperator, newOperator));
        return node;
		
	}
	
	public ASTNode modifyOperand(ASTNode node,String oldOperand,String newOperand) {
		if(node == null) {
			return null;
		}
		if("OPERAND".equals(node.getType()) && node.getValue().equals(oldOperand)) {
			node.setValue(newOperand);
			return node;
		}
		node.setLeft(modifyOperand(node.getLeft(),oldOperand,newOperand));
		node.setRight(modifyOperand(node.getRight(),oldOperand,newOperand));
		return node;
		
	}
	
	public ASTNode addSubExpression(ASTNode node,ASTNode newNode,String operator) {
		if (node == null || newNode == null) {
	        return null;
	    }
		ASTNode operatorNode = new ASTNode();
        operatorNode.setType("OPERATOR");
        operatorNode.setValue(operator);
        operatorNode.setLeft(node);
        operatorNode.setRight(newNode); 
        return operatorNode;
	}
	
	public ASTNode removeSubExpession(ASTNode node,String operandToRemove) {
		if(node == null)return null;
		if("OPERAND".equals(node.getType()) && operandToRemove.equals(node.getValue())) {
			return null;
		}
		if("OPERATOR".equals(node.getType())) {
			node.setLeft(removeSubExpession(node.getLeft(),operandToRemove));
			node.setRight(removeSubExpession(node.getRight(),operandToRemove));
			if(node.getLeft() == null && node.getRight() != null) {
				return node.getRight();
			}
			else if(node.getRight() == null && node.getLeft() != null) {
				return node.getLeft();
			}
			
			if(node.getLeft() == null  && node.getRight() == null) {
				return null;
			}
		}
		return node;
	}
	
	
}
