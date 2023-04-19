# https://docs.camunda.org/rest/camunda-bpm-platform/7.19-SNAPSHOT/#tag/Process-Definition/operation/startProcessInstanceByKey

import pandas as pd
import requests
import itertools
import random
# import pm4py

NUM_OF_INSTANCES = 2
PROCESS_KEY = "Process_Ticket"

BASE_URL = "http://localhost:8080/engine-rest"
DIR_PROCESS_INSTANCES = "/process-instance"
DIR_HISTORY_PROCESS_INSTANCES = "/history/process-instance"
DIR_START_PROCESS = f"/process-definition/key/{PROCESS_KEY}/start"
DIR_TASK = "/task"
DIR_TASK_SUBMIT_FORM = "/submit-form"  # DIR_TASK + "/" + id + "/submit-form"
DIR_HISTORY_ACTIVITY_INSTANCES = "/history/activity-instance?sortBy=endTime&sortOrder=asc"
'''
[
    {
        "id": "UserTask_checkSolution:538ba4e4-dc6d-11ed-91a4-9ac28a18adeb",
        "parentActivityInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "activityId": "UserTask_checkSolution",
        "activityName": "check the solution",
        "activityType": "userTask",
        "processDefinitionKey": "Process_Ticket",
        "processDefinitionId": "Process_Ticket:8:7cb1b963-d1f8-11ed-8048-9ac28a18adeb",
        "processInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "executionId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "taskId": "538ba4e5-dc6d-11ed-91a4-9ac28a18adeb",
        "calledProcessInstanceId": null,
        "calledCaseInstanceId": null,
        "assignee": null,
        "startTime": "2023-04-16T17:42:42.956+0200",
        "endTime": "2023-04-16T17:46:25.336+0200",
        "durationInMillis": 222380,
        "canceled": false,
        "completeScope": false,
        "tenantId": null,
        "removalTime": null,
        "rootProcessInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb"
    },
    ...
]
'''
DIR_HISTORY_DETAIL = "/history/detail"
'''
[
    {
        "type": "variableUpdate",
        "id": "c68060b5-d1f8-11ed-8048-9ac28a18adeb",
        "processDefinitionKey": "Process_Ticket",
        "processDefinitionId": "Process_Ticket:8:7cb1b963-d1f8-11ed-8048-9ac28a18adeb",
        "processInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "activityInstanceId": "UserTask_createTicket:a3e37238-d1f8-11ed-8048-9ac28a18adeb",
        "executionId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "caseDefinitionKey": null,
        "caseDefinitionId": null,
        "caseInstanceId": null,
        "caseExecutionId": null,
        "taskId": null,
        "tenantId": null,
        "userOperationId": "c68060b0-d1f8-11ed-8048-9ac28a18adeb",
        "time": "2023-04-03T10:23:13.075+0200",
        "removalTime": null,
        "rootProcessInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "variableName": "field_ticketReporter",
        "variableInstanceId": "c68060b4-d1f8-11ed-8048-9ac28a18adeb",
        "variableType": "String",
        "value": "Lars",
        "valueInfo": {},
        "initial": false,
        "revision": 0,
        "errorMessage": null
    },
    {
        "type": "formField",
        "id": "c68060b6-d1f8-11ed-8048-9ac28a18adeb",
        "processDefinitionKey": "Process_Ticket",
        "processDefinitionId": "Process_Ticket:8:7cb1b963-d1f8-11ed-8048-9ac28a18adeb",
        "processInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "activityInstanceId": "UserTask_createTicket:a3e37238-d1f8-11ed-8048-9ac28a18adeb",
        "executionId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "caseDefinitionKey": null,
        "caseDefinitionId": null,
        "caseInstanceId": null,
        "caseExecutionId": null,
        "taskId": "a3e37239-d1f8-11ed-8048-9ac28a18adeb",
        "tenantId": null,
        "userOperationId": "c68060b0-d1f8-11ed-8048-9ac28a18adeb",
        "time": "2023-04-03T10:23:13.075+0200",
        "removalTime": null,
        "rootProcessInstanceId": "a3e32416-d1f8-11ed-8048-9ac28a18adeb",
        "fieldId": "field_ticketReporter",
        "fieldValue": "Lars"
    },
    ...
]
'''

FORM_VARIABLES = {
    # "field_ticketID" vom system
    "field_ticketType": ["support", "security"],
    "field_ticketStatus": ["todo", "deploy", "done"],
    "field_ticketReporter": ["hans", "julia", "ahmed", "uliana", "pavel", "dilek"],
    "field_ticketDescription": ["da geht was nicht", "ich hab da ein problem", "HILFEE!"],
    "field_riskRating": [*range(1, 11)]
}


def chunker(seq, size):
    return (seq[pos:pos + size] for pos in range(0, len(seq), size))


# wähle dafür Zufällige Werte für die Forms
def random_form_variables(form_key):
    if form_key == "camunda-forms:app:forms/createTicket-form.form":
        return {
            "variables": {
                "field_ticketType": {
                    "value": random.choice(FORM_VARIABLES["field_ticketType"]),
                    "type": "string"
                },
                "field_ticketReporter": {
                    "value": random.choice(FORM_VARIABLES["field_ticketReporter"]),
                    "type": "string"
                },
                "field_ticketDescription": {
                    "value": random.choice(FORM_VARIABLES["field_ticketDescription"]),
                    "type": "string"
                }
            }
        }
    elif form_key == "camunda-forms:app:forms/rateRisk-form.form":
        return {
            "variables": {
                "field_riskRating": {
                    "value": random.choice(FORM_VARIABLES["field_riskRating"]),
                    "type": "integer"
                }
            }
        }
    elif form_key == "camunda-forms:app:forms/editTicketStatus-form.form":
        return {
            "variables": {
                "field_ticketStatus": {
                    "value": random.choice(FORM_VARIABLES["field_ticketStatus"]),
                    "type": "string"
                }
            }
        }


def run_instances():
    # Loop über Anzahl der gewünschten Instanzenanzahl in 10er-Schritten
    instances_as_list = list(itertools.chain(range(0, NUM_OF_INSTANCES)))
    for group in chunker(instances_as_list, 10):
        # Erzeuge bis zu 10 Instanzen
        for i in group:
            body = {
                "variables": {
                },
                "businessKey": f"instance-{i}"
            }
            res = requests.post(BASE_URL + DIR_START_PROCESS, json=body)
            print(res)
        # Arbeite Tasks in zufälliger Reihenfolge ab
        res = requests.get(BASE_URL + DIR_TASK)
        task_list = res.json()
        while task_list:
            # TODO : was ist mit den timern???
            next_task = random.choice(task_list)
            # wähle dafür Zufällige Werte für die Forms
            print(f"{BASE_URL}{DIR_TASK}/{next_task['id']}{DIR_TASK_SUBMIT_FORM}")
            vars = random_form_variables(next_task['formKey'])
            print(vars)

            res = requests.post(f"{BASE_URL}{DIR_TASK}/{next_task['id']}{DIR_TASK_SUBMIT_FORM}",
                                json=vars)
            print(res, res.text)
            res = requests.get(BASE_URL + DIR_TASK)
            task_list = res.json()


def build_event_log():
    # Alle aktivitäten holen mit DIR_HISTORY_ACTIVITY_INSTANCES und df füllen
    res = requests.get(BASE_URL + DIR_HISTORY_ACTIVITY_INSTANCES)
    print(f"Number of History Activities: {len(res.json())}")
    all_history_activities = res.json()

    # Alle Variablen details holen
    res = requests.get(BASE_URL + DIR_HISTORY_DETAIL)
    print(f"Number of History Details: {len(res.json())}")
    all_history_details = res.json()

    for activity in all_history_activities:
        for detail in all_history_details:
            if detail["activityInstanceId"] == activity["id"]:
                # TODO nicht nur variablenUpdates sondern auch formField berücksichtigen
                if detail["type"] == "variableUpdate":
                    activity["detail_id-" + detail["variableName"]] = detail["variableName"]
                    activity["detail_type-" + detail["variableName"]] = detail["type"]
                    activity["detail_value-" + detail["variableName"]] = detail["value"]
                    activity["detail_valueType-" + detail["variableName"]] = detail["variableType"]

    df = pd.DataFrame.from_records(all_history_activities)
    print(df)
    df.to_csv("pip_test_csv.csv", sep='\t')

    # TODO XES erstellen
    # def convert_cvs_to_xes():
    #     dataframe = pd.read_csv('test.csv', sep=',')
    #
    # dataframe = pm4py.format_dataframe(dataframe, case_id='Id', activity_key='Activity', timestamp_key='Timestamp')
    # pm4py.write_xes(dataframe, 'exported.xes')



def cleanup():
    # get alle instanzen
    all_instances = requests.get(BASE_URL + DIR_PROCESS_INSTANCES)

    # kill all instances
    print("start delete instances")
    for instance in all_instances.json():
        # print(f"deleting instance {instance['id']}")
        res = requests.delete(f"{BASE_URL}{DIR_PROCESS_INSTANCES}/{instance['id']}")
        # print(f"{res}")
    print("end delete instances")

    # reset history
    print("start delete history")
    all_history_instances = requests.get(f"{BASE_URL}{DIR_HISTORY_PROCESS_INSTANCES}")
    for instance in all_history_instances.json():
        # print(f"deleting history of instance {instance['id']}")
        res = requests.delete(f"{BASE_URL}{DIR_HISTORY_PROCESS_INSTANCES}/{instance['id']}")
        # print(f"{res}")
    print("end delete history")


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    run_instances()
    build_event_log()
    cleanup()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
