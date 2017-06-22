import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tipmailer {

  public static void main(String[] args) throws IOException {
    MailSender mailSender = new MailSender();
    List<Path> tips = Files.list(Paths.get("tips")).collect(Collectors.toList());
    Files.readAllLines(Paths.get("subscribers.txt"))
      .forEach(subscriber -> send(mailSender, subscriber, getRandomTip(tips)));
  }

  private static void send(MailSender mailSender, String subscriber, String randomTip) {
    mailSender.sendMessage(MailSender.createEmail(subscriber, "Collective Intelligence - Tip of the day", randomTip));
  }

  private static String getRandomTip(List<Path> tips) {
    Path randomTip = tips.get(new Random().nextInt(tips.size()));
    try {
      return Files.readAllLines(randomTip).stream().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
