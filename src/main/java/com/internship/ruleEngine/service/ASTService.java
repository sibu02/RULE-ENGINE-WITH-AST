package com.internship.ruleEngine.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.ruleEngine.model.ASTNode;

@Service
public class ASTService {

    private final ObjectMapper objectMapper;

    public ASTService() {
        this.objectMapper = new ObjectMapper();
    }

    public String serializeAST(ASTNode astNode) throws Exception {
        return objectMapper.writeValueAsString(astNode);
    }

    public ASTNode deserializeAST(String json) throws Exception {
        return objectMapper.readValue(json, ASTNode.class);
    }
    
    public String astNodeToRuleString(ASTNode astNode) {
    	if(astNode == null) {
    		return "";
    	}
    	
    	if("OPERAND".equals(astNode.getType())) {
    		return astNode.getValue();
    	}
    	else if("OPERATOR".equals(astNode.getType())) {
    		String leftString = astNodeToRuleString(astNode.getLeft());
        	String rightString = astNodeToRuleString(astNode.getRight());
        	return "("+leftString+" "+astNode.getValue()+" "+rightString+")";
    	}
    	
    	return "";
    }
    
}
