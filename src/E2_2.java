import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class E2_2 {

    private static HashMap<String, MacroStruct> MacroCode;

    private static class MacroStruct {
        String name, arg1, arg2;
        MacroStruct(String name, String arg1, String arg2) {
            this.name = name;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    public static void main(String[] args) {
        String str = removeComments("foo.c");
        str = replaceMacroCode(str);
        String[] strs = Scanner(str);
        for (String s : strs) {
            System.out.println(s);
        }
    }

    /*
    I assume that there are only two arguments of the macro code
     */
    public static String replaceMacroCode(String string) {
        MacroCode = new HashMap<>();
        String[] temps = string.split("\n");
        for (String temp : temps) {
            if (Pattern.matches("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*\\(.*\\))(\\s+)(..*)", temp)) {
                Pattern pattern = Pattern.compile("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*)(\\(([A-Z]+)(\\s*,\\s*)([A-Z]+)\\)\\s+)(..*)");
                Matcher matcher = pattern.matcher(temp);
                if (matcher.find()) {
                    System.out.println(matcher.group(0));
                    System.out.println(matcher.group(1));
                    System.out.println(matcher.group(2));
                    System.out.println(matcher.group(3));
                    System.out.println(matcher.group(4));
                    System.out.println(matcher.group(5));
                    System.out.println(matcher.group(6));
                    System.out.println(matcher.group(7));
                    System.out.println(matcher.group(8));

                }
                MacroStruct macroStruct = new MacroStruct(matcher.group(3), matcher.group(5), matcher.group(6));
                String regex = macroStruct.name + "(" + "\\s*" + macroStruct.arg1 + "\\s*" + macroStruct.arg2 + "\\s*" + ")";


                MacroCode.put(regex, macroStruct);
            }
        }
        String result = string.replaceAll("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*\\(.*\\))(\\s+)(..*)\\n", "");
        for (String str : MacroCode.keySet()) {
            MacroStruct macroStruct = MacroCode.get(str);

        }
        return result;
    }

    public static String[] Scanner(String string) {
        boolean inQuote = false;
        Stack<String> stk = new Stack<>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\"'(){},;+-<>=!*/% ", true);
        while (stringTokenizer.hasMoreTokens()) {
            String tokens = stringTokenizer.nextToken();
            if (tokens.equals("\"")) {
                if (inQuote) {
                    String str = stk.pop();
                    str += tokens.trim();
                    stk.push(str);
                } else {
                    stk.push(tokens.trim());
                }
                inQuote = !inQuote;
                continue;
            }
            if (inQuote) {
                String str = stk.pop();
                str += tokens;
                stk.push(str);
            } else {
                if (stk.empty()) {
                    if (!Pattern.matches("\\s*", tokens)) {
                        stk.push(tokens.trim());
                    }
                } else if (stk.peek().equals("<") && tokens.equals("<")
                        || stk.peek().equals(">") && tokens.equals(">")
                        || stk.peek().equals(">") && tokens.equals("=")
                        || stk.peek().equals("<") && tokens.equals("=")
                        || stk.peek().equals("=") && tokens.equals("=")
                        || stk.peek().equals("!") && tokens.equals("=")
                        ) {
                    String str = stk.pop();
                    str += tokens;
                    stk.push(str.trim());
                } else {
                    if (!Pattern.matches("\\s*", tokens)) {
                        stk.push(tokens.trim());
                    }
                }
            }
        }
        String[] ar = new String[stk.size()];
        stk.toArray(ar);
        return ar;
    }


    public static String removeComments(String readingFilePath) {
        Path path = Paths.get(readingFilePath);
        String result = null;
        try {
            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader((new InputStreamReader(in)));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                sb.append(str + "\n");
            }
            result = replaceMethod2(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String replaceMethod1(String str) {
        String res = str.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
        return res;
    }

    public static String replaceMethod2(String str) {
        StringBuilder sb = new StringBuilder();
        Stack<String> stk = new Stack<>();
        int n = str.length();
        for (int i = 0; i < n - 1; i++) {
            if (stk.empty()) {
                if (str.charAt(i) == '"') {
                    stk.push("\"");
                    sb.append(str.charAt(i));
                } else if (str.charAt(i) == '/' && str.charAt(i + 1) == '*') {
                    stk.push("/*");
                    i++;
                } else if (str.charAt(i) == '/' && str.charAt(i + 1) == '/') {
                    stk.push("//");
                } else {
                    sb.append(str.charAt(i));
                }
            } else if (stk.peek().equals("\"")) {
                if (str.charAt(i) == '"') {
                    stk.pop();
                }
                sb.append(str.charAt(i));
            } else if (stk.peek().equals("/*")) {
                if (str.charAt(i) == '*' && str.charAt(i + 1) == '/') {
                    stk.pop();
                    i++;
                }
            } else if (stk.peek().equals("//")) {
                if (str.charAt(i) == '\n') {
                    stk.pop();
                }
            }
        }
        return sb.toString();
    }
}