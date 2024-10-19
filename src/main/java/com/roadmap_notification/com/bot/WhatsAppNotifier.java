package com.roadmap_notification.com.bot;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppNotifier {
    // Your Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "YOUR TWILIO ACCOUNT SID";
    public static final String AUTH_TOKEN = "YOUR TWILIO AUTH TOKEN";
    
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
    
    public static void sendWhatsAppMessage(String to, String messageContent) {
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + to),  // To WhatsApp number
                new PhoneNumber("whatsapp:+14155238886"), // Your Twilio WhatsApp Number
                messageContent
        ).create();
        
        System.out.println("Message sent: " + message.getSid());
    }
}

