package org.example;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.JSONObject;

public class Drinks {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        String[] drinkCategories = drinkCategories();
        printDrinks(drinkCategories);

        String category = checkDrinkChoice(drinkCategories, "category");

        String[] drinkList = drinkList(category);
        printDrinks(drinkList);

        String drink = checkDrinkChoice(drinkList, "drink");

        System.out.println(drinkDetails(drink));
    }

    private static void printDrinks(String[] drinks) {
        for (String drink : drinks) {
            System.out.printf("-----------------------\n| %-20s|\n", drink);
        }
        System.out.println("-----------------------\n");
    }

    private static String checkDrinkChoice(String[] drinks, String whatToChoose) {
        Scanner s = new Scanner(System.in);
        String drink = "";
        while (!Objects.equals(drink, Arrays.stream(drinks).toString())) {
            try {
                System.out.printf("\nPlease choose a %s: \n", whatToChoose);
                drink = s.nextLine();

                if (Arrays.stream(drinks).noneMatch(drink::equals)) {
                    throw new Exception();
                }
                break;
            } catch (Exception e) {
                System.out.printf("Invalid %s \n", whatToChoose);
            }
        }
        return drink;
    }

    private static String[] drinkCategories() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            String[] drinks = new String[jsonResponse.getJSONArray("drinks").length()];

            for (int i = 0; i < drinks.length; i++) {
                drinks[i] = jsonResponse.getJSONArray("drinks").getJSONObject(i).getString("strCategory");
            }
            return drinks;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] drinkList(String category) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=" + category))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            String[] drinks = new String[jsonResponse.getJSONArray("drinks").length()];

            for (int i = 0; i < drinks.length; i++) {
                drinks[i] = jsonResponse.getJSONArray("drinks").getJSONObject(i).getString("strDrink");
            }
            return drinks;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject drinkDetails(String drink){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + drink))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject jsonDetails = new JSONObject();
            JSONObject drinkObject = jsonResponse.getJSONArray("drinks").getJSONObject(0);

            Set<String> keys = drinkObject.keySet();
            for (String key : keys) {
                if (!drinkObject.isNull(key)) {
                    Object value = drinkObject.get(key);

                    if (!value.toString().isEmpty()) {
                        jsonDetails.put(key, value);
                    }
                }
            }

            return jsonDetails;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}