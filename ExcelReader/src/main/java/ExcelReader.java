import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class ExcelReader {
    public static void main(String[] args) {
        System.out.println("Reading Excel file");
        String[][] data = readExcel();

        System.out.println("Inserting data into database");
        insertDataIntoDatabase(data);
    }

    private static String[][] readExcel(){
        try (FileInputStream file = new FileInputStream("ExcelReader/src/main/resources/test_data.xlsx")) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("data");
            String[][] userData = new String[sheet.getLastRowNum()+1][sheet.getRow(0).getLastCellNum()];
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                if(sheet.getRow(rowIndex) != null) {
                    for (int colIndex = 0; colIndex < sheet.getRow(rowIndex).getLastCellNum(); colIndex++) {
                        userData[rowIndex][colIndex] = sheet.getRow(rowIndex).getCell(colIndex).toString();
                    }
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

    private static void insertDataIntoDatabase(String[][] data) {
        try (Connection connection = getConnectionToDatabase()) {
            Statement st = connection.createStatement();
            String query = String.format("""
                    drop table if exists user_info;\s
                    create table user_info (id serial primary key not null, name varchar(50), email varchar(50));
                    insert into user_info (id, name, email) values %s
                    """, (Object) data);
            ResultSet rs = st.executeQuery(query);
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
