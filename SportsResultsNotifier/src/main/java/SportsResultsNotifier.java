import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SportsResultsNotifier {
    public static void main(String[] args) {
        // Website URL
        String url = "https://www.basketball-reference.com/boxscores/";
        Document doc = getWebsiteResponse(url);
        List<String> gameResults = getGameResults(doc);
        System.out.println(gameResults);
//        sendEmail(gameResults);
    }

    private static Document getWebsiteResponse(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getGameResults(Document doc) {
        var gameSummaries = doc.getAllElements().stream().filter(
                element -> element.tagName().equals("div") && element.className().equals("game_summaries")
        ).toList();

        List<String> results = new ArrayList<>();

        for (Element gameSummary : gameSummaries) {
            String winner = gameSummary.getElementsByClass("winner").select("td a").text();
            String winnerScore = gameSummary.getElementsByClass("winner").select("td").get(1).text();
            String loser = gameSummary.getElementsByClass("loser").select("td a").text();
            String loserScore = gameSummary.getElementsByClass("loser").select("td").get(1).text();
            String formattedResult = "In the game between %s and %s, %s won with a score of %s to %s's %s".formatted(winner, loser, winner, winnerScore, loser, loserScore);
            results.add(formattedResult);
        }
        return results;
    }

    private static void sendEmail(List<String> gameResults) {
        // Implement email sending logic here
    }
}
