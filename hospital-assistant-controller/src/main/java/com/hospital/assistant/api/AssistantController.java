package com.hospital.assistant.api;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;

import java.io.IOException;
import java.io.StringWriter;

import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Intents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController(value = "/hospital/assistant")
public class AssistantController {

    @Autowired
    private AccountRepository repo;

    private static final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String webhook(@RequestBody String request) throws IOException {

        GoogleCloudDialogflowV2WebhookRequest parse = jacksonFactory.createJsonParser(request).parse(
                GoogleCloudDialogflowV2WebhookRequest.class);
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jacksonFactory.createJsonGenerator(stringWriter);
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        Intents.getIntentsMap().forEach((key, pair) -> {
            if (key.equals(parse.getQueryResult().getIntent().getDisplayName())) { // matched intent
                for (Account account : repo.getAccounts()) {
                    if (account.getRole() == pair.getValue()) {
                        // send push notification to all such people
                    }
                }
            }
        });

        response.setFulfillmentText("Your request has been acknowledged. A medical staff personnel will be here shortly");
        jsonGenerator.serialize(response);
        jsonGenerator.flush();

        return stringWriter.toString();
    }
}
