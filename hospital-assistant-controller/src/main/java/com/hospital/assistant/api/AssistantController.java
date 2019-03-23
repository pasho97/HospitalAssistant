package com.hospital.assistant.api;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import java.io.IOException;
import java.io.StringWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController(value = "/hospital/assistant")
public class AssistantController {

  private static final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

  @RequestMapping(
      method = RequestMethod.POST, name = "/test",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String webhook(@RequestBody String request) throws IOException {

    GoogleCloudDialogflowV2WebhookRequest parse = jacksonFactory.createJsonParser(request).parse(
        GoogleCloudDialogflowV2WebhookRequest.class);
    StringWriter stringWriter = new StringWriter();
    JsonGenerator jsonGenerator = jacksonFactory.createJsonGenerator(stringWriter);
    GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

    response.setFulfillmentText("But first Rado has to show me the architecture, " +
                                    "Pavka one share the project in github and Pavka two answer in slack");
    jsonGenerator.serialize(response);
    jsonGenerator.flush();

    return stringWriter.toString();
  }
}
