import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private void run() {
        try {
            Connection conn = ConnectionFactory.getConnection();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Main() {
        run();
    }

    public static void main(String[] args) {
        new Main();
    }
}