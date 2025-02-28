import java.sql.*;
import java.util.Scanner;

public class HabitLogger {
    private static final String dbPath = "HabitLogger/src/main/resources/Habits.db";

    public static void main(String[] args) {
        initializeDatabase();
        mainMenu();
    }

    private static void mainMenu() {
        System.out.println("MAIN MENU\n");
        System.out.println("What would you like to do?\n");
        System.out.println("Type 0 to Close Application.");
        System.out.println("Type 1 to View All Records.");
        System.out.println("Type 2 to Insert Record.");
        System.out.println("Type 3 to Delete Record.");
        System.out.println("Type 4 to Update Record.");
        System.out.println("-------------------------\n");

        handleUserInput();
    }

    private static void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        try {
            int userInput = scanner.nextInt();
            switch (userInput) {
                case 0:
                    System.out.println("Closing Application...");
                    break;
                case 1:
                    System.out.println("Viewing All Records...");
                    viewAllRecords();
                    break;
                case 2:
                    System.out.println("Inserting Record...");
                    insertNewRecord();
                    break;
                case 3:
                    System.out.println("Deleting Record...");
                    deleteRecord();
                    break;
                case 4:
                    System.out.println("Updating Record...");
                    updateRecord();
                    break;
                default:
                    System.out.println("Invalid Input. Please try again.");
                    handleUserInput();
            }
        } catch (Exception e) {
//            System.out.println("Invalid input. Please try again.");
//            handleUserInput();
            throw new RuntimeException(e);
        }
    }

    private static void viewAllRecords() {
        try (Connection connection = getConnectionToDatabase()) {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM habits");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String date = rs.getString("date");
                System.out.println("ID: " + id + ", Name: " + name + ", Description: " + description + ", Date: " + date);
            }

            mainMenu();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateRecord() {
    }

    private static void deleteRecord() {
    }

    private static void insertNewRecord() {
    }

    private static void insertIntoDatabase() {
        try (Connection connection = getConnectionToDatabase()) {
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnectionToDatabase() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            Statement st = connection.createStatement();
            st.execute("create table if not exists habits (id integer primary key autoincrement, name text not null, description text, date text);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
