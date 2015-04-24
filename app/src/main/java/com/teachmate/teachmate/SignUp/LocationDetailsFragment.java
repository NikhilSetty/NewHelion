package com.teachmate.teachmate.SignUp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teachmate.teachmate.Base64;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.UserModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public class LocationDetailsFragment extends Fragment implements onNextPressed{

    UserModel userData;

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextAddress;
    EditText editTextPinCode;

    String _editTextName = "";
    String _editTextEmail = "";
    String _editTextAddress = "";
    String _editTextPinCode = "";

    String pinCode1Status = "";
    String pinCode2Status = "";

    boolean isPinCode1Tested = false;
    boolean isPinCode2Tested = false;

    float lattitude1, longitude1, lattitude2, longitude2;

    boolean isPinCode1Verified = false;
    boolean isPinCode2Verified = false;

    String pinCode1HttpStatus = "unknown";
    String pinCode2HttpStatus = "unknown";

    InputStream inputStream;

    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_location_details, container, false);

        userData = new UserModel();

        editTextName = (EditText) layout.findViewById(R.id.editTextHelioName);
        editTextEmail = (EditText) layout.findViewById(R.id.editTextHelioEmail);
        editTextAddress = (EditText) layout.findViewById(R.id.editTextAddress);
        editTextPinCode = (EditText) layout.findViewById(R.id.editTextPinCode);

        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((NewSignUpActicity) getActivity()).setInterface(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NewSignUpActicity) getActivity()).setInterface(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void saveCurrentData() {
        ((NewSignUpActicity) getActivity()).showProgressDialog();

        _editTextName = editTextName.getText().toString();
        if(_editTextName.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getActivity().getApplicationContext(), "Please enter " + editTextName.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextEmail = editTextEmail.getText().toString();
        if(_editTextEmail.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getActivity().getApplicationContext(), "Please enter " + editTextEmail.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextAddress = editTextAddress.getText().toString();
        if(_editTextAddress.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextAddress.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getActivity().getApplicationContext(), "Please enter " + editTextAddress.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPinCode = editTextPinCode.getText().toString();
        if(_editTextPinCode.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextPinCode.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getActivity().getApplicationContext(), "Please enter " + editTextPinCode.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        NewSignUpActicity.userModel.FirstName = _editTextName;
        NewSignUpActicity.userModel.EmailId = _editTextEmail;
        NewSignUpActicity.userModel.Address1 = _editTextAddress;
        NewSignUpActicity.userModel.PinCode1 = _editTextPinCode;

        HttpGetterPinCode1Handler getter1 = new HttpGetterPinCode1Handler();
        getter1.execute("http://maps.google.com/maps/api/geocode/xml?address='" + _editTextPinCode + "'&sensor=false");

        SignUpUsersIfPinCodesAreVerified signUpProcess = new SignUpUsersIfPinCodesAreVerified();
        signUpProcess.execute();
    }

    private class HttpGetterPinCode1Handler extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String line = "";

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    Log.v("Getter", "Your data: " + builder.toString()); //response data
                } else {
                    Log.e("Getter", "Failed to get data");
                }
            } catch (ClientProtocolException e) {
                pinCode1HttpStatus = "error";
                e.printStackTrace();
            } catch (IOException e) {
                pinCode1HttpStatus = "error";
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            pinCode1HttpStatus = "OK";
            pinCode1Status = getStatusPinCode1FromXml(result);
            isPinCode1Tested = true;
            if(pinCode1Status.equals("OK")) {
                isPinCode1Verified = true;
            }
            Toast.makeText(getActivity().getApplicationContext(), pinCode1Status, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusPinCode1FromXml(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(result));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("status")) {
                            pinCode1Status = parser.nextText();
                            if(pinCode1Status.equals("ZERO_RESULTS")){
                                return pinCode1Status;
                            }
                        }
                        else if (name.equalsIgnoreCase("location")){
                            int subEvent = parser.nextTag();
                            if(subEvent == XmlPullParser.START_TAG){
                                String subName = parser.getName();
                                if(subName.equals("lat")){
                                    lattitude1 = Float.parseFloat(parser.nextText().toString());
                                }
                                subEvent = parser.nextTag();
                                if(subEvent == XmlPullParser.START_TAG){
                                    subName = parser.getName();
                                    if(subName.equals("lng")){
                                        longitude1 = Float.parseFloat(parser.nextText().toString());
                                    }
                                }

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PinCode", ex.getMessage());
        }


        return pinCode1Status;
    }

    private class HttpGetterPinCode2Handler extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String line = "";

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    Log.v("Getter", "Your data: " + builder.toString()); //response data
                } else {
                    Log.e("Getter", "Failed to get data");
                }
            } catch (ClientProtocolException e) {
                pinCode2HttpStatus = "error";
                e.printStackTrace();
            } catch (IOException e) {
                pinCode2HttpStatus = "error";
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            pinCode2HttpStatus = "OK";
            pinCode2Status = getStatusPinCode2FromXml(result);
            isPinCode2Tested = true;
            if(pinCode2Status.equals("OK")){
                isPinCode2Verified = true;
            }
            Toast.makeText(getActivity().getApplicationContext(), "p2:" + pinCode2Status, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusPinCode2FromXml(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(result));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("status")) {
                            pinCode2Status = parser.nextText();
                            if(pinCode2Status.equals("ZERO_RESULTS")){
                                return pinCode2Status;
                            }
                        }
                        else if (name.equalsIgnoreCase("location")){
                            int subEvent = parser.nextTag();
                            if(subEvent == XmlPullParser.START_TAG){
                                String subName = parser.getName();
                                if(subName.equals("lat")){
                                    lattitude2 = Float.parseFloat(parser.nextText().toString());
                                }
                                subEvent = parser.nextTag();
                                if(subEvent == XmlPullParser.START_TAG){
                                    subName = parser.getName();
                                    if(subName.equals("lng")){
                                        longitude2 = Float.parseFloat(parser.nextText().toString());
                                    }
                                }

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            //.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PinCode", ex.getMessage());
        }


        return pinCode2Status;
    }

    private class SignUpUsersIfPinCodesAreVerified extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            while(!(isPinCode1Verified)){
                if(isPinCode1Tested) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            if (pinCode1HttpStatus.equals("error") || pinCode1Status.equals("ZERO_RESULTS")) {
                Toast.makeText(getActivity().getApplicationContext(), "Pin Code 1 not Valid.", Toast.LENGTH_SHORT).show();
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                return;
            }

            HttpSignUpAsyncTask signUpUser = new HttpSignUpAsyncTask();
            signUpUser.execute("http://helion-helloworld-java.gids.cf.helion-dev.com/AddUser");

            return;
        }
    }

    private class HttpSignUpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POSTSignUpDetails(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            Toast.makeText(getActivity().getApplicationContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();

            if(result != null){
                if(!(result.isEmpty() || result.contains("ERROR"))){
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                    TempDataClass.userName = NewSignUpActicity.userModel.FirstName;
                    TempDataClass.serverUserId = result;

                    NewSignUpActicity.userModel.ServerUserId = result;
                    userData.ServerUserId = result;

                    UserModelDBHandler.InsertProfile(getActivity().getApplicationContext(), NewSignUpActicity.userModel);

                    HttpPostRegIdToServer regIdPost = new HttpPostRegIdToServer();
                    regIdPost.execute("http://helion-helloworld-java.gids.cf.helion-dev.com/updateregid");
                }
                else{
                    ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                    Toast.makeText(getActivity().getApplicationContext(), "Registration Failed. Please try Again.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Registration Failed. Please try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String POSTSignUpDetails(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserName", NewSignUpActicity.userModel.FirstName);
            jsonObject.put("EmailId", NewSignUpActicity.userModel.EmailId);
            jsonObject.put("Address1", _editTextAddress);
            jsonObject.put("PinCode1", _editTextPinCode);
            jsonObject.put("Password", "1234");
            jsonObject.put("Latitude1", lattitude1);
            jsonObject.put("Longitude1", longitude1);
            json = jsonObject.toString();
            Log.i("GETTER", json.toString());
            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");


            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            return result;

        } catch (Exception e) {
            Log.v("Getter", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class HttpPostRegIdToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POSTRegID(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                Toast.makeText(getActivity().getApplicationContext(), "Registration Successfull.", Toast.LENGTH_SHORT).show();

                ((NewSignUpActicity) getActivity()).startMainActivity();
            }
        }
    }

    public String POSTRegID(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", NewSignUpActicity.userModel.ServerUserId);
            jsonObject.put("RegistrationId", TempDataClass.deviceRegId);
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");


            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            return result;

        } catch (Exception e) {
            Log.v("Getter", e.getLocalizedMessage());
        }

        return result;
    }

    public void UploadImage(String image_location){

        Bitmap bitmap = BitmapFactory.decodeFile(image_location);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        final byte [] byte_arr = stream.toByteArray();
        final String image_str = Base64.encodeBytes(byte_arr);


        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    JSONObject json=new JSONObject();
                    json.put("UserID", TempDataClass.serverUserId);
                    json.put("ImageArray",image_str);
                    String myjson="";

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://teach-mate.azurewebsites.net/User/UploadImage");
                    myjson=json.toString();
                    StringEntity se = new StringEntity(myjson);
                    Log.e("Upload", myjson);
                    httppost.setEntity(se);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity _response = response.getEntity(); // content will be consume only once
                    final  String the_string_response = convertResponseToString(_response);
                    Log.e("Upload", the_string_response);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Response " + the_string_response, Toast.LENGTH_LONG).show();
                        }
                    });

                }catch(Exception e){
                   /* runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });*/
                    Log.e("Upload Image","Error in http connection "+e.toString());
                    //Toast.makeText(getApplicationContext(), "" +e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
        t.start();
    }

    public String convertResponseToString(HttpEntity response) throws IllegalStateException, IOException{

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getContent();
        final int contentLength = (int) response.getContentLength(); //getting content length…..
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
                Log.d("Upload", "Image Upload successful");
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..
/*
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(UploadImage.this, "Result : " + res, Toast.LENGTH_LONG).show();
                }
            });*/
            System.out.println("Response => " +  EntityUtils.toString(response));
        }
        return res;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
