import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class AllergenFinder {
    public static void main(String[] args) {
        var allergensAndIngredients = readInData(args[0]);
        ArrayList<HashSet<String>> allergens = allergensAndIngredients.get(0);
        ArrayList<HashSet<String>> ingredients = allergensAndIngredients.get(1);
        var allergensToIngredients = new HashMap<String, String>();
        trimmDownIngredientsAndAllergensListsAndFillAllergensToIngredientsMap(
            allergens, ingredients, allergensToIngredients);
        System.out.println("Count of ingredients not containing any listed allergen: "
            + getUnmatchedIngredientCount(ingredients));
        System.out.println("Canonical dangerous ingredient list: "
            + makeCanonicalDangerousIngredientList(allergensToIngredients));
    }
    private static ArrayList<ArrayList<HashSet<String>>> readInData(String fileName) {
        var allergensAndIngredients = new ArrayList<ArrayList<HashSet<String>>>();
        var allergens = new ArrayList<HashSet<String>>();
        var ingredients = new ArrayList<HashSet<String>>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                String[] splitLine = fileScanner.nextLine().split(" \\(contains ");
                allergens.add(new HashSet(Arrays.asList(splitLine[1].split(", |\\)"))));
                ingredients.add(new HashSet(Arrays.asList(splitLine[0].split(" "))));
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        allergensAndIngredients.add(allergens);
        allergensAndIngredients.add(ingredients);
        return allergensAndIngredients;
    }
    private static void trimmDownIngredientsAndAllergensListsAndFillAllergensToIngredientsMap(
            ArrayList<HashSet<String>> allergens, ArrayList<HashSet<String>> ingredients, HashMap<String,
            String> allergensToIngredients) {
        while (true) {
            ArrayList<String> ingredientAndAllergen =
                findMatchingIngredientAndAllergen(allergens, ingredients);
            if (ingredientAndAllergen.size() == 2) {
                String ingredient = ingredientAndAllergen.get(0);
                String allergen = ingredientAndAllergen.get(1);
                allergensToIngredients.put(allergen, ingredient);
                removeIngredientAndAllergenFromSets(allergens, ingredients, allergen, ingredient);
            } else {
                break;
            }
        }
    }
    private static int getUnmatchedIngredientCount(ArrayList<HashSet<String>> ingredients) {
        int unmatchedIngredientCount = 0;
        for (int j = 0; j < ingredients.size(); j ++) {
            unmatchedIngredientCount += ingredients.get(j).size();
        }
        return unmatchedIngredientCount;
    }
    private static String makeCanonicalDangerousIngredientList(
            HashMap<String, String> allergensToIngredients) {
        ArrayList<String> allergens = new ArrayList<String>(allergensToIngredients.keySet());
        Collections.sort(allergens);
        StringBuilder cdil = new StringBuilder();
        for (String allergen : allergens) {
            cdil.append(allergensToIngredients.get(allergen));
            cdil.append(",");
        }
        cdil.delete(cdil.length() - 1,cdil.length());
        return cdil.toString();
    }
    private static ArrayList<String> findMatchingIngredientAndAllergen(
            ArrayList<HashSet<String>> allergenSets, ArrayList<HashSet<String>> ingredientSets) {
        ArrayList<String> ingredientAndAllergen = new ArrayList<String>();
        HashSet<String> allAllergens = new HashSet<String>();
        HashSet<String> allIngredients = new HashSet<String>();
        for (HashSet<String> allergenSet : allergenSets) {
            allAllergens.addAll(allergenSet);
        }
        for (HashSet<String> ingredientSet : ingredientSets) {
            allIngredients.addAll(ingredientSet);
        }
        for (String allergen : allAllergens) {
            var allergensIntersection = (HashSet<String>) allAllergens.clone();
            var ingredientsIntersection = (HashSet<String>) allIngredients.clone();
            for (int i = 0; i < allergenSets.size(); i ++) {
                if (allergenSets.get(i).contains(allergen)) {
                    allergensIntersection.retainAll(allergenSets.get(i));
                    ingredientsIntersection.retainAll(ingredientSets.get(i));
                }
                if (allergensIntersection.size() == 1 && ingredientsIntersection.size() == 1) {
                    String ingredient = ingredientsIntersection.iterator().next();
                    ingredientAndAllergen.add(ingredient);
                    ingredientAndAllergen.add(allergen);
                    return ingredientAndAllergen;
                }
            }
        }
        return ingredientAndAllergen;
    }
    private static void removeIngredientAndAllergenFromSets(
            ArrayList<HashSet<String>> allergens, ArrayList<HashSet<String>> ingredients,
            String allergen, String ingredient) {
        for (int i = 0; i < allergens.size(); i ++) {
            allergens.get(i).remove(allergen);
            ingredients.get(i).remove(ingredient);
        }
    }
}