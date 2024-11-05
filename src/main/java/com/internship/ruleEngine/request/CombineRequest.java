package com.internship.ruleEngine.request;

import java.util.List;

import lombok.Data;

@Data
public class CombineRequest {
	
	private List<Long> ids;
	private String operator;
}
