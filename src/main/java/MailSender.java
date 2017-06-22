import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender {
  private static final String APPLICATION_NAME = "Tipmailer";
  private static final java.io.File DATA_STORE_DIR = new java.io.File(".credentials");
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);

  private final Gmail service;

  public static Credential authorize(FileDataStoreFactory DATA_STORE_FACTORY, HttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
    InputStream in = MailSender.class.getResourceAsStream("/client_secret.json");
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    GoogleAuthorizationCodeFlow flow =
      new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .setAccessType("offline")
        .build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static Gmail getGmailService() throws IOException, GeneralSecurityException {
    FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

    Credential credential = authorize(dataStoreFactory, httpTransport);
    return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
      .setApplicationName(APPLICATION_NAME)
      .build();
  }

  public MailSender() {
    try {
      service = getGmailService();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException, MessagingException, GeneralSecurityException {
    new MailSender().sendMessage(createEmail("bartfastiel@gmail.com", "Test", "Bla"));
  }

  public static MimeMessage createEmail(String to, String subject, String bodyText) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session);

    try {
      email.setFrom(new InternetAddress("bartfastiel@gmail.com"));
      email.addRecipient(javax.mail.Message.RecipientType.TO,
        new InternetAddress(to));
      email.setSubject(subject);
      email.setText(bodyText);
      return email;
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendMessage(MimeMessage email) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      email.writeTo(baos);
      String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
      Message message = new Message();
      message.setRaw(encodedEmail);
      service.users().messages().send("me", message).execute();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
