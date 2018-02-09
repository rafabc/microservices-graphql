package com.micro.graphql.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.micro.graphql.Schema;

import graphql.ExecutionInput.Builder;
import graphql.ExecutionResult;
import graphql.GraphQL;

@RestController
public class Controller {


    @Autowired
    Schema schema;

    private GraphQL graphql;

    private static final Logger log = LoggerFactory.getLogger(Controller.class);


    @PostConstruct
    public void init() {
        this.graphql = GraphQL.newGraphQL(schema.getSchema()).build();
    }

    @RequestMapping(value = "/graphql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object executeOperation(@RequestBody Map<String, Object> body) {
        String query = (String) body.get("query");
        
        @SuppressWarnings("unchecked")
		Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        
        Builder builder = new Builder();
        builder.query(query);
        builder.variables(variables);
        
        ExecutionResult executionResult = graphql.execute(builder);
        Map<String, Object> result = new LinkedHashMap<>();
        if (executionResult.getErrors().size() > 0) {
            result.put("errors", executionResult.getErrors());
            log.error("Errors: {}", executionResult.getErrors());
        }
        result.put("data", executionResult.getData());
        return result;
    }

}
