package com.example.spacepetrescue.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spacepetrescue.R;
import java.util.Locale;

public class Welcome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btnStart).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btnGuide).setOnClickListener(v -> showGuideDialog());
        findViewById(R.id.btnLang).setOnClickListener(v -> showLanguageDialog());
        findViewById(R.id.btnQuit).setOnClickListener(v -> finishAffinity());
    }

    private void showLanguageDialog() {
        String[] langs = {"English \uD83C\uDDEC\uD83C\uDDE7", "Tiếng Việt \uD83C\uDDFB\uD83C\uDDF3", "Suomi \uD83C\uDDEB\uD83C\uDDEE"};
        new AlertDialog.Builder(this)
                .setTitle(R.string.change_lang)
                .setItems(langs, (d, which) -> {
                    if (which == 0) setLocale("en");
                    else if (which == 1) setLocale("vi");
                    else setLocale("fi");
                }).show();
    }

    private void setLocale(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        com.example.spacepetrescue.util.LocaleHelper.setLocale(this, code);
        recreate();
    }

    private void showGuideDialog() {
        View view = getLayoutInflater().inflate(R.layout.guideshowing, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView)view.findViewById(R.id.tvGuideTitle)).setText(R.string.guide_title);
        ((TextView)view.findViewById(R.id.tvGuideContent)).setText(R.string.guide_content);
        view.findViewById(R.id.btnCloseGuide).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}