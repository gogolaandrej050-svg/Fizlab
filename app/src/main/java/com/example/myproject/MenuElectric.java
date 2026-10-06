package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MenuElectric extends AppCompatActivity {
    String[] LabNameArr = {"Исследование зависимости сопротивления проводника от его параметров", "Изучение законов последовательного соединений проводников", "Измерение сопротивления проводника. Изучение принципа действия реостата"};
    ArrayList<Lab> Labs;
    LabAdapter labAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.electric_menu);
        listView = findViewById(R.id.listViewLabs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Labs = new ArrayList<>();
        for (int i = 0; i < LabNameArr.length; i++) {
            Labs.add(new Lab(LabNameArr[i]));
        }
        labAdapter = new LabAdapter(this, R.layout.list_item, Labs);
        listView.setAdapter(labAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) { // Если нажата первая работа
                Intent intent = new Intent(MenuElectric.this, Lab1Activity.class);
                startActivity(intent);
            }
            if (position == 1) { // Если нажата вторая работа
                Intent intent = new Intent(MenuElectric.this, Lab2Activity.class);
                startActivity(intent);
            }
            if (position == 2) { // Если нажата третья работа
                Intent intent = new Intent(MenuElectric.this, Lab3Activity.class);
                startActivity(intent);
            }
        });
    }
}
