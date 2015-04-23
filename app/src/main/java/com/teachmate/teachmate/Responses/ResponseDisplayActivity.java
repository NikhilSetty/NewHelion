
package com.teachmate.teachmate.Responses;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.DBHandlers.RequestsDBHandler;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.Requests;
import com.teachmate.teachmate.models.Responses;
import com.teachmate.teachmate.models.UserModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseDisplayActivity extends Fragment {

    Responses currentResponse;
    Requests  currentRequest;

    Button    acceptResponse;

    String    notificationRequestId;

    TextView  requestString;

    TextView  responseUserName;
    TextView  responseUserProfession;
    TextView  responseString;

    ImageView profilePhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_response_display, container, false);

        acceptResponse = (Button) layout.findViewById(R.id.buttonAccept);

        profilePhoto = (ImageView) layout.findViewById(R.id.imageViewUserProfilePhoto);

        requestString = (TextView) layout.findViewById(R.id.textViewResponseRequestString);
        responseUserName = (TextView) layout.findViewById(R.id.textViewResponseDisplayUserName);
        responseUserProfession = (TextView) layout
                .findViewById(R.id.textViewResponseDisplayUserProfession);
        responseString = (TextView) layout.findViewById(R.id.textViewResponseDisplayString);

        currentResponse = new Responses();
        Bundle args = getArguments();

        try {
            notificationRequestId = args.getString("NotificationResponseId");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        if (notificationRequestId == null) {
            currentRequest = new Requests();

            currentRequest.RequestID = args.getString("RequestId");
            currentRequest.RequestString = args.getString("RequestString");
            currentRequest.RequestTime = args.getString("RequestTime");

            currentResponse.RequestId = args.getString("RequestId");
            currentResponse.ResponseId = args.getString("ResponseId");
            currentResponse.ResponseString = args.getString("ResponseString");
            currentResponse.ResponseTime = args.getString("ResponseTime");
            currentResponse.ResponseUserId = args.getString("ResponseUserId");
            currentResponse.ResponseUserName = args.getString("ResponseUserName");
            currentResponse.ResponseUserProfession = args.getString("ResponseUserProfession");
            currentResponse.ResponseUserProfilePhotoServerPath = args
                    .getString("ResponseUserProfilePhotoServerPath");

            requestString.setText(currentRequest.RequestString);
            responseUserName.setText(currentResponse.ResponseUserName);
            responseUserProfession.setText(currentResponse.ResponseUserProfession);
            responseString.setText(currentResponse.ResponseString);
        }
        else {
            UserModel user = UserModelDBHandler.ReturnValue(getActivity().getApplicationContext());
            TempDataClass.serverUserId = user.ServerUserId;

            currentResponse.ResponseId = args.getString("NotificationResponseId");
            currentResponse.RequestId = args.getString("NotificationRequestId");
            currentResponse.ResponseUserId = args.getString("NotificationResponseUserId");
            currentResponse.ResponseUserName = args.getString("NotificationResponseUserName");
            currentResponse.ResponseString = args.getString("NotificationResponseMessage");
            currentResponse.ResponseUserProfession = args
                    .getString("NotificationResponseUserProfession");
            currentResponse.ResponseUserProfilePhotoServerPath = args
                    .getString("NotificationResponseUserProfilePhotoServerPath");

            currentRequest = RequestsDBHandler.GetRequest(getActivity().getApplicationContext(),
                    currentResponse.RequestId);

            requestString.setText(currentRequest.RequestString);
            responseUserName.setText(currentResponse.ResponseUserName);
            responseUserProfession.setText(currentResponse.ResponseUserProfession);
            responseString.setText(currentResponse.ResponseString);
        }

        if(!currentResponse.ResponseUserProfilePhotoServerPath.isEmpty() && currentResponse.ResponseUserProfilePhotoServerPath != null){
            Picasso.with(activity.getApplicationContext()).load(currentResponse.ResponseUserProfilePhotoServerPath).into(profilePhoto);
        }

        acceptResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        return layout;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String POST(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("SenderId", TempDataClass.serverUserId);
            jsonObject.put("ReceiverId", currentResponse.ResponseUserId);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";

            return result;

        } catch (Exception e) {
            Log.v("Getter", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class GetChatId extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
