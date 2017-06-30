import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class Tipmailer {

  private static final Random RANDOM = new Random();
  private static final String[] FEEDBACK_MESSAGES = {
    "It would be great to get feedback about yesterday:",
    "What do you think about yesterday's tip?",
    "Leave your feedback:",
    "Tell your colleagues what you think about yesterday's tip:",
    "Vote!",
  };
  private static final String[] CONTRIBUTION_MESSAGES = {
    "What do you want to suggest to your collegues?",
    "Contribute!",
    "Why not suggesting something new?",
    "Make these suggestions better!",
    "Receiving same tips again and again? Contribute!",
    "What are YOUR suggestions for tips?",
  };

  public static void main(String[] args) throws Exception {
    MailSender mailSender = new MailSender();
    Map<String, String> tips = WikiReader.getTips();
    Files.readAllLines(Paths.get("subscribers.txt"))
      .forEach(subscriber -> send(mailSender, subscriber, getRandomTip(tips)));
  }

  private static void send(MailSender mailSender, String subscriber, String randomTip) {
    System.out.println("sending to " + subscriber + ": " + randomTip);
    mailSender.sendMessage(MailSender.createEmail(subscriber, "Collective Intelligence - Tip of the day", mailBody(randomTip)));
  }

  private static String mailBody(String randomTip) {
    String feedbackMessage = FEEDBACK_MESSAGES[RANDOM.nextInt(FEEDBACK_MESSAGES.length)] + " https://goo.gl/forms/z4VpbWEwJW3POOUN2";
    String contributionMessage = CONTRIBUTION_MESSAGES[RANDOM.nextInt(CONTRIBUTION_MESSAGES.length)] + " https://github.com/bartfastiel/tipmailer/wiki";
    String unsubscribeMessage = "To unsubscribe, answer to this mail.";
    return randomTip + "\n\n" + feedbackMessage + "\n" + contributionMessage + "\n" + unsubscribeMessage;
  }

  private static String getRandomTip(Map<String, String> tips) {
    Map.Entry[] entries = tips.entrySet().toArray(new Map.Entry[0]);
    return entries[RANDOM.nextInt(entries.length)].getValue().toString();
  }
}
