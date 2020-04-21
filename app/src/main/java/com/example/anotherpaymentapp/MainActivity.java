package com.example.anotherpaymentapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText amount;
    EditText cardname;
    EditText upiid;
    EditText note;
    Button Pay;
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializedmethod();
        Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // card details inpute
                String amountText = amount.getText().toString();
                String cardnameText = cardname.getText().toString();
                String upiidText = upiid.getText().toString();
                String noteText = note.getText().toString();
                PayusintUpi(amountText, cardnameText, upiidText, noteText);
            }
        });
    }

    private void PayusintUpi(String amountText, String cardnameText, String upiidText, String noteText) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiidText)
                .appendQueryParameter("am", amountText)
                .appendQueryParameter("pa", cardnameText)
                .appendQueryParameter("tn", noteText)
                .appendQueryParameter("cu", "INR").build();
        Intent upi_payment = new Intent(Intent.ACTION_VIEW);
        upi_payment.setData(uri);
        Intent chosser = Intent.createChooser(upi_payment, "pay with");
        if (null != chosser.resolveActivity(getPackageManager())) {
            startActivityForResult(chosser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, "NO Upi App fount", Toast.LENGTH_SHORT).show();


        }
    }

    private void initializedmethod() {
        Pay = (Button) findViewById(R.id.but_Pay);
        amount = (EditText) findViewById(R.id.edt_Amount);
        cardname = (EditText) findViewById(R.id.edt_card_name);
        upiid = (EditText) findViewById(R.id.edt_UPI);
        note = (EditText) findViewById(R.id.edt_note);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {

                    if (data != null) {
                        String txt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + txt);
                        ArrayList<String> datalst = new ArrayList<>();
                        datalst.add("nothing");
                        upidataoperation(datalst);

                    } else {
                        Log.d("UPI", "onActivityResult" + "Return data is null");
                        ArrayList<String> datalst = new ArrayList<>();
                        datalst.add("nothing");
                        upidataoperation(datalst);
                    }
                    break;
                }

        }
    }

    private void upidataoperation(ArrayList<String> data) {
        if (isConnectionAveliable(MainActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "UpiPaymentOperation" + str);

            String PaymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalref = "";
            String response[] = str.split("&");

            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("approval Ref".toLowerCase())
                            || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalref = equalStr[1];
                    }
                } else {
                    // paymentCancel is a function not method to use
                    PaymentCancel = "payment canceled by user";
                }
            }
            if (status.equals("Success")) {
                Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "resposns" + approvalref);

            } else if ("payment canceled by user".equals(PaymentCancel)) {
                Toast.makeText(this, "payment canceled by user", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
            }

            }else{
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isConnectionAveliable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() &&
                    networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}

