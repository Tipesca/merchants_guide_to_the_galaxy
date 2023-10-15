import com.mechant.galaxy.metrics.Metal;
import com.mechant.galaxy.metrics.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static final String HOW_MANY_CREDITS_IS_ = "how many Credits is ";
    public static final String HOW_MUCH_IS_ = "how much is ";

    private static Map<String, Symbol> romanSymbols =
            new HashMap<String, Symbol> () {
        {
            put("I", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(1)
                    .repeated(3)
                    .subtracted(Arrays.asList("V", "X"))
                    .build());
            put("V", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(5)
                    .repeated(0)
                    .subtracted(new ArrayList<>())
                    .build());
            put("X", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(10)
                    .repeated(3)
                    .subtracted(Arrays.asList("L", "C"))
                    .build());
            put("L", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(50)
                    .repeated(0)
                    .subtracted(new ArrayList<>())
                    .build());
            put("C", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(100)
                    .repeated(3)
                    .subtracted(Arrays.asList("D", "M"))
                    .build());
            put("D", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(500)
                    .repeated(0)
                    .subtracted(new ArrayList<>())
                    .build());
            put("M", Symbol.builder()
                    .intergalacticUnit(null)
                    .value(1000)
                    .repeated(3)
                    .subtracted(new ArrayList<>())
                    .build());
        }
    };
    private static Map<String, String> galacticToRoman =
            new HashMap<>();
    private static Map<String, Metal> metals =
            new HashMap<>();
    private static boolean isValidQuery;

    public static void main(String[] args) {
        Map<String, String> intergalacticToRoman = new HashMap<>();
        Map<String, String> metalPrices = new HashMap<>();
        String[] queries = {
                "glob is I",
                "prok is V",
                "pish is X",
                "tegj is L",
                "glob glob Silver is 34 Credits",
                "glob prok Gold is 57800 Credits",
                "pish pish Iron is 3910 Credits",
                "how much is pish tegj glob glob ?",
                "how many Credits is glob prok Silver ?",
                "how many Credits is glob prok Gold ?",
                "how many Credits is glob prok Iron ?",
                "how much wood could a woodchuck chuck if a woodchuck could chuck wood?"
        };

        for (String query : queries) {
            isValidQuery = false;
            mapGalacticUnits(query);
            defineMetalPrices(query);
            calculateSymbols(query);
            calculateMetalPrices(query);

            if (!isValidQuery)
                System.out.println("I have no idea what you are talking about");
        }
    }

    private static void calculateMetalPrices(String query) {

        if (query.contains(HOW_MANY_CREDITS_IS_)) {
            String prompt = query.substring(HOW_MANY_CREDITS_IS_.length(), query.indexOf(" ?"));
            int quantity = determinePrice(prompt);
            System.out.println(prompt + " is " + quantity + " Credits");
            isValidQuery = true;
        }
    }

    private static int determinePrice(String prompt) {

        String metalName = getMetalName(prompt);
        Metal metal = metals.get(metalName);
        double pricePerUnit = Double.valueOf(metal.getPrice()) / Double.valueOf(metal.getQuantity());
        int quantity = calculateQuantity(prompt);

        double total = pricePerUnit * quantity;
        return (int) total;
    }

    private static void calculateSymbols(String query) {
        if (query.startsWith(HOW_MUCH_IS_)) {

            String askAddition = query.substring("how much is ".length(), query.indexOf(" ?"));
            int quantity = calculateQuantity(askAddition);
            System.out.println(askAddition + " is " + quantity);
            isValidQuery = true;
        }
    }

    private static void defineMetalPrices(String query) {
        Pattern pattern = Pattern.compile("(.+) is (\\d+) Credits");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String definition = matcher.group(1);
            String price = matcher.group(2);
            int quantity = calculateQuantity(definition);
            String metalName = getMetalName(definition);
            metals.put(metalName, new Metal(metalName, quantity, Integer.parseInt(price)));
            isValidQuery = true;
        }
    }

    private static String getMetalName(String definition) {
        String[] definitions = definition.split(" ");

        for (int i = 0; i < definitions.length; i++) {
            if (galacticToRoman.get(definitions[i]) == null)
                return definitions[i];
        }
        return null;
    }

    private static int calculateQuantity(String definition) {
        String[] galacticSymbols = definition.split(" ");
        StringBuilder romanSymbolBuilder = new StringBuilder();

        for (int i = 0; i < galacticSymbols.length; i++) {
            if (galacticToRoman.containsKey(galacticSymbols[i]))
                romanSymbolBuilder.append(galacticToRoman.get(galacticSymbols[i]));
        }

        return romanToValue(romanSymbolBuilder.toString());
    }

    private static int romanToValue(String romanString) {
        int size = romanString.length();
        int total = 0;
        for (int i = 0; i < size; i++) {
            String currentCharacter = String.valueOf(romanString.charAt(i));
            Symbol currentSymbol = romanSymbols.get(currentCharacter);

            if (i == (size - 1)) { // check if last symbol
                total += currentSymbol.getValue();
            } else {
                String nextCharacter = String.valueOf(romanString.charAt(i + 1));
                Symbol nextSymbol = romanSymbols.get(nextCharacter);

                if (isCurrentBigger(currentSymbol, nextSymbol)
                        && !nextSymbol.getSubtracted().contains(currentCharacter)) {
                    total += currentSymbol.getValue();
                } else {
                    total += (nextSymbol.getValue() - currentSymbol.getValue());
                    i++;
                }
            }
        }
        return total;
    }

    private static boolean isCurrentBigger(Symbol currentSymbol, Symbol nextSymbol) {
        return currentSymbol.getValue() >= nextSymbol.getValue();
    }

    private static void mapGalacticUnits(String query) {
        String[] parts = query.split(" is ");
        if (parts.length == 2 && parts[1].length() == 1) {
            String intergalacticUnit = parts[0];
            String romanNumeral = parts[1];
            galacticToRoman.put(intergalacticUnit, romanNumeral);
            updateConversion(romanNumeral, intergalacticUnit);
            isValidQuery = true;
        }
    }

    private static void updateConversion(String romanNumeral, String intergalacticUnit) {
        romanSymbols.get(romanNumeral).setIntergalacticUnit(intergalacticUnit);
    }
}
