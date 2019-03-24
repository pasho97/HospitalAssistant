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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirebaseNotificationSender {
  private static final String PATIENT_NEEDS_ASSISTANCE_PRIORITY_TEMPLATE = "Priority: %s! Patient needs assistance";
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
    LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    if (account.getInterval().stream().anyMatch(intervalDto -> intervalDto.getStart()
        .isBefore(now) && intervalDto.getEnd().isAfter(now))) {
      MulticastMessage message = MulticastMessage.builder()
          .setNotification(new Notification(String.format(PATIENT_NEEDS_ASSISTANCE_PRIORITY_TEMPLATE,
                                                          intentDto.getPriority()), intentDto.getMessage()))
          .addAllTokens(Collections.singletonList(account.getFirebaseData().getToken()))
          .build();
      BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
      log.info(" {} messages were sent successfully to user {}", response.getSuccessCount(), account.getName());
    }
  }

}
