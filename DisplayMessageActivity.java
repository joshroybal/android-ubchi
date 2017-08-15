package com.example.josh.ubchi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DisplayMessageActivity extends AppCompatActivity {

    private static DisplayMessageActivity ins;

    public static DisplayMessageActivity  getInstance(){
        return ins;
    }

    public void updateTheTextView(final String receivedMSG) {
        DisplayMessageActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                // get the stuff we need from the activity layout
                EditText msgTxt = (EditText) findViewById(R.id.messageArea);
                EditText keyTxt = (EditText) findViewById(R.id.keyword);
                // now that we have it use it to get the keyword and ?
                // msgTxt.setText(receivedMSG);

                String keyword = keyTxt.getText().toString().toLowerCase();
                StringBuilder receiverBUF = new StringBuilder(receivedMSG);
                int n  = receivedMSG.length();
                int k = keyword.length();
                int[] keyperm = new int[k];
                makeKeyPerm(keyword, keyperm, k);
                String plaintext = decipher(receiverBUF, keyperm, n, k);
                msgTxt.setText(plaintext);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // receive SMS text message instatiation instance or whateve
        ins = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string - debugging
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // define/instatiate our screen elements
        // final EditText msgTxt = (EditText) findViewById(R.id.messageArea);
        final EditText msgTxt = (EditText) findViewById(R.id.messageArea);
        final EditText keyTxt = (EditText) findViewById(R.id.keyword);
        final EditText phoneTxt = (EditText) findViewById(R.id.phoneNo);

        // assign the keyword
        // final String keyword = keyTxt.toString();

        // the exchange number should be the last twelve characters
        int startpos = message.length() - 13;
        String phonenoStr = message.substring(startpos, message.length());
        phoneTxt.setText(phonenoStr);

        // try to send SMS text message by button listeeer
        Button sendMessage = (Button) findViewById(R.id.send);
        sendMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String plaintext = msgTxt.getText().toString().toLowerCase();
                String keyword = keyTxt.getText().toString().toLowerCase();

                // *********************************************************************************
                // begin debugging section
                // debug test simple caesar cipher - to be replaced eventually with a keyword
                // generated transpsosition cipher
                // int count = 0;
                StringBuilder ctb = new StringBuilder();

                // strip non-alphabetic characters
                for (int i = 0; i < plaintext.length(); i++) {
                    char ch = plaintext.charAt(i);
                    if (ch >= 'a' && ch <= 'z') ctb.append(ch);
                }
                // end debugging section
                // *********************************************************************************

                // main working block
                int n = ctb.length();
                // String keyword = keyTxt.toString();
                int k = keyword.length();
                int[] keyperm = new int[k];
                makeKeyPerm(keyword, keyperm, k);
                String ciphertext = encipher(ctb, keyperm, n, k);
                String number = phoneTxt.getText().toString();
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(number, null, ciphertext, null, null);
            }
        });



    }

    // create permulation array for scrambling
    public static void makeKeyPerm(String keyword, int [] keyperm, int n) {
        // create array containing alphabetic positions of characters in keyword
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = ((int) keyword.charAt(i)) - 97;

        int k = 0;
        for (int i = 0; i < 26 && k < n; i++) {
            for (int j = 0; j < n && k < n; j++) {
                if (arr[j] == i) keyperm[k++] = j;
            }
        }
    }

    // main scrambling function
    public static String encipher(StringBuilder buffer, int [] keyperm, int n, int k) {
        // do something - simple columnar transposition
        StringBuilder temp = new StringBuilder();
        // main transposition loop
        for (int j = 0; j < k; j++)
            for (int i = keyperm[j]; i < n; i += k)
                temp.append(buffer.charAt(i));
        // repeat above and we have double transposition aka unbchi
        buffer.setLength(0);
        for (int j = 0; j < k; j++)
            for (int i = keyperm[j]; i < n; i += k)
                buffer.append(temp.charAt(i));
        // return the doubly transposed by keyword generated transposition string
        return(buffer.toString().toUpperCase());
    }

    public static String decipher(StringBuilder buffer, int [] keyperm, int n, int k) {
        // do something - simple columnar transposition
        // StringBuilder temp = new StringBuilder(buffer);
        StringBuilder temp = new StringBuilder(buffer);
        // main transposition loop
        int m = 0;
        for (int j = 0; j < k; j++)
            for (int i = keyperm[j]; i < n; i += k)
                temp.setCharAt(i, buffer.charAt(m++));
        // one more de-transposition pass and we shall have back our plaintext
        m = 0;
        for (int j = 0; j < k; j++)
            for (int i = keyperm[j]; i < n; i +=k)
                buffer.setCharAt(i, temp.charAt(m++));
        // return the doubly keyword determined permutation detransposition of the ciphertext
        // as the plaintext
        return(buffer.toString().toLowerCase());
    }

}
