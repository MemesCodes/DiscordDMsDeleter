import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.fusesource.jansi.AnsiConsole;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class DiscordMassDeleteMain {
static Scanner in = new Scanner(System.in);
static ArrayList<String> names = new ArrayList<String>();
static String userid = "";
static String token = "";
static ArrayList<DiscordUser> Users = new ArrayList<DiscordUser>();
	   static boolean check(String s) {
	      if (s == null) // checks if the String is null {
	         return false;
	      
	      int len = s.length();
	      for (int i = 0; i < len; i++) {
	         // checks whether the character is not a letter
	         // if it is not a letter ,it will return false
	         if ((Character.isLetter(s.charAt(i)) == false) && Character.isDigit(s.charAt(i)) == false && s.charAt(i)!=' '&&s.charAt(i)!='#') {
	            return false;
	         }
	      }
	      return true;
	      }

	   
public static void main(String[] args) throws IOException, InterruptedException {

	StartScript();
}
public static void Delete(DiscordUser user) throws IOException, InterruptedException {
	JsonArray todelete = new JsonArray();
	System.out.println(CLRS.BLUE+"COLLECTING MESSAGES WITH "+user.name+CLRS.RESET);
	ArrayList<JsonObject> preparse = getMessages(user.id);
	for (JsonObject e : preparse) try{if (e.get("author").getAsJsonObject().get("id").getAsString().contentEquals(userid)) todelete.add(e);}catch(Exception f) {}
	double i =0;
	for (JsonElement e:todelete) {
		for(;;)
		try {
			i++;
			Utility.CLILoadingBar((int)((i/((double) todelete.size()))*100),"Deleting Messages with "+user.name+" ["+i+"/"+todelete.size()+"]");
		Response r0 = Jsoup.connect("https://discordapp.com/api/channels/"+user.id+"/messages/"+e.getAsJsonObject().get("id").getAsString())
				.method(Method.DELETE)
				.header("Authorization",token)
				.execute();
		if (r0.statusCode()!=429) break;
		}catch(Exception f) {break;}
		TimeUnit.MILLISECONDS.sleep(300);
	}
}
public static ArrayList<JsonObject> getMessages(String id) throws IOException, InterruptedException {
	JsonArray total = null;
	JsonParser parse = new JsonParser();
	Response r0 = Jsoup.connect("https://discordapp.com/api/channels/"+id+"/messages?amount=50")
			.header("Authorization",token)
			.ignoreContentType(true)
			.ignoreHttpErrors(true)
			.execute();
	JsonArray temp  = parse.parse(r0.body()).getAsJsonArray();
	total = parse.parse(r0.body()).getAsJsonArray();
	while(temp.size()>45) {
		r0 = Jsoup.connect("https://discordapp.com/api/channels/"+id+"/messages?amount=50&after="+temp.get(temp.size()-1).getAsJsonObject().get("id").getAsString())
				.header("Authorization",token)
				.ignoreContentType(true)
				.ignoreHttpErrors(true)
				.execute();
		TimeUnit.MILLISECONDS.sleep(500);
		temp  = parse.parse(r0.body()).getAsJsonArray();
		total.addAll(temp);
	}
	ArrayList<JsonObject> toreturn = new ArrayList<JsonObject>();
	for (JsonElement e:total) toreturn.add(e.getAsJsonObject());
	return toreturn;
}
public static void StartScript() throws InterruptedException, IOException {
AnsiConsole.systemInstall();
System.out.println(CLRS.RED+"DO NOT RESIZE CHROME!"+CLRS.RESET);
System.out.println(CLRS.BLUE+"[Starting Chrome...]"+CLRS.RESET);
java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
System.setProperty("webdriver.chrome.silentOutput", "true");
ChromeOptions co = new ChromeOptions();
co.addArguments("--window-size=1000,700");
WebDriver driver = new ChromeDriver(co);
driver.get("https://discordapp.com/channels/");
System.out.println(CLRS.RED+"STARTED CHROME SUCCESSFULLY, LOGIN, THEN PRESS ENTER ON THIS CONSOLE!."+CLRS.RESET);
in.nextLine();
JavascriptExecutor js = (JavascriptExecutor) driver;
js.executeScript(" let popup;\r\n" +
" popup = window.open('', '', `top=0,left=${screen.width-800},width=800,height=${screen.height}`);\r\n" +
" if (!popup) console.error('Popup blocked! Please allow popups and try again.');\r\n" +
"	window.dispatchEvent(new Event('beforeunload'));\r\n" +
"	token = JSON.parse(popup.localStorage.token);\r\n" +
"	authorid = JSON.parse(popup.localStorage.user_id_cache);\r\n" +
"	popup.close();\r\n" +
" progress = 0;\r\n" +
"	total = 0;\r\n" +
" console.log(\"Saved Token in var.!\");\r\n" +
" running = false;\r\n" +
" \r\n" +
" StartClearMessage = (function() {\r\n" +
" 'use strict';\r\n" +
"	running = true;\r\n" +
"\r\n" +
" const channelID = window.location.href.split('/').pop(); //Get our channel ID from the current window's URL\r\n" +
" const userID = authorid; //Get our user ID from localStorage\r\n" +
"	let messageID = \"000000000000000000\";\r\n" +
"	const authToken = token;\r\n" +
" let msgCount = 0; //Keeps track of how many messages we find\r\n" +
" const interval = 800; //The amount of time to wait in-between deleting messages (default \"safe\" value is 500)\r\n" +
"\r\n" +
" let delay = (duration) => {\r\n" +
" return new Promise((resolve, reject) => {\r\n" +
" setTimeout(() => resolve(), duration);\r\n" +
" });\r\n" +
" }\r\n" +
"\r\n" +
" let clearMessages = () => {\r\n" +
" const baseURL = \"https://discordapp.com/api/channels/\" + channelID + \"/messages\";\r\n" +
" const headers = {\r\n" +
" \"Authorization\": authToken\r\n" +
" };\r\n" +
"\r\n" +
" let clock = 0;\r\n" +
"\r\n" +
" window.fetch(baseURL + \"?before=\" + messageID, {\r\n" +
" headers,\r\n" +
" method: 'GET'\r\n" +
" }) //Fetch the message data from discord\r\n" +
" .then((resp) => resp.json()) //Make it into a json\r\n" +
" .then((messages) => { //Call that json \"messages\" and do this function with it as the parameter:\r\n" +
" if (typeof messages === \"undefined\" || !messages.hasOwnProperty('length')) {\r\n" +
" window.alert(\"Failed to retrieve messages! Try refreshing the page, then running the script again.\");\r\n" +
" return true;\r\n" +
" } else if (messages.length === 0) {\r\n" +
" running = false;\r\n" +
" return true;\r\n" +
" }\r\n" +
" return Promise.all(messages.map(\r\n" +
" (message) => { //Call this function for all messages we have\r\n" +
" messageID = message.id; //Update our message ID\r\n" +
" if (message.author.id === userID) { //Checks to see if message is yours\r\n" +
" msgCount++;\r\n" +
" const msgNumber = msgCount; //Remember the count for this message for logging purposes\r\n" +
" console.log(\"Found message #\" + msgNumber);\r\n" +
" return delay(clock += interval)\r\n" +
" .then(() => {\r\n" +
" progress = msgNumber;\r\n" +
" total = msgCount;\r\n" +
" console.log(\"Deleting message \" + msgNumber + \"/\" + msgCount);\r\n" +
" fetch(`${baseURL}/${message.id}`, {\r\n" +
" headers,\r\n" +
" method: 'DELETE'\r\n" +
" });\r\n" +
" });\r\n" +
" } else { //If the message is not yours, we skip it.\r\n" +
" console.log(\"Skipped message from other user.\");\r\n" +
" return;\r\n" +
" //Chrome's console groups up repeated logs. If this prints out 3 times, it'll say:\r\n" +
" //\"(3) Skipped message from other user\". You can add a variable to track how many\r\n" +
" //messages it skips and log the count, but beware it will spam your console log.\r\n" +
" }\r\n" +
" }));\r\n" +
" })\r\n" +
" .then((isFinished) => {\r\n" +
" if (isFinished === true) { //Check to see if we are finished deleting messages.\r\n" +
" return; //We finished deleting all our messages, so we end the process!\r\n" +
" }\r\n" +
" clearMessages(); //Once we've deleted all the messages we can see, we ask for more!\r\n" +
" });\r\n" +
" }\r\n" +
" clearMessages();\r\n" +
"});");
System.out.println(CLRS.GREEN+"Succesfully Injected Script into CHrome"+CLRS.RESET);
token = (String) js.executeScript("return token;");
userid = (String) js.executeScript("return authorid;");
System.out.println("Loaded User Token");
LoadFriends(token);
delete(driver);
}
public static void delete(WebDriver driver) throws InterruptedException, IOException {
double start = names.size();
ArrayList<String> namestaken = new ArrayList<String>();
while(names.size()>0) {
String name = names.remove(0);
if (!check(name)) {Utility.Save(name, "Skipped");System.out.println("Skipped 1 name, non unicode.");continue;}
double progress = start-names.size();

Utility.CLILoadingBar((int)((progress/start*100)), "Deleting convo with:: "+name+" ["+progress+"/"+start+"]");
Actions builder = new Actions(driver);
builder.moveToElement(driver.findElement(By.tagName("body")),-340, -220).click().sendKeys(name).pause(100).sendKeys(Keys.ENTER).sendKeys(Keys.ESCAPE).sendKeys(Keys.ESCAPE).build().perform();
while(!driver.getTitle().contains(name.split("#")[0])) TimeUnit.MILLISECONDS.sleep(100);
Delete(new DiscordUser(name,driver.getCurrentUrl().split("/")[driver.getCurrentUrl().split("/").length-1]));
}

}

static double convertDouble(Object longValue){
double valueTwo = -1; // whatever to state invalid!
if(longValue instanceof Long)
valueTwo = ((Long) longValue).doubleValue();
return valueTwo;
}
public static void LoadFriends(String Token) throws IOException {
org.jsoup.Connection.Response r0 = Jsoup.connect("https://discordapp.com/api/v6/users/@me/relationships")
.ignoreContentType(true)
.ignoreHttpErrors(true)
.header("Authorization",Token)
.execute();
JsonArray arr = new JsonParser().parse(r0.body()).getAsJsonArray();
for(JsonElement e:arr) {
JsonObject o = e.getAsJsonObject().get("user").getAsJsonObject();
String s = o.get("username").getAsString()+"#"+o.get("discriminator").getAsString();
names.add(s);
}
System.out.println("Loaded "+names.size()+" Relationsship");
}
}