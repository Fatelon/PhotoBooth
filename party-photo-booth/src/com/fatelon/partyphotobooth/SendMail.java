package com.fatelon.partyphotobooth;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fatelon.partyphotobooth.helpers.PreferencesHelper;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by User on 12.09.2016.
 */
public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;

    private Session session;

    private PreferencesHelper mPreferencesHelper = new PreferencesHelper();

    //Information to send email
    private MailObject mailObject = new MailObject();

    //Progressdialog to show while sending email
//    private ProgressDialog progressDialog;

    //Class Constructor
    public SendMail(Context context, MailObject mailObject){
        //Initializing variables
        this.context = context;
        this.mailObject = mailObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        //progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();


        props.put("mail.smtp.user", mailObject.getHostAddress());
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");


        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailObject.getHostAddress(), mailObject.getHostPassword());
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);
            //Setting sender address
            mm.setFrom(new InternetAddress(mailObject.getHostAddress()));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mailObject.getUserAddress()));
            //Adding subject
            mm.setSubject(mailObject.getSubject());
            //Adding message
//            mm.setText(mailObject.getMessage());
            //Adding fileName
            try {
                MimeMultipart multipart = new MimeMultipart("related");
                BodyPart messageBodyPart = new MimeBodyPart();
                String htmlText = "<H1>" + mailObject.getMessage() + "</H1><img src=\"cid:image\">";
                messageBodyPart.setContent(htmlText, "text/html");
                // add it
                multipart.addBodyPart(messageBodyPart);

                // second part (the image)
                messageBodyPart = new MimeBodyPart();
                if (!mailObject.getFilePath().equals("")) {
                    DataSource fds = new FileDataSource(mailObject.getFilePath());
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                }
                messageBodyPart.setHeader("Content-ID", "<image>");
                // add image to the multipart
                multipart.addBodyPart(messageBodyPart);
                // put everything together
                mm.setContent(multipart);
            } catch (Exception e) {
                Log.i("sendObject", e.toString());
                e.printStackTrace();
            }
            //Sending email
            Transport.send(mm);
            Log.i("sendObject", mailObject.getHostAddress() + " " + mailObject.getHostPassword()
                    + " " + mailObject.getUserAddress() + " " + mailObject.getFilePath()
                    + " " + mailObject.getSubject() + " " + mailObject.getMessage());
        } catch (MessagingException e) {
            String s = e.toString();
            Log.i("sendObject", s);
            if (!mailObject.getUserAddress().equals("") && !mailObject.getHostAddress().equals("")
                    && e.toString().contains("Could not connect to SMTP host")) {
                MyBDHelper myBDHelper = MyBDHelper.getInstance(context);
                myBDHelper.addMailObject(mailObject);
                context.stopService(new Intent(context, SendMailService.class));
                context.startService(new Intent(context, SendMailService.class));
                Log.i("sendObject", "Could not connect to SMTP host");
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
//        progressDialog.dismiss();
        //Showing a success message
//        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
    }

}
