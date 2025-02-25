import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class ExcelReader {
    public static void main(String[] args) {
        System.out.println("Reading Excel file");
        StringBuilder data = readExcel();

        System.out.println("Inserting data into database");
        insertDataIntoDatabase(data);
    }

    private static StringBuilder readExcel() {
        try (FileInputStream file = new FileInputStream("ExcelReader/src/main/resources/test_data.xlsx")) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("data");
            StringBuilder userData = new StringBuilder();
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                if (sheet.getRow(rowIndex) != null) {
                    userData.append("(");
                    for (int colIndex = 0; colIndex < sheet.getRow(rowIndex).getLastCellNum(); colIndex++) {
                        userData.append(sheet.getRow(rowIndex).getCell(colIndex).toString()).append(colIndex < sheet.getRow(rowIndex).getLastCellNum() - 1 ? "," : "");
                    }
                    userData.append(rowIndex < sheet.getLastRowNum() ? ")," : ")");
                }
            }
            return userData;
        } catch (IOException e) {
            throw new RuntimeException("Error reading excel file", e);
        }
    }

    private static Connection getConnectionToDatabase() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/Excel Reader", "postgres", "password24$");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertDataIntoDatabase(StringBuilder data) {
        // removing the first row
        int firstRowEnd = data.indexOf("),") + 2;
        data.delete(0, firstRowEnd);

        try (Connection connection = getConnectionToDatabase()) {
            Statement st = connection.createStatement();

            st.execute("drop table if exists user_info");
            st.execute("create table user_info (id serial primary key not null, name varchar(50), age int, email varchar(50))");

            String formattedData = data.toString()
                    .replaceAll("([a-zA-Z]+@[a-zA-Z.]+)", "'$1'")
                    .replaceAll("([a-zA-Z]+),", "'$1',")
                    .replaceAll("([0-9]+)\\.0", "$1");

            st.execute("insert into user_info (id, name, age, email) values " + formattedData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
