package com.hospital.assistant.api;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Intents;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "/hospital/assistant")
public class AssistantController {

    @Autowired
    private AccountRepository repo;

    private static final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

  @PostMapping(path = "/test",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String webhook(@RequestBody String request) throws IOException {

        GoogleCloudDialogflowV2WebhookRequest parse = jacksonFactory.createJsonParser(request).parse(
                GoogleCloudDialogflowV2WebhookRequest.class);
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jacksonFactory.createJsonGenerator(stringWriter);
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        CompletableFuture.runAsync(() ->
                                       Intents.getIntentsMap().forEach((key, intentDto) -> {
                if (key.equals(parse.getQueryResult().getIntent().getDisplayName())) { // matched intent
                    for (Account account : repo.getAccounts()) {
                      if (account.getRole() == intentDto.getContactRole()) {
                            // send push notification to all such people
                            System.out.println(String.format("sent push notification to '%s'", account.getName()));
                        }
                    }
                }
            })
        );

        response.setFulfillmentText("Your request has been acknowledged. A medical staff personnel will be here shortly");
        jsonGenerator.serialize(response);
        jsonGenerator.flush();

        return stringWriter.toString();
    }
}
