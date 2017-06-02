package apis;

import network.HttpMethods;
import org.json.JSONObject;
import utils.Color;
import utils.ColorToRgb;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;


public class Lifx {

    private final static String LIFX_TOKEN = "";

    final static String TOKEN_TYPE = "Bearer";

    public static boolean processPossibleCommand(String msg) {

        if (LIFX_TOKEN.equalsIgnoreCase("")) {
            System.out.println("Please get a token from: https://cloud.lifx.com/settings");
            return false;
        }

        final String request = msg.trim().toLowerCase();
        final ArrayList<String> requestTokens = getRequestTokens(request);
        if(requestTokens.size()>LARGEST_REQUEST_LENGTH){
            return false; //exits if the size is larger than the largest possible request
        }

        if (isDanceParty(request)) {
            startDanceParty();
            return true;
        }
        if (isStopDanceParty(request)) {
            endDanceParty();
            return true;
        }
        if(isNotCommand(requestTokens)){
            return false; //exits if its not a command
        }

        if(isTurnOff(requestTokens)){
            try {
                turnOff();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if(isTurnOn(requestTokens)){
            try {
                turnOn();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if(isColorChange(requestTokens)){
            try {
                changeColor(request);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if(isBrightnessChange(requestTokens)){
            try {
                changeBrightness(request);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }











    /*
	 * http://developer.lifx.com
	 * HTTP API requiring a user's access token
	 */

    final static String LIFX_API_LINK = "https://api.lifx.com/v1/lights";
    final static int LARGEST_REQUEST_LENGTH = 10;

    private static List<String> danceParties = new ArrayList<>();

    private static void startDanceParty() {
        danceParties.add(LIFX_TOKEN);
        Thread danceThread = new Thread(new LIFXDanceParty(LIFX_TOKEN));
        danceThread.start();
    }

    private static void endDanceParty() {
        danceParties.remove(LIFX_TOKEN);
    }

    private static boolean isDanceParty(String request) {
        if (request.equalsIgnoreCase("dance party")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isStopDanceParty(String request) {
        if (request.contains("stop")) {
            return true;
        } else {
            return false;
        }
    }


    public static class LIFXDanceParty implements Runnable{

        String accessToken;

        private Timer timer;

        public LIFXDanceParty(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public void run() {
            final Random rand = new Random();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run() {
                    if(!danceParties.contains(accessToken)){
                        timer.cancel();
                    }
                    int color = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                    String colorRequest = createColorRequestString(color);
                    try {
                        setColor(accessToken, "all", colorRequest, false, 0.5f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000, 250);
        }
    }

    public static boolean isTurnOn(ArrayList<String> tokens){
        if(tokens.contains("turn")&&tokens.contains("on")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isTurnOff(ArrayList<String> tokens){
        if(tokens.contains("turn")&&tokens.contains("off")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isColorChange(ArrayList<String> tokens) {
        boolean hasColor = false;
        for(String color: ColorToRgb.getInstance().getColorNameList()) {
            for(String token: tokens){
                if(token.equals(color.toLowerCase())) {
                    hasColor = true;
                }
            }
        }
        if (hasColor&&containsRequestWord(tokens)) {
            return true;
        } else if (containsRequestWord(tokens)) {
            logMissingColor(tokens);
        }
        return false;
    }

    private static void logMissingColor(ArrayList<String> tokens) {
        //TODO
    }

    public static boolean isBrightnessChange(ArrayList<String> tokens){
        boolean hasPercent = false;
        for(String token: tokens){
            if(token.contains("%")){
                hasPercent = true;
            }
        }
        if(hasPercent&&(tokens.contains("brightness")||containsRequestWord(tokens))){
            return true;
        }else{
            return false;
        }
    }

    public static boolean containsRequestWord(ArrayList<String> tokens){
        String[] requestTokens = {"want","give","change","make","wish","gimme","turn"};

        for (String token: requestTokens){
            if (tokens.contains(token)){
                return true;
            }
        }
        return false;
    }




    public static void setPower(String accessToken, String selector, boolean isOn) throws IOException {
        setPower(accessToken, selector, isOn, 1.0f);
    }

    /**
     * This method is the raw HTTP request for the "power" endpoint of the LIFX API. See API documentation <a href="http://developer.lifx.com/#set-power">LIFX API</a>
     *
     * @param accessToken
     * 		a user's unique access token for their lights
     * @param selector
     * 		which lights should be effected by this request
     * @param isOn
     * 		the new power state for the lights
     * @param duration
     * 		the duration of the change taking place. default power change value is 1.0f
     */
    public static void setPower(String accessToken, String selector, boolean isOn, float duration) throws IOException {

        Invocation.Builder webResource = HttpMethods.getInstance().getClient()
                .target(String.format("%s/%s/%s", LIFX_API_LINK, selector, "state"))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        String.format("%s %s", TOKEN_TYPE,
                                accessToken));

        JSONObject entity = new JSONObject();
        if(isOn){
            entity.put("power", "on");
        }else{
            entity.put("power", "off");
        }
        entity.put("duration", duration);

        String response = HttpMethods.getInstance().put(webResource, entity.toString(), MediaType.APPLICATION_JSON_TYPE);
        System.out.println("Response: " + response);
    }

    /**
     * This method is the raw HTTP request for the "color" endpoint of the LIFX API. See API documentation <a href="http://developer.lifx.com/#set-color">LIFX API</a>
     *
     * @param accessToken
     * 		a user's unique access token for their lights
     * @param selector
     * 		which lights should be effected by this request
     * @param colorRequest
     * 		the final string to be sent to the color endpoint
     * @param turnOn
     * 		whether or not the bulb should be turned on with this request. default should be false because color requests can be made without turning the lights on
     * @param duration
     * 		the duration of the change taking place. default color change value is 1.0f
     */
    public static void setColor(String accessToken, String selector, String colorRequest, boolean turnOn, float duration) throws IOException {
        Invocation.Builder webResource = HttpMethods.getInstance().getClient()
                .target(String.format("%s/%s/%s", LIFX_API_LINK, selector, "state"))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        String.format("%s %s", TOKEN_TYPE,
                                accessToken));

        JSONObject entity = new JSONObject();
        entity.put("color", colorRequest);
        if (turnOn){
            entity.put("power", "on");
        }
        entity.put("duration", duration);

        String response = HttpMethods.getInstance().put(webResource, entity.toString(), MediaType.APPLICATION_JSON_TYPE);
        System.out.println("Response: " + response);
    }











    public static void changeBrightness(String request) throws IOException {
        int position = request.indexOf("%");
        StringBuilder brightnessRequest = new StringBuilder();
        brightnessRequest.append("brightness:");
        String percentBrightness = "";
        if(request.substring(position-2, position).equals("00")){
            percentBrightness = "100%";
        }else{
            percentBrightness = request.substring(position-2, position+1);
        }
        brightnessRequest.append(percentBrightness);
        setColor(LIFX_TOKEN, "all", brightnessRequest.toString(), false, 1.0f);
    }

    public static void changeColor(String request) throws IOException {
        Hashtable<String, Integer> colorWithSpaces = new Hashtable<String, Integer>();
        for(String color: ColorToRgb.getInstance().getColorNameList()){
            if(request.contains(color.toLowerCase())){
                colorWithSpaces.put(color, color.length()-color.replaceAll(" ", "").length());
            }
        }
        int maxValue = -1;
        String maxKey = "";
        for(String key: colorWithSpaces.keySet()){
            if(colorWithSpaces.get(key)>maxValue){
                maxValue = colorWithSpaces.get(key);
                maxKey = key;
            }
        }
        int color = ColorToRgb.getInstance().nameToColor(maxKey);
        String colorRequest = createColorRequestString(color);
        setColor(LIFX_TOKEN, "all", colorRequest, false, 1.0f);
    }

    private static String createColorRequestString(int color) {
        return "rgb:" + Color.red(color) + "," + Color.green(color) + "," + Color.blue(color);
    }

    public static void turnOff() throws IOException {
        setPower(LIFX_TOKEN, "all", false);
    }

    public static void turnOn() throws IOException {
        setPower(LIFX_TOKEN, "all", true);
    }

    public static boolean isNotCommand(ArrayList<String> tokens){
        boolean isNotCommand = false; //assumes it is a command
        String[] invalidTokens = {"who","what","where","when","why","how","can"}; //think about can
        for(String token: tokens){
            for(String invalid: invalidTokens){
                if(token.equals(invalid)){
                    isNotCommand = true;
                }
            }
            if(token.contains("?")){
                isNotCommand = true;
            }
        }
        return isNotCommand;
    }

    public static ArrayList<String> getRequestTokens(String request){
        String[] rawTokens = request.split("[ ]+");
        ArrayList<String> requestTokens = new ArrayList<String>();
        for(String token: rawTokens){
            requestTokens.add(token.replaceAll("[^a-z0-9%]", ""));//only characters are considered
        }
        return requestTokens;
    }




}

