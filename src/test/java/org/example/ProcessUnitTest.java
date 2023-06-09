package org.example;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessUnitTest {

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSupportTicket_GenerateSolution_done() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
        .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","support",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_checkSolution");
    complete(task(),withVariables("field_ticketStatus","done"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("ServiceTask_generateSolution")
            .hasPassed("UserTask_checkSolution")
            .hasNotPassed("UserTask_rateSecurityRisk")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("UserTask_someoneFixIt")
            .hasNotPassed("UserTask_maryFixIt")
            .hasNotPassed("ServiceTask_deploySolution")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSupportTicket_GenerateSolution_Deploy() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","support",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_checkSolution");
    complete(task(),withVariables("field_ticketStatus","deploy"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("ServiceTask_generateSolution")
            .hasPassed("UserTask_checkSolution")
            .hasPassed("ServiceTask_deploySolution")
            .hasNotPassed("UserTask_rateSecurityRisk")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("UserTask_someoneFixIt")
            .hasNotPassed("UserTask_maryFixIt")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSupportTicket_GenerateSolution_Todo() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","support",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_checkSolution");
    complete(task(),withVariables("field_ticketStatus","todo"));

    assertThat(processInstance).isWaitingAt("UserTask_maryFixIt");
    complete(task(),withVariables("field_ticketStatus","done"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("ServiceTask_generateSolution")
            .hasPassed("UserTask_checkSolution")
            .hasPassed("UserTask_maryFixIt")
            .hasNotPassed("UserTask_rateSecurityRisk")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("UserTask_someoneFixIt")
            .hasNotPassed("ServiceTask_deploySolution")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSecurityTicket_LowRisk_GenerateSolution_Todo() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_rateSecurityRisk");
    complete(task(),withVariables("field_riskRating",1));

    assertThat(processInstance).isWaitingAt("UserTask_checkSolution");
    complete(task(),withVariables("field_ticketStatus","todo"));

    assertThat(processInstance).isWaitingAt("UserTask_maryFixIt");
    complete(task(),withVariables("field_ticketStatus","done"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("UserTask_rateSecurityRisk")
            .hasPassed("ServiceTask_generateSolution")
            .hasPassed("UserTask_checkSolution")
            .hasPassed("UserTask_maryFixIt")
            .hasNotPassed("UserTask_someoneFixIt")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("ServiceTask_deploySolution")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSecurityTicket_HighRisk_SomeoneRates() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_rateSecurityRisk");
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans",
            "field_riskRating",6));

    assertThat(processInstance).isWaitingAt("UserTask_someoneFixIt");
    complete(task(),withVariables("field_ticketStatus","deploy"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("UserTask_rateSecurityRisk")
            .hasPassed("UserTask_someoneFixIt")
            .hasPassed("ServiceTask_deploySolution")
            .hasNotPassed("ServiceTask_generateSolution")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("UserTask_checkSolution")
            .hasNotPassed("UserTask_maryFixIt")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSecurityTicket_HighRisk_MaryFix() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();
    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_rateSecurityRisk");
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans",
            "field_riskRating",10));

    assertThat(processInstance).isWaitingAt("UserTask_someoneFixIt");
    execute(job("Event_TimeToFixIt"));

    assertThat(processInstance).isWaitingAt("UserTask_maryFixIt");
    complete(task(),withVariables("field_ticketStatus","deploy"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("UserTask_rateSecurityRisk")
            .hasPassed("UserTask_someoneFixIt")
            .hasPassed("UserTask_maryFixIt")
            .hasPassed("ServiceTask_deploySolution")
            .hasNotPassed("ServiceTask_generateSolution")
            .hasNotPassed("UserTask_JohnRatesSecurityRisk")
            .hasNotPassed("UserTask_checkSolution")
            .isEnded();
  }

  @Test
  @Deployment(resources = "Ticket.bpmn")
  public void testHappyPathSecurityTicket_HighRisk_JohnRates() {
    // Drive the process by API and assert correct behavior by camunda-bpm-assert

    ProcessInstance processInstance = processEngine().getRuntimeService()
            .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isStarted();

    // Formulareingabe
    complete(task(),withVariables(
            "field_ticketType","security",
            "field_ticketReporter","Hans"));

    assertThat(processInstance).isWaitingAt("UserTask_rateSecurityRisk");
    execute(job("Event_TimeToRate"));

    assertThat(processInstance).isWaitingAt("UserTask_JohnRatesSecurityRisk");
    complete(task("UserTask_JohnRatesSecurityRisk"),withVariables("field_riskRating",6));

    assertThat(processInstance).isWaitingAt("UserTask_someoneFixIt");
    complete(task(),withVariables("field_ticketStatus","done"));

    assertThat(processInstance)
            .hasPassed("UserTask_createTicket")
            .hasPassed("ServiceTask_createTicketID")
            .hasPassed("UserTask_rateSecurityRisk")
            .hasPassed("UserTask_JohnRatesSecurityRisk")
            .hasPassed("UserTask_someoneFixIt")
            .hasNotPassed("UserTask_checkSolution")
            .hasNotPassed("UserTask_maryFixIt")
            .hasNotPassed("ServiceTask_generateSolution")
            .hasNotPassed("ServiceTask_deploySolution")
            .isEnded();
  }

}
