package client;


import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Driver {

	public static void main(String[] args) {

		System.out.println("SimpleEmail Start");

		String smtpHostServer = "smtp.gmail.com";
		int port = 587;
		String host = "smtp.gmail.com";
		String user = "markaustin2k@gmail.com";
		String pwd = "Pytho123!";
		Properties props = new Properties();
		try {
			// required for gmail
			props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", smtpHostServer);
			Session session = Session.getInstance(props, null);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, port, user, pwd);
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress("markaustin2k@gmail.com", "NoReply-JD"));

			msg.setReplyTo(InternetAddress.parse("markaustin2k@gmail.com", false));

			msg.setSubject("SimpleEmail Testing Subject", "UTF-8");

			msg.setText("SimpleEmail Testing Body", "UTF-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("markaustin2k@gmail.com", false));
			transport.sendMessage(msg, InternetAddress.parse("markaustin2k@gmail.com", false));
			transport.close();
			System.out.println("success");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
