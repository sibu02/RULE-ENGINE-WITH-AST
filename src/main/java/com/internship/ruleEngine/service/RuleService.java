package com.internship.ruleEngine.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.internship.ruleEngine.model.ASTNode;
import com.internship.ruleEngine.model.Rule;
import com.internship.ruleEngine.respository.RuleRepository;

@Service
public class RuleService {

	private RuleRepository ruleRepository;
	private ASTService astService;
	private RuleEvaluateService ruleEvaluateService;
	private RuleCombineService ruleCombineService;
	private ValidationService validationService;
	private RuleModifierService ruleModifierService;

	public RuleService(RuleRepository ruleRepository, ASTService astService, 
			RuleEvaluateService ruleEvaluateService,RuleCombineService ruleCombineService,
			ValidationService validationService,RuleModifierService ruleModifierService) {
		this.ruleRepository = ruleRepository;
		this.astService = astService;
		this.ruleEvaluateService = ruleEvaluateService;
		this.ruleCombineService = ruleCombineService;
		this.validationService = validationService;
		this.ruleModifierService = ruleModifierService;
	}

	// Rule creation
	@Transactional
	public Rule createRule(String ruleStr) throws Exception {
		if(ruleStr.charAt(0) == '"') {
			ruleStr = ruleStr.substring(1,ruleStr.length()-1);
		}
		validationService.validateRule(ruleStr);
		ASTNode ast = helper(ruleStr.trim());
		Rule rule = new Rule();
		rule.setRuleString(ruleStr);
		rule.setAstJson(astService.serializeAST(ast));
		return ruleRepository.save(rule);
	}

	private ASTNode helper(String rule) {
		rule = rule.trim();
		if (rule.startsWith("(") && rule.endsWith(")")) {
			rule = rule.substring(1, rule.length() - 1).trim();
		}

		int orIdx = findOperator("OR", rule);
		if (orIdx != -1) {
			ASTNode left = helper(rule.substring(0, orIdx).trim());
			ASTNode right = helper(rule.substring(orIdx + 2).trim());
			return new ASTNode("OPERATOR", left, right, "OR");
		}

		int andIdx = findOperator("AND", rule);
		if (andIdx != -1) {
			ASTNode left = helper(rule.substring(0, andIdx).trim());
			ASTNode right = helper(rule.substring(andIdx + 3).trim());
			return new ASTNode("OPERATOR", left, right, "AND");
		}

		return new ASTNode("OPERAND", null, null, rule.trim());
	}

	private int findOperator(String operator, String rule) {
	    int depth = 0;
	    for (int i = 0; i < rule.length() - operator.length() + 1; i++) {
	        if (rule.charAt(i) == '(') {
	            depth++;
	        } else if (rule.charAt(i) == ')') {
	            depth--;
	        } else if (depth == 0 && rule.substring(i, i + operator.length()).equals(operator)) {
	            return i;
	        }
	    }
	    return -1; 
	}

	public ASTNode getASTNodeById(Long id) throws Exception {
		Optional<Rule> opt = ruleRepository.findById(id);
		if (opt.isPresent()) {
			Rule rule = opt.get();
			String astString = rule.getAstJson();
			ASTNode node = astService.deserializeAST(astString);
			String ruleString = astNodeToRuleString(node);
			return node;
		}
		throw new Exception("Rule Not Found!");
	}
	
	public Rule getRuleById(Long id) throws Exception {
		Optional<Rule> opt = ruleRepository.findById(id);
		if (opt.isPresent()) {
			return opt.get();
		}
		throw new Exception("Rule Not Found!");
	}
	
	public List<Rule> getAllRule(){
		return ruleRepository.findAll();
	}

	public boolean evaluateRule(Long id, Map<String, Object> userData) throws Exception {
		ASTNode ast = getASTNodeById(id);
		validationService.validateUserData(userData, ast);
		return ruleEvaluateService.evaluateAst(userData, ast);

	}

	public ASTNode combineRules(List<Long> astIds, String operator) throws Exception {
		List<ASTNode> astNodes = astIds.stream().map(id -> {
			try {
				return getASTNodeById(id);
			} catch (Exception e) {
				throw new RuntimeException("Error fetching rule with ID: " + id, e);
			}
		}).collect(Collectors.toList());
		ASTNode combinedNode =  ruleCombineService.combineRules(astNodes, operator);
		String ruleString = astNodeToRuleString(combinedNode);
		Rule rule = new Rule();
		rule.setRuleString(ruleString);
		rule.setAstJson(astService.serializeAST(combinedNode));
		ruleRepository.save(rule);
		return combinedNode;
	}
	
	@Transactional
	public ASTNode modifyOperator(long ruleId,String oldOperator,String newOperator) throws Exception {
		Rule rule = getRuleById(ruleId);
		ASTNode astNode = astService.deserializeAST(rule.getAstJson());
		ASTNode newNode = ruleModifierService.modifyOperator(astNode, oldOperator, newOperator);
		rule.setAstJson(astService.serializeAST(newNode));
		rule.setRuleString(astNodeToRuleString(newNode));
		ruleRepository.save(rule);
		return newNode;
	}

	@Transactional
	public ASTNode modifyOperand(Long ruleId, String oldOperand, String newOperand) throws Exception {
		Rule rule = getRuleById(ruleId);
		ASTNode astNode = astService.deserializeAST(rule.getAstJson());
		ASTNode newNode = ruleModifierService.modifyOperand(astNode, oldOperand, newOperand);
		rule.setAstJson(astService.serializeAST(newNode));
		rule.setRuleString(astNodeToRuleString(newNode));
		ruleRepository.save(rule);
		return newNode;
	}

	@Transactional
	public ASTNode addSubExpression(Long ruleId, String newCondition, String operator) throws Exception {
		Rule rule = getRuleById(ruleId);
		ASTNode astNode = astService.deserializeAST(rule.getAstJson());
		ASTNode newNode = new ASTNode();
		newNode.setType("OPERAND");
		newNode.setValue(newCondition);
		ASTNode updatedNode = ruleModifierService.addSubExpression(astNode, newNode, operator);
		rule.setRuleString(astNodeToRuleString(updatedNode));
		rule.setAstJson(astService.serializeAST(updatedNode));
		ruleRepository.save(rule);
		return updatedNode;
	}

	@Transactional
	public ASTNode removeSubExpression(Long ruleId, String condition) throws Exception {
		Rule rule = getRuleById(ruleId);
		ASTNode astNode = astService.deserializeAST(rule.getAstJson());
		ASTNode newNode = ruleModifierService.removeSubExpession(astNode, condition);
		rule.setAstJson(astService.serializeAST(newNode));
		rule.setRuleString(astNodeToRuleString(newNode));
		ruleRepository.save(rule);
		return newNode;
	}
	
	public String astNodeToRuleString(ASTNode astNode) {
		return astService.astNodeToRuleString(astNode);
	}

	public String deleteRule(Long ruleId) throws Exception {
		Rule rule = getRuleById(ruleId);
		ruleRepository.delete(rule);
		return "Deleted Rule";
	}
}
