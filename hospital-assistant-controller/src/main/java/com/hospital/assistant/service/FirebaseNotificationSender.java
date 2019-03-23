package com.hospital.assistant.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.IntentDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirebaseNotificationSender {
  @Value("${firebase.api.databaseUrl}") private String databaseUrl;
  @Value("${firebase.api.keyFile}") private String credentialsFile;
  public static final String TITLE = "title";
  public static final String BODY = "body";
  public static final String NOTIFICATION = "notification";
  public static final String TO = "to";

  @Autowired
  public FirebaseNotificationSender() throws IOException {
    FileInputStream serviceAccount =
        new FileInputStream(
            "/Users/ppanayotov/Desktop/hospital-assistant/hospital-assistant-controller/src/main/resources/fcmtestapp-bbc49-firebase-adminsdk-pg9xa-53441cbc46.json");

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://fcmtestapp-bbc49.firebaseio.com")
        .build();

    FirebaseApp.initializeApp(options);
  }

  public void sendNotifications(Account account, IntentDto intentDto) throws IOException, FirebaseMessagingException {
    if (account.getFirebaseData() == null) {
      log.debug("Cannot send notification to user account {} of role {}. Cause: No firebase token",
                account.getName(),
                account.getRole());
      return;
    }

    MulticastMessage message = MulticastMessage.builder()
        .setNotification(new Notification(intentDto.getMessage(), intentDto.getMessage()))
        .addAllTokens(Collections.singletonList(account.getFirebaseData().getToken()))
        .build();
    BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
    System.out.println(response.getSuccessCount() + " messages were sent successfully");
  }

}
