import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tipmailer {

  private static final Random RANDOM = new Random();
  private static final String[] FEEDBACK_MESSAGES = {
    "It would be great to get feedback about yesterday:",
    "What do you think about yesterday's tip?",
  };
  private static final String[] CONTRIBUTION_MESSAGES = {
    "What do you want to suggest to your collegues?",
    "Contribute!",
    "Why not suggesting something new?",
    "Make these suggestions better!",
  };

  public static void main(String[] args) throws IOException {
    MailSender mailSender = new MailSender();
    List<Path> tips = Files.list(Paths.get("tips")).collect(Collectors.toList());
    Files.readAllLines(Paths.get("subscribers.txt"))
      .forEach(subscriber -> send(mailSender, subscriber, getRandomTip(tips)));
  }

  private static void send(MailSender mailSender, String subscriber, String randomTip) {
    System.out.println("sending to " + subscriber + ": " + randomTip);
    mailSender.sendMessage(MailSender.createEmail(subscriber, "Collective Intelligence - Tip of the day", mailBody(randomTip)));
  }

  private static String mailBody(String randomTip) {
    String fm = FEEDBACK_MESSAGES[RANDOM.nextInt(FEEDBACK_MESSAGES.length)] + " https://goo.gl/forms/z4VpbWEwJW3POOUN2";
    String contributionMessage = CONTRIBUTION_MESSAGES[RANDOM.nextInt(CONTRIBUTION_MESSAGES.length)] + " https://docs.google.com/a/sonarsource.com/document/d/19twzgwwu9YXmosBvtR-SujLNirneR4DOHtsqbgJKk7A/edit?usp=sharing";
    return randomTip + "\n\n" + fm + "\n" + contributionMessage;
  }

  private static String getRandomTip(List<Path> tips) {
    Path randomTip = tips.get(RANDOM.nextInt(tips.size()));
    try {
      return Files.readAllLines(randomTip).stream().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
