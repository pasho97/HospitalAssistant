package com.hospital.assistant;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.StringWriter;

@CrossOrigin
@SpringBootApplication
@RestController(value = "/hospital/assistant")
public class AssistantController {

  private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

  public static void main(String[] args) {
    SpringApplication.run(AssistantController.class);
  }


  @RequestMapping(method = RequestMethod.POST)
  public String webhook(@RequestBody String rawRequest) throws IOException {

    GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(rawRequest)
            .parse(GoogleCloudDialogflowV2WebhookRequest.class);

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
