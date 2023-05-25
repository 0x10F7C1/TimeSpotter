package com.example.timespotter;

import com.example.timespotter.DataModels.User;

//Ova klasa ce da sluzi da se u njoj cuvaju podaci koji treba da budu perzistirani
//kroz ceo zivotni vek aplikacije i koji su potrebni svim activity-ma,
//kako ne bi morao da koristim SharedPreferences i TempFiles
public class AppData {
    public static User user;
}
