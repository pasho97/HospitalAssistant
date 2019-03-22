package com.hospital.assistant;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@CrossOrigin
@SpringBootApplication
@Controller(value = "/hospital/assistant")
public class AssistantController {
  public static void main(String[] args) {
    SpringApplication.run(AssistantController.class);
  }


  @RequestMapping(value = "/test",method = RequestMethod.POST,produces = MediaType.TEXT_HTML_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
  public String test(@RequestBody GoogleCloudDialogflowV2WebhookRequest googleCloudDialogflowV2WebhookRequest){
    return googleCloudDialogflowV2WebhookRequest.getQueryResult().getIntent().getDisplayName();
  }
}
