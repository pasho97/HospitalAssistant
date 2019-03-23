package com.hospital.assistant.api;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.hospital.assistant.account.repo.AccountRepository;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Intents;
import com.hospital.assistant.service.FirebaseNotificationSender;
import java.io.IOException;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/hospital/assistant")
public class AssistantController {

  @Autowired
  private AccountRepository repo;

  @Autowired private FirebaseNotificationSender firebaseNotificationSender;

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
    new Thread(() ->
                                   Intents.getIntentsMap().forEach((key, intentDto) -> {
                                     if (key.equals(parse.getQueryResult()
                                                        .getIntent()
                                                        .getDisplayName())) { // matched intent
                                       for (Account account : repo.getAccounts()) {
                                         if (account.getRole() == intentDto.getContactRole()) {
                                           // send push notification to all such people
                                           log.info("Sending intent {} to {}",
                                                    intentDto.getMessage(),
                                                    account.getName());
                                           try {
                                             firebaseNotificationSender.sendNotifications(account, intentDto);
                                           } catch (IOException | FirebaseMessagingException e) {
                                             log.error("Failed to send notification to account {}", account.getName());
                                           }
                                           System.out.println(String.format("sent push notification to '%s'",
                                                                            account.getName()));
                                         }
                                       }
                                     }
                                   })
    ).start();

    response.setFulfillmentText("Your request has been acknowledged. A medical staff personnel will be here shortly");
    jsonGenerator.serialize(response);
    jsonGenerator.flush();

    return stringWriter.toString();
  }
}
