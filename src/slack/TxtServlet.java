package slack;

import java.util.logging.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.net.URL;
import javax.servlet.http.*;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

@SuppressWarnings("serial")
public class TxtServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TxtServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String token = req.getParameter("token");
		String text = req.getParameter("text");
		String number = "+1"+text.substring(0, text.indexOf(" "));
		//String team_domain = req.getParameter("team_domain");
		//String channel_name = req.getParameter("channel_name");
		String user_name = req.getParameter("user_name");
		
		// ack to Slack
		resp.setContentType("text/plain");
		if (token == null || text == null) {
			resp.getWriter().println("Something went wrong. Try again later. Or don't. I don't really care.");
			return;
		}
		else
			resp.getWriter().println("Texting "+number+" now...");
		
		String message = user_name+": "+text.substring(text.indexOf(" ")+1, text.length());
		
		// post to Twilio
		final String twilio_account_sid = System.getenv("TWILIO_ACCOUNT_SID");
		final String twilio_auth_token = System.getenv("TWILIO_AUTH_TOKEN");
		final String twilio_number = System.getenv("TWILIO_NUMBER");
		
		TwilioRestClient twilio_client = new TwilioRestClient(twilio_account_sid, twilio_auth_token);
		List<NameValuePair> twilio_params = new ArrayList<NameValuePair>();
		twilio_params.add(new BasicNameValuePair("Body", message));
		twilio_params.add(new BasicNameValuePair("To", number));
		twilio_params.add(new BasicNameValuePair("From", twilio_number));
		
		try {
			MessageFactory messageFactory = twilio_client.getAccount().getMessageFactory();
			Message twilio_message = messageFactory.create(twilio_params);
			String twilo_ack = twilio_message.getSid();
		} catch (TwilioRestException exception) {
			log.info(exception.toString());
		}
	}
}