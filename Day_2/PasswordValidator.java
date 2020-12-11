import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PasswordValidator {
    public static void main(String[] args) {
        ArrayList<String[]> policiesAndPasswords = readInFile(args[0]);
        String[] policies = policiesAndPasswords.get(0);
        String[] passwords = policiesAndPasswords.get(1);
        System.out.println("Number of passwords fullfilling policy 1: " 
            + countPasswordsFullfillingNumberOfOccurencesFallWithinRangePolicy(policies, passwords));
        System.out.println("Number of passwords fullfilling policy 2: "
            + countPasswordsFullfillingExactlyOneCharacterAtPositionsIsCorrectPolicy(policies, passwords));
    }
    private static ArrayList<String[]> readInFile(String fileName) {
        ArrayList<String[]> dataRead = new ArrayList<String[]>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                dataRead.add(fileScanner.nextLine().split(": "));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] policies = new String[dataRead.size()];
        String[] passwords = new String[dataRead.size()];
        for (int i = 0; i < dataRead.size(); i ++) {
            policies[i] = dataRead.get(i)[0];
            passwords[i] = dataRead.get(i)[1];
        }
        ArrayList<String[]> policiesAndPasswords = new ArrayList<String[]>();
        policiesAndPasswords.add(policies);
        policiesAndPasswords.add(passwords);
        return policiesAndPasswords;
    }
    private static int countPasswordsFullfillingNumberOfOccurencesFallWithinRangePolicy(
            String[] policies, String[] passwords) {
        int counter = 0;
        for (int i = 0; i < policies.length; i ++) {
            String policy = policies[i];
            String password = passwords[i];
            if (passwordFullfillsNumberOfOccurencesFallWithinRangePolicy(
                    password, policy)) {
                counter ++;
            }
        }
        return counter;
    }
    private static int countPasswordsFullfillingExactlyOneCharacterAtPositionsIsCorrectPolicy(
            String[] policies, String[] passwords) {
        int counter = 0;
        for (int i = 0; i < policies.length; i ++) {
            String policy = policies[i];
            String password = passwords[i];
            if (passwordFullfillsExactlyOneCharacterAtPositionsIsCorrectPolicy(
                    password, policy)) {
                counter ++;
            }
        }
        return counter;
    }
    private static boolean passwordFullfillsExactlyOneCharacterAtPositionsIsCorrectPolicy(
            String password, String policy) {
        ArrayList policyDetails = getPolicyDetails(policy);
        int firstPosition = (int) policyDetails.get(0);
        int secondPosition = (int) policyDetails.get(1);
        char letter = (char) policyDetails.get(2);
        if (password.charAt(firstPosition - 1) == letter 
                ^ password.charAt(secondPosition - 1) == letter) {
            return true;
        } else {
            return false;
        }
    }
    private static boolean passwordFullfillsNumberOfOccurencesFallWithinRangePolicy(
            String password, String policy) {
        ArrayList policyDetails = getPolicyDetails(policy);
        int minimalOccurences = (int) policyDetails.get(0);
        int maximalOccurences = (int) policyDetails.get(1);
        char letter = (char) policyDetails.get(2);
        long numberOfOccurences = getNumberOfOccurences(password, letter);
        if (numberOfOccurences >= minimalOccurences && numberOfOccurences <= maximalOccurences) {
            return true;
        } else {
            return false;
        }
    }
    private static ArrayList getPolicyDetails(String policy) {
        ArrayList policyDetails = new ArrayList();
        policyDetails.add(Integer.parseInt(policy.split("-")[0]));
        policyDetails.add(Integer.parseInt(policy.split(" ")[0].split("-")[1]));
        policyDetails.add(policy.split(" ")[1].charAt(0));
        return policyDetails;
    }
    private static long getNumberOfOccurences(String password, char letter) {
        long count = password.chars().filter(ch -> ch == letter).count();
        return count;
    }
}