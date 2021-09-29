import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

// This class is used to convert the currencies with some amount.
public class CurrencyCalculator {

    public static final String EXCHANGERATE_ENDPOINT = "http://api.exchangeratesapi.io/v1/latest";
    public static final String PARAM_ACCESS_KEY = "access_key";
    public static final String PARAM_TARGET = "symbols";
    private static final String ACCESS_KEY = "07759b6a44f50a1fd09a4db17fa16a32";
	public static void main(String[] args) throws IOException{
		
		try { 
            Scanner scanner = new Scanner(System.in);
		
            System.out.println("Welcome to the Currency Calculator.");

            System.out.println("(Type a Source Currency)");
            String sourceCurrency = scanner.nextLine();
            System.out.println("(Type a Target Currency)");
            String targetCurrency = scanner.nextLine();
            System.out.println("(Type a Monetary value)");
            String amountValue = scanner.nextLine();
            Double monetaryValue = Double.parseDouble(amountValue);
            scanner.close();
            
            // If any input is blank then just show some message!!
            if(sourceCurrency.isEmpty() || targetCurrency.isEmpty() || amountValue.isEmpty()) {
                System.out.println("You can not proceed with Currency Conversion as there are some missing information!!");
                return;
            }
            // ****** Calling an API *****
            String jsonString = getCurrencyConversion(sourceCurrency, targetCurrency); 
            JSONParser parser = new JSONParser();  
            JSONObject object = (JSONObject) parser.parse(jsonString); 
            
            // Handling error responses due to some authentication failure or both currencies are not found in system!!
            // We can also use wrapper error class to store error responses in more specific format!!
            if(object.containsKey("error")) {
                System.out.println("Error in Authentication!! "+object.get("error"));
                return;
            }

            JSONObject ratesJSON = (JSONObject) object.get("rates");

            // If any one of the currency is not available in system!!
            if(!ratesJSON.containsKey(sourceCurrency.toUpperCase()) || !ratesJSON.containsKey(targetCurrency.toUpperCase())) {
                System.out.println("Either one of the Source or target currency is not available in System!!");
                return;
            }

            Double sourceBaseEuro = Double.valueOf(ratesJSON.get(sourceCurrency.toUpperCase()).toString());
            Double targetBaseEuro = Double.valueOf(ratesJSON.get(targetCurrency.toUpperCase()).toString());
            System.out.println("Currency Conversion = " +convertCurrencyRate(sourceBaseEuro, targetBaseEuro, monetaryValue));
        } 
        catch(Exception ex) {
            System.out.println("There is some issue in Transaction!!" + ex);
        }
	}
	
    /****  This function is used to calculate the amount for conversion as we have 
     * source and target Currency conversion as a Base Euros */
    public static Double convertCurrencyRate(Double sourceAmount, Double targetAmount, Double monetaryValue) {
        Double conversionAmount = (targetAmount * monetaryValue) / sourceAmount;
        return conversionAmount;
    }

    // This function is used to hit the endpoint with parameters as symbols!!
	public static String getCurrencyConversion(String sourceCurrency, 
                                                String targetCurrency) throws IOException {

        String postData = "?" + PARAM_ACCESS_KEY + "=" + ACCESS_KEY;
		postData += "&" + PARAM_TARGET + "=" + sourceCurrency + "," + targetCurrency;

		HttpURLConnection connection = (HttpURLConnection) new URL(EXCHANGERATE_ENDPOINT+postData).openConnection();
        connection.setRequestMethod("GET");
		
        int responseCode = connection.getResponseCode();
		if(responseCode == 200){
			String response = "";
			Scanner scanner = new Scanner(connection.getInputStream());
			while(scanner.hasNextLine()){
				response += scanner.nextLine();
				response += "\n";
			}
			scanner.close();
            return response;
		}
        // an error happened
        else {
            String errorResponse = "";
            Scanner scanner = new Scanner(connection.getErrorStream());
			while(scanner.hasNextLine()){
				errorResponse += scanner.nextLine();
				errorResponse += "\n";
			}
			scanner.close();
            return errorResponse;
        }
	}

}