package com.iliessnp.fetchdata;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//QR code
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editTextsenderid;
    Button buttonfetch;
    ListView listview;
    String sender_id;
    ProgressDialog mProgressDialog;

    String f_name  ;
    String l_name  ;
    String phone  ;
    //QR code
    EditText etInput;
    Button btnGen;
    ImageView ivOutput;

    public static final String KEY_SENDERID = "sender_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextsenderid = (EditText)findViewById(R.id.et_snederid);
        buttonfetch = (Button)findViewById(R.id.btnfetch);
        listview = (ListView)findViewById(R.id.listView);

        buttonfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender_id = editTextsenderid.getText().toString().trim();
                if (sender_id.equals("")){
                    Toast.makeText(MainActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
                }else {
                    GetMatchData();
                }
            }
        });

        //QR code
        etInput =findViewById(R.id.et_input);
        btnGen = findViewById(R.id.btn_generate);
        ivOutput = findViewById(R.id.iv_output);

        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sText = f_name + "\n" + l_name + "\n" + phone ;

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix =writer.encode(sText, BarcodeFormat.QR_CODE, 350 , 350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    ivOutput.setImageBitmap(bitmap);
                    InputMethodManager manager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    manager.hideSoftInputFromWindow(etInput.getApplicationWindowToken(),0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GetMatchData() {

        sender_id = editTextsenderid.getText().toString().trim();

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getString(R.string.progress_detail));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config5.MATCHDATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            showJSON(response);
                            mProgressDialog.dismiss();
                        } else {
                            showJSON(response);
                            mProgressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_SENDERID, sender_id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config5.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                  f_name = jo.getString(Config5.KEY_FNAME);
                  l_name = jo.getString(Config5.KEY_LNAME);
                  phone = jo.getString(Config5.KEY_PHONE);

                final HashMap<String, String> employees = new HashMap<>();
                employees.put(Config5.KEY_FNAME, "f_name = " +f_name);
                employees.put(Config5.KEY_LNAME, "l_name = " +l_name);
                employees.put(Config5.KEY_PHONE, "phone = " +phone);

                list.add(employees);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, list, R.layout.list_item,
                new String[]{Config5.KEY_FNAME, Config5.KEY_LNAME, Config5.KEY_PHONE},
                new int[]{R.id.tv_fname, R.id.tv_lname, R.id.tv_phone});

        listview.setAdapter(adapter);
    }


}