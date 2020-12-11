import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.ArrayList;

public class PassportChecker {
    public static void main(String[] args) {
        ArrayList<HashMap<String, String>> passports = readInPassports(args[0]);
        System.out.println("Number of passports with all necessary fields: "
            + countPassportsWithAllNecessaryFields(passports));
        System.out.println("Number of passports with all necessary fields and valid values: "
            + countPassportsWithAllNecessaryFieldsAndValidValues(passports));
    }
    private static ArrayList<HashMap<String, String>> readInPassports(String fileName) {
        ArrayList<HashMap<String, String>> passports = new ArrayList<HashMap<String, String>>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                passports.add(getNextPassport(fileScanner));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return passports;
    }
    private static HashMap<String, String> getNextPassport(Scanner fileScanner) {
        HashMap<String, String> passport = new HashMap<String, String>();
        while (fileScanner.hasNext()) {
            String nextLine = fileScanner.nextLine();
            if (nextLine.length() > 0) {
                String[] nextLineElements = nextLine.split(" ");
                for (String element : nextLineElements) {
                    String[] splitElement = element.split(":");
                    passport.put(splitElement[0], splitElement[1]);
                }
            } else {
                return passport;
            }
        }
        return passport;
    }
    private static int countPassportsWithAllNecessaryFields(
            ArrayList<HashMap<String, String>> passports) {
        int validPassportCounter = 0;
        for (HashMap<String, String> passport : passports) {
            if (hasAllNecessaryElements(passport)) {
                validPassportCounter ++;
            }
        }
        return validPassportCounter;        
    }
    private static int countPassportsWithAllNecessaryFieldsAndValidValues(
            ArrayList<HashMap<String, String>> passports) {
        int validPassportCounter = 0;
        for (HashMap<String, String> passport : passports) {
            if (hasAllNecessaryElements(passport) 
                    && hasAllValidEntries(passport)) {
                validPassportCounter ++;
            }
        }
        return validPassportCounter;
    }
    private static boolean hasAllNecessaryElements(HashMap<String, String> passport) {
        return (passport.containsKey("byr") && passport.containsKey("iyr") 
            && passport.containsKey("eyr") && passport.containsKey("hgt")
            && passport.containsKey("hcl") && passport.containsKey("ecl")
            && passport.containsKey("pid"));
    }
    private static boolean hasAllValidEntries(HashMap<String, String> passport) {
        return (hasValidBYR(passport) && hasValidIYR(passport) 
            && hasValidEYR(passport) && hasValidHGT(passport)
            && hasValidHCL(passport) && hasValidECL(passport)
            && hasValidPID(passport));
    }
    private static boolean hasValidBYR(HashMap<String, String> passport) {
        return matchesPattern(passport.get("byr"), "19[2-9][0-9]|200[0-2]");
    }
    private static boolean hasValidIYR(HashMap<String, String> passport) {
        return matchesPattern(passport.get("iyr"), "201[0-9]|2020");
    }
    private static boolean hasValidEYR(HashMap<String, String> passport) {
        return matchesPattern(passport.get("eyr"), "202[0-9]|2030");
    }
    private static boolean hasValidHGT(HashMap<String, String> passport) {
        return matchesPattern(passport.get("hgt"), "1[5-8][0-9]cm|19[0-3]cm|59in|6[0-9]in|7[0-6]in");
    }
    private static boolean hasValidHCL(HashMap<String, String> passport) {
        return matchesPattern(passport.get("hcl"), "#[a-f0-9]{6}");
    }
    private static boolean hasValidECL(HashMap<String, String> passport) {
        return matchesPattern(passport.get("ecl"), "amb|blu|brn|gry|grn|hzl|oth");
    }
    private static boolean hasValidPID(HashMap<String, String> passport) {
        return matchesPattern(passport.get("pid"), "[0-9]{9}");
    }
    private static boolean matchesPattern(String entry, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entry);
        return matcher.matches();
    }
}
