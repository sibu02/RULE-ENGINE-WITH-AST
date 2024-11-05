package com.internship.ruleEngine.model;

import lombok.Data;

@Data
public class ASTNode {

	private String type;
	private String value;
	private ASTNode left;
	private ASTNode right;
	
	public ASTNode() {
    }
	
	public ASTNode(String type,ASTNode left,ASTNode right,String value) {
		this.type = type;
		this.value = value;
		this.left = left;
		this.right = right;
	}
}
