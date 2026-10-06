package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectMenuActivity extends AppCompatActivity {
    TextView sourcecodegithublink;
    Button optika, mechanic, electronic;
    Button fourththeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_menu);
        optika = findViewById(R.id.buttonoptika);
        mechanic = findViewById(R.id.buttonmechanic);
        electronic = findViewById(R.id.buttonelectronic);
        fourththeme = findViewById(R.id.buttongraphs);
        sourcecodegithublink = findViewById(R.id.sourcecodegithublink);
        sourcecodegithublink.setText(Html.fromHtml("<a href=\"https://github.com/gogolaandrej050-svg/Fizlab\">Исходный код</a>", Html.FROM_HTML_MODE_LEGACY));
        sourcecodegithublink.setMovementMethod(LinkMovementMethod.getInstance());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        electronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMenuActivity.this, MenuElectric.class);
                startActivity(intent);
            }
        });
        optika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMenuActivity.this, MenuOptika.class);
                startActivity(intent);
            }
        });
        mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMenuActivity.this, MenuMechanic.class);
                startActivity(intent);
            }
        });

    }
}