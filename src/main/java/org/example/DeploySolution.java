package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.UUID;
import java.util.logging.Logger;

public class DeploySolution implements JavaDelegate {
    private final static Logger LOGGER = Logger.getLogger("CreateTicketID");

    public void execute(DelegateExecution execution) throws Exception {

        execution.setVariable("field_ticketStatus", "done");

        LOGGER.info("Ticket " + execution.getVariable("field_ticketID") + " is done.");
    }
}
