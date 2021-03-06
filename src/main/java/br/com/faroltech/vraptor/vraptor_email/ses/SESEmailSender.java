package br.com.faroltech.vraptor.vraptor_email.ses;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import br.com.faroltech.vraptor.vraptor_email.interfaces.EmailSender;

@RequestScoped
@SES
@Default
public class SESEmailSender implements EmailSender {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * This method defines the regions of the SES client's service calls. Callers can use this method to control which AWS region they want to work with.
	 *  
	 * @return the AWS Region
	 */
	public Regions getRegion() {
		log.debug("Using default region (US_EAST_1)");
		return Regions.US_EAST_1;
	}

	public void sendMail(String fromEmailString, String toEmailString, String replyEmailString, String subjectString, String bodyString, boolean isHtml) throws Exception {
		// Construct an object to contain the recipient address.
		Destination destination = new Destination().withToAddresses(new String[] { toEmailString });

		// Create the subject and body of the message.
		Content subject = new Content().withData(subjectString);
		Content textBody = new Content().withData(bodyString);
		
		Body body = new Body();
		if (isHtml) {
			body.setHtml(textBody);
		} else {
			body.setText(textBody);
		}

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(fromEmailString).withDestination(destination)
				.withMessage(message);

		try {
			log.info("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

			Region REGION = Region.getRegion(getRegion());
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			log.info("Email sent!");
		} catch (Exception ex) {
			log.error("The email was not sent.", ex);
			throw ex;
		}
	}

}
