package com.internship.ruleEngine.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internship.ruleEngine.model.ASTNode;
import com.internship.ruleEngine.model.Rule;
import com.internship.ruleEngine.request.CombineRequest;
import com.internship.ruleEngine.service.RuleService;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
	
	private RuleService ruleService;
	public RuleController(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	
	@GetMapping
	public ResponseEntity<List<Rule>> getRule() throws Exception{
		List<Rule> rules = ruleService.getAllRule();
		return new ResponseEntity<>(rules,HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Rule> createRule(@RequestBody String rule) throws Exception{
		Rule res = ruleService.createRule(rule);
		return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ASTNode> getRule(@PathVariable("id")Long id) throws Exception{
		ASTNode node = ruleService.getASTNodeById(id);
		return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/{id}/evaluate")
    public boolean evaluateRule(@PathVariable Long id, @RequestBody Map<String, Object> userData) throws Exception {
        // Evaluate the rule against the user's data
        return ruleService.evaluateRule(id, userData);
    }
	
	@PostMapping("/combine")
    public ResponseEntity<ASTNode> combineRule(@RequestBody CombineRequest request) throws Exception {
        ASTNode node =  ruleService.combineRules(request.getIds(), request.getOperator());
        return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
    }
	
	@PutMapping("/modifyOperator")
    public ResponseEntity<ASTNode> modifyOperator(
        @RequestParam("ruleId") Long ruleId,
        @RequestParam("oldOperator") String oldOperator,
        @RequestParam("newOperator") String newOperator) throws Exception {
        ASTNode node = ruleService.modifyOperator(ruleId, oldOperator, newOperator);
        
        if (node != null) {
            return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
        } else {
        	return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }
	
	@PutMapping("/modifyOperand")
    public ResponseEntity<ASTNode> modifyOperand(
        @RequestParam("ruleId") Long ruleId,
        @RequestParam("oldOperand") String oldOperand,
        @RequestParam("newOperand") String newOperand) throws Exception {
        
        // Modify the operand
		System.out.println(oldOperand+" "+newOperand);
		ASTNode node = ruleService.modifyOperand(ruleId, oldOperand, newOperand);
        
		 if (node != null) {
	            return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
	        } else {
	        	return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
	        }
    }
	
	@PutMapping("/addSubExpression")
    public ResponseEntity<ASTNode> addSubExpression(
        @RequestParam("ruleId") Long ruleId,
        @RequestParam("newCondition") String newCondition,
        @RequestParam("operator") String operator) throws Exception {
        
		ASTNode node = ruleService.addSubExpression(ruleId, newCondition, operator);
        
        if (node != null) {
            return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
        } else {
        	return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }
	
	@DeleteMapping("/removeSubExpression")
	public ResponseEntity<ASTNode> removeSubExpression(
			@RequestParam("ruleId")Long ruleId,
			@RequestParam("condition")String condition
			) throws Exception{
		ASTNode node = ruleService.removeSubExpression(ruleId, condition);
		if (node != null) {
            return new ResponseEntity<>(node,HttpStatus.ACCEPTED);
        } else {
        	return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
	}
	
	@DeleteMapping("/delrule/{id}")
	public ResponseEntity<String> deleteRule(@PathVariable Long id) throws Exception{
		String res = ruleService.deleteRule(id);
		return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
	}
}
