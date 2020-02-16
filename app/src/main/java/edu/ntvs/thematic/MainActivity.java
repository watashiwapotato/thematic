package edu.ntvs.thematic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public String url;
    WebView webView;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webview);
        webSettings = webView.getSettings();

        url = getSharedPreferences("Url", MODE_PRIVATE).getString("url", "http://");
        if (url == "http://") {
            setUrl();
        } else {
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);
        }

        ImageView refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });

        ImageView setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUrl();
            }
        });
    }

    public void setUrl() {
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.edittext, null);
        final EditText edit = (EditText) (view.findViewById(R.id.edit));
        edit.setText(url);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("設定網址")
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        url = edit.getText().toString();
                        SharedPreferences pref = getSharedPreferences("Url", MODE_PRIVATE);
                        pref.edit()
                                .putString("url", url)
                                .commit();
                        webView.setWebViewClient(new WebViewClient());
                        webView.loadUrl(url);
                    }
                })
                .show();
    }

}
