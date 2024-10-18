package com.example.appuserssqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class listUsers extends AppCompatActivity {

    ListView listUsers;
    clsDB oDB = new clsDB(this,"dbusers",null,1);
    //Definir un objeto para el arrayList que pasara los datos al ArrayAdapter
    ArrayList<String> arrUsers;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listUsers = findViewById(R.id.lvUsers);
        loadUsers();
    }

    private void loadUsers() {
        arrUsers = getAllUsers();
        //Asignar  al ArrayAdapter lo que tiene el ArrayList arrUsers
        ArrayAdapter<String> adpUsers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrUsers);
        //Llenar el lastView con el arrayAdapter adpUsers
        listUsers.setAdapter(adpUsers);
    }

    private ArrayList<String> getAllUsers() {
        ArrayList<String> dataUsers = new ArrayList<String>();
        SQLiteDatabase osqdbUsers = oDB.getReadableDatabase();
        Cursor cUsers = osqdbUsers.rawQuery("Select fullname, Username, rol from user",null);

        if (cUsers.moveToFirst()){
            do {
                String lUser = cUsers.getString(0) + "\n" + cUsers.getString(1) + "\n" + (cUsers.getInt(2)== 1 ? "Administrador" : "Usuario");
                dataUsers.add(lUser);
            } while (cUsers.moveToNext());
        }
        return dataUsers;
    }
}