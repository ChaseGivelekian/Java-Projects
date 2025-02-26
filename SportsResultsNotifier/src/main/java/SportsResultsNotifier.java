import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SportsResultsNotifier {
    private static final String username = "givelekianc@gmail.com";

    public static void main(String[] args) {
        String url = "https://www.basketball-reference.com/boxscores/";
        Document doc = getWebsiteResponse(url);
        List<String> gameResults = getGameResults(doc);
        sendEmail(gameResults);
    }

    private static Document getWebsiteResponse(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getGameResults(Document doc) {
        var gameSummaries = doc.select("div.game_summaries");

        List<String> results = new ArrayList<>();

        for (var gameSummary : gameSummaries.select("div.game_summary")) {
            String winner = gameSummary.getElementsByClass("winner").select("td a").getFirst().text();
            String winnerScore = gameSummary.getElementsByClass("winner").select("td").get(1).text();
            String loser = gameSummary.getElementsByClass("loser").select("td a").getFirst().text();
            String loserScore = gameSummary.getElementsByClass("loser").select("td").get(1).text();
            String formattedResult = "In the game between %s and %s, %s won with a score of %s to %s's %s.".formatted(winner, loser, winner, winnerScore, loser, loserScore);
            results.add(formattedResult);
        }
        return results;
    }

    private static void sendEmail(List<String> gameResults) {
        Session session = getSession();

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(username));
            message.setSubject("Today's NBA Results");
            message.setText("Here are the results of last night's games:\n\n" + String.join("\n\n", gameResults));

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Session getSession() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, System.getenv("GMAIL_PASSWORD"));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
