import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    // Class to represent each (x, y) root pair
    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        try {
            // Parse JSON input from file
            JSONObject inputJson = new JSONObject(new JSONTokener(new FileReader("input.json")));

            // Extract values of n and k
            JSONObject keys = inputJson.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");
            int m = k - 1; // degree of the polynomial

            // Extract and decode points
            List<Point> points = new ArrayList<>();
            for (String key : inputJson.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key); // x is the key of the object
                    JSONObject root = inputJson.getJSONObject(key);
                    int base = root.getInt("base");
                    String value = root.getString("value");
                    BigInteger y = new BigInteger(value, base); // Decode y with given base
                    points.add(new Point(x, y));
                }
            }

            // Use Lagrange interpolation to find constant term
            BigInteger constantTerm = lagrangeInterpolation(points, BigInteger.ZERO);
            System.out.println("The constant term (secret) c is: " + constantTerm);

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    // Function to perform Lagrange interpolation at x = 0 to find constant term
    public static BigInteger lagrangeInterpolation(List<Point> points, BigInteger xValue) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xi = BigInteger.valueOf(points.get(i).x);
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    term = term.multiply(xValue.subtract(xj))
                               .divide(xi.subtract(xj));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
