package com.santosh.findme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaytmCheckSum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String customer_id,order_id,transaction_amount;
    final String TAG = "paytmchecksum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //initorder_id();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = getIntent();
        try {
            order_id = intent.getExtras().getString("order_id");
            customer_id = intent.getExtras().getString("customer_id");
            transaction_amount = intent.getExtras().getString("txn_amount");
        }catch (NullPointerException e){
            //
        }
        sendUserDetailsToServer dl = new sendUserDetailsToServer();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // volley , retrofit, async
    }

    public class sendUserDetailsToServer extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(PaytmCheckSum.this);
        //private String order_id , mid, customer_id, amt;
        String url = "https://unpedigreed-claps.000webhostapp.com/Paytm/generateChecksum.php";
        String verify_url = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+order_id;
        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(PaytmCheckSum.this);
            String param =  "MID=" + "cXSFlM97244649024451" +
                            "&ORDER_ID=" + order_id +
                            "&CUST_ID=" + customer_id +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+transaction_amount+"&WEBSITE=WEBSTAGING" +
                            "&CALLBACK_URL=" + verify_url + "&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>", jsonObject.toString());
            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e("CheckSum result >>", CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", "cXSFlM97244649024451");
            paramMap.put("ORDER_ID", order_id);
            paramMap.put("CUST_ID", customer_id);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", transaction_amount);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL", verify_url);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(PaytmCheckSum.this, true, true,
                    PaytmCheckSum.this);
        }
    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e(TAG, "Response true " + bundle.toString());
        Intent intent = new Intent(getApplicationContext(),PaymentSplashActivity.class);
        intent.putExtra("parent_activity","PaytmActivity");
        intent.putExtra("status",bundle.getString("RESPCODE"));
        startActivity(intent);
        PaytmCheckSum.this.finish();
    }

    @Override
    public void networkNotAvailable() {
        Log.d(TAG, "Network not available");
        failed();
    }

    private void failed() {
        Intent intent = new Intent(getApplicationContext(),PaymentSplashActivity.class);
        intent.putExtra("parent_activity","PaytmActivity");
        intent.putExtra("status","31");
        startActivity(intent);
        PaytmCheckSum.this.finish();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        failed();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e(TAG, " UI failed respond" + s);
        failed();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e(TAG, "Error loading web page true " + s + "  s1 " + s1);
        failed();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e(TAG, "Back Pressed : Cancel Transaction");
        failed();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e(TAG, "Transaction cancel");
        failed();
    }
}