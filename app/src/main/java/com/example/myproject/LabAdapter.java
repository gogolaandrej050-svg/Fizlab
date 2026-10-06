package com.example.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LabAdapter extends ArrayAdapter<Lab> {
    int resource;
    public LabAdapter(@NonNull Context context, int resource, ArrayList<Lab> labs) {
        super(context, resource, labs);
        this.resource = resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
        }
        Lab lab = getItem(position);
        ((TextView) convertView.findViewById(R.id.nameView)).setText(lab.name);
        ImageView photoView = convertView.findViewById(R.id.photoView);
        switch (lab.name) {
            //Электричество
            case "Исследование зависимости сопротивления проводника от его параметров" :
                photoView.setImageResource(R.drawable.circuit);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#1");
                break;
            case "Изучение законов последовательного соединений проводников" :
                photoView.setImageResource(R.drawable.circut2);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#2");
                break;
            case "Измерение сопротивления проводника. Изучение принципа действия реостата" :
                photoView.setImageResource(R.drawable.circuit3);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#3");
                break;
                //Оптика
            case "Исследование зависимости угла отражения от угла падения" :
                photoView.setImageResource(R.drawable.optika1);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#1");
                break;
            case "Изучение законов преломления света" :
                photoView.setImageResource(R.drawable.optika2);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#2");
                break;
            case "Построение изображений в тонких линзах":
                photoView.setImageResource(R.drawable.optika3);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#3");
                break;
            //Механика
            case "Исследование закона Гука и измерение жесткости пружины" :
                photoView.setImageResource(R.drawable.optika1);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#1");
                break;
            case "Определение коэффициента трения скольжения" :
                photoView.setImageResource(R.drawable.optika2);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#2");
                break;
            case "Выяснение условий равновесия рычага":
                photoView.setImageResource(R.drawable.optika3);
                ((TextView) convertView.findViewById(R.id.labNumber)).setText("#3");
                break;
        }
        return convertView;
    }
}
