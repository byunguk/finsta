package com.homework.finsta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.homework.finsta.R;
import com.homework.finsta.util.Const;
import com.homework.finsta.util.RestAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbw815 on 7/25/15.
 */
public class WebViewActivity extends AppCompatActivity {
    private final String MODE_SIGN_IN = "sign_in";
    private final String MODE_SIGN_OUT = "sign_out";

    private WebView mWebView;
    private String mUrl;
    private String mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mUrl = getIntent().getStringExtra(Const.FIELD_URL);
        if (!TextUtils.isEmpty(mUrl))
        {
            mMode = MODE_SIGN_OUT;
        }
        else
        {
            mMode = MODE_SIGN_IN;
        }
        bindUIElements();
        setUpWebView();
        setUpListeners();
    }

    private void bindUIElements()
    {
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(new FinstaWebViewClient());
        if (TextUtils.isEmpty(mUrl))
            mWebView.loadUrl(Const.URL_AUTHORIZATION);
        else
            mWebView.loadUrl(mUrl);
    }

    private void setUpWebView()
    {

    }

    private void setUpListeners()
    {

    }

    private class FinstaWebViewClient extends WebViewClient {
        private ProgressDialog progressDialog;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(Const.REDIRECT_URI))
            {
                String[] paths = url.split("code=");
                AccessTokenAsyncTask task = new AccessTokenAsyncTask(paths[1]);
                task.execute();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                return true;
            }
            else if (url.equals(Const.BASE_URL2))
            {
                view.loadUrl(Const.URL_AUTHORIZATION);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressDialog == null)
            {
                String msg = MODE_SIGN_OUT.equals(mMode) ? "Signing out..." : "Loading...";
                progressDialog = ProgressDialog.show(WebViewActivity.this, "", msg, true);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

    }

    private class AccessTokenAsyncTask extends AsyncTask<Void, Void, String> {
        private String code;

        public AccessTokenAsyncTask(String code)
        {
            this.code = code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                List<RestAgent.Parameter> parameters = new ArrayList<>();
                parameters.add(new RestAgent.Parameter(Const.FIELD_CLIENT_ID, Const.CLIENT_ID));
                parameters.add(new RestAgent.Parameter(Const.FIELD_CLIENT_SECRET, Const.CLIENT_SECRET));
                parameters.add(new RestAgent.Parameter(Const.FIELD_GRANT_TYPE, Const.AUTHORIZATION_CODE));
                parameters.add(new RestAgent.Parameter(Const.FIELD_REDIRECT_URI, Const.REDIRECT_URI));
                parameters.add(new RestAgent.Parameter(Const.FIELD_CODE, code));
                RestAgent agent = new RestAgent(Const.OAUTH_ACCESS_TOKEN_URL, RestAgent.POST, parameters);
                String result = agent.send();
                return result;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result))
            {
                Toast.makeText(WebViewActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try
                {
                    JSONObject resultJSONObject = new JSONObject(result);
                    String accessToken = resultJSONObject.optString(Const.FIELD_ACCESS_TOKEN);

                    Intent intent = new Intent(WebViewActivity.this, FeedActivity.class);
                    intent.putExtra(Const.FIELD_ACCESS_TOKEN, accessToken);
                    startActivity(intent);
                }
                catch (JSONException ex)
                {
                    ex.printStackTrace();
                }

            }
        }
    }
}
