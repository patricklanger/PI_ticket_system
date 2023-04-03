package org.example;

import java.util.logging.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import java.util.UUID;

public class CreateTicketID implements JavaDelegate {
    private final static Logger LOGGER = Logger.getLogger("CreateTicketID");

    public void execute(DelegateExecution execution) throws Exception {
        String ticketID = UUID.randomUUID().toString();

        execution.setVariable("field_ticketID", ticketID);

        LOGGER.info("Ticket request by '" + execution.getVariable("field_ticketReporter") +
                " for ticket type: " + execution.getVariable("field_ticketType") +
                "gets the Ticket ID" + ticketID);
    }
}
