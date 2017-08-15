package com.example.josh.ubchi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.josh.ubchi.DisplayMessageActivity;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";
        String messagebody = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i=0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                messagebody = msgs[i].getMessageBody().toString();
                str += "\n";
            }

            // Display the entire SMS Message
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            DisplayMessageActivity.getInstance().updateTheTextView(messagebody);
        }
    }

    public static String decipher(StringBuilder buffer, int [] keyperm, int n, int k) {
        // do something - simple columnar transposition
        StringBuilder temp = new StringBuilder();
        // main de-transposition loop
        int m = 0;
        for (int j = 0; j < k; j++)
            for (int i = keyperm[j]; i < n; i += k)
                temp.append(buffer.charAt(i));

        return(temp.toString().toUpperCase());
    }
}
