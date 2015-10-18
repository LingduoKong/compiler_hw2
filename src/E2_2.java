import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class E2_2 {

    private static HashMap<String, MacroStruct> MacroCode;

    private static class MacroStruct {
        String name, replacement;
        MacroStruct(String name, String replacement) {
            this.name = name;
            this.replacement = replacement;
        }
    }

    public static void main(String[] args) {
        String str = removeComments("foo.c");
        String replaceMacro = replaceMacroCode(str);
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println("// remove comments");
            writer.println(str);
            writer.println("// replace macro code");
            writer.println(replaceMacro);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * I assume that there are exactly two arguments of the macro code, whose arguments only contains capital letters
     *
     */
    public static String replaceMacroCode(String string) {
        MacroCode = new HashMap<>();
        String[] temps = string.split("\n");
        for (String temp : temps) {
            if (Pattern.matches("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*\\(.*\\))(\\s+)(..*)", temp)) {
                Pattern pattern = Pattern.compile("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*)(\\(([A-Z]+)(\\s*,\\s*)([A-Z]+)\\)\\s+)(..*)");
                Matcher matcher = pattern.matcher(temp);
                if (matcher.find()) {
                    MacroStruct macroStruct = new MacroStruct(matcher.group(3), matcher.group(8));
                    macroStruct.replacement = macroStruct.replacement.replaceAll(matcher.group(5), "X");
                    macroStruct.replacement = macroStruct.replacement.replaceAll(matcher.group(7), "Y");
                    String regex = macroStruct.name + "\\s*\\(\\s*" + "(.+)" + "\\s*,\\s*" + "(.+)" + "\\s*\\)";
                    MacroCode.put(regex, macroStruct);
                }
            }
        }
        String result = string.replaceAll("(#define)(\\s+)([a-zA-Z_][a-zA-Z0-9_]*\\(.*\\))(\\s+)(..*)\\n", "");
        for (String s : MacroCode.keySet()) {
            Pattern pattern = Pattern.compile(s);
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                MacroStruct macro = MacroCode.get(s);
                String replacement = macro.replacement.replaceAll("X", matcher.group(1));
                replacement = replacement.replaceAll("Y", matcher.group(2));
                String regex = macro.name + "\\s*\\(\\s*" + matcher.group(1) + "\\s*,\\s*" + matcher.group(2) + "\\s*\\)";
                result = result.replaceAll(regex, replacement);
            }
        }
        return result;
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
            result = replaceMethod(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String replaceMethod(String str) {
        String res = str.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
        return res;
    }

}