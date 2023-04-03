package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.UUID;
import java.util.logging.Logger;

public class GenerateSolution implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger("LOAN-REQUESTS");

    public void execute(DelegateExecution execution) throws Exception {

        String solutionDescription = "ChatGPT generated solution. Accept my solution, take a break and have a Kitkat.";

        execution.setVariable("field_solutionDescription", solutionDescription);

        LOGGER.info("A solution for ticket id " + execution.getVariable("field_ticketID") +
                " was generated. The Solution is: " + solutionDescription);
    }


}
