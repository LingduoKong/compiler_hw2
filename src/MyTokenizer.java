import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by lingduokong on 10/15/15.
 * use file read template from "https://docs.oracle.com/javase/tutorial/essential/io/file.html"
 */
public class MyTokenizer {
    public static void main(String[] args) {
        Path file = Paths.get(args[0]);
        HashMap<String, String> map = new HashMap<>();
        map.put("[0-9]+", "NUMBER");
        map.put("\\+", "PLUS_OP");
        map.put("\\-", "MINUS_OP");
        map.put("\\*", "MULT_OP");
        map.put("\\(", "L_PARENTHESIS");
        map.put("\\)", "R_PARENTHESIS");
        map.put("[a-zA-Z_][a-zA-Z0-9_]*", "NAME");
        System.out.printf("%-10s%s\n", "Lex", "Token");
        System.out.println("------------------------");
        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String str = null;
            while ((str = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "+-*/()=", true);
                while (st.hasMoreTokens()) {
                    outPut(st.nextToken(), map);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void outPut(String token, HashMap<String, String> map) {
        for (String str : map.keySet()) {
            if (Pattern.matches(str, token)) {
                System.out.printf("%-10s%s\n", token, map.get(str));
                break;
            }
        }
    }
}
