package com.bpm.workflow.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationEvent;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationListener;
import org.camunda.bpm.dmn.engine.delegate.DmnEvaluatedDecisionRule;
import org.camunda.bpm.dmn.engine.delegate.DmnEvaluatedInput;
import org.springframework.stereotype.Component;

import com.bpm.workflow.dto.DecisionTableEvaluation;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component("decisionTablePostEvaluationListener")
public class DecisionTablePostEvaluationListener implements DmnDecisionTableEvaluationListener {

  @Override
  public void notify(DmnDecisionTableEvaluationEvent decisionTableEvaluationEvent) {
    /* TODO: Decision table post evaluation listener can be executed based on configuration */
    List<DecisionTableEvaluation> decisionInputKeyValues = new ArrayList<>();
    List<DecisionTableEvaluation> decisionOutputKeyValues = new ArrayList<>();
    Map<String, List<DecisionTableEvaluation>> decisionEvaluationKeyValueMap = new HashMap<>();
    log.info("DecisionLogic:{}", decisionTableEvaluationEvent.getDecision().getDecisionLogic());
    Optional<List<DmnEvaluatedInput>> optionalDmnEvaluatedInput = Optional
        .ofNullable(decisionTableEvaluationEvent.getInputs());
    if (optionalDmnEvaluatedInput.isPresent() && CollectionUtils.isNotEmpty(optionalDmnEvaluatedInput.get())) {
      optionalDmnEvaluatedInput.get()
          .forEach(dmnEvaluatedInput -> decisionInputKeyValues
              .add(new DecisionTableEvaluation(dmnEvaluatedInput.getInputVariable(),
                  dmnEvaluatedInput.getValue().getType().getName(), dmnEvaluatedInput.getValue().getValue())));
      Optional<List<DmnEvaluatedDecisionRule>> optionalDmnEvaluatedDecisionRule = Optional
          .ofNullable(decisionTableEvaluationEvent.getMatchingRules());
      if (optionalDmnEvaluatedDecisionRule.isPresent()
          && CollectionUtils.isNotEmpty(optionalDmnEvaluatedDecisionRule.get())) {
        optionalDmnEvaluatedDecisionRule.get()
            .forEach(dmnEvaluatedDecisionRule -> dmnEvaluatedDecisionRule.getOutputEntries().entrySet()
                .forEach(dmnEvaluatedOutput -> decisionOutputKeyValues.add(new DecisionTableEvaluation(
                    dmnEvaluatedOutput.getKey(), dmnEvaluatedOutput.getValue().getValue().getType().getName(),
                    dmnEvaluatedOutput.getValue().getValue().getValue()))));
      }
    }
    decisionEvaluationKeyValueMap.put("decisioninputs", decisionInputKeyValues);
    decisionEvaluationKeyValueMap.put("decisionoutputs", decisionOutputKeyValues);
    log.info("DecisionEvaluation: {}",
        new Gson().toJson(decisionEvaluationKeyValueMap, new TypeToken<Map<String, List<DecisionTableEvaluation>>>() {
        }.getType()));
  }

}
