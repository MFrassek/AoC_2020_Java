import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class MessageValidator {
    public static void main(String[] args) {
        DataContainer dataContainer = readInData(args[0]);
        var rules = dataContainer.getRules();
        var messages = dataContainer.getMessages();
        List<String> rule0 = rules.get("0");
        ArrayList<String> newRule = new ArrayList<String>();
        boolean stillThingsToReplace = true;
        for (int i = 0; i < 14; i ++) {
            stillThingsToReplace = false;
            for (String ele : rule0) {
                if (rules.containsKey(ele)) {
                    stillThingsToReplace = true;
                    newRule.addAll(rules.get(ele));
                } else {
                    newRule.add(ele);
                }
            }
            rule0 = (ArrayList<String>) newRule.clone();
            newRule = new ArrayList<String>();
        }
        StringBuilder rule = new StringBuilder();
        rule.append("^");
        for (String ele : rule0) {
            rule.append(ele);
        }
        rule.append("$");
        int matchCount = 0;
        String finalRule = rule.toString();
        for (String message : messages) {
            if (message.matches(finalRule)) {
                matchCount ++;
            }
        }
        System.out.println(matchCount);
    }
    private static DataContainer readInData(String fileName) {
        var rules = new HashMap<String, List<String>>();
        var messages = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                String currentLine = inScanner.nextLine();
                if (currentLine.equals("")) {
                    break;
                }
                String[] colonSplitLine = currentLine.split(": ");
                String ruleIndex = colonSplitLine[0];
                String cleanedRule = "( " + colonSplitLine[1].replace("\"","") + " )";
                rules.put(ruleIndex, Arrays.asList(cleanedRule.split(" ")));
            }
            while (inScanner.hasNext()) {
                messages.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return new DataContainer(rules, messages);
    }
}

class DataContainer {
    private final HashMap<String, List<String>> rules;
    private final ArrayList<String> messages;

    public DataContainer(HashMap<String, List<String>> rules, ArrayList<String> messages) {
        this.rules = rules;
        this.messages = messages;
    }

    public HashMap<String, List<String>> getRules() {
        return rules;
    }
    public ArrayList<String> getMessages() {
        return messages;
    }
}