package com.example.appuserssqlite;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Instanciar los elementos de xml que tienen id
    EditText fullname, username, password;
    Spinner rol;
    ImageButton save,search,edit,delete,list;
    TextView message;
    // Crear un array de tipo string para llenar el spinner con las
    // opciones: Administrador y Usuario
    // Definir el array
    String[] arrRoles = {"Administrador","Usuario"};
    // Instanciar la clase de SQLite (clsDB)
    clsDB oDB = new clsDB(this,"dbusers",null,1);
    // crear el objeto de user de manera global
    User oUser = new User();
    String usernameFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Referenciar los objetos de los ids del archivo xml
        fullname = findViewById(R.id.etFullName);
        username = findViewById(R.id.etUserName);
        password = findViewById(R.id.etPassword);
        rol = findViewById(R.id.spRol);
        save = findViewById(R.id.ibSave);
        search = findViewById(R.id.ibSearch);
        edit = findViewById(R.id.ibEdit);
        delete = findViewById(R.id.ibDelete);
        list = findViewById(R.id.ibList);
        message = findViewById(R.id.tvMessage);
        // Llenar el spinner rol con los datos del array
        // 1. Crear el adaptador
        ArrayAdapter<String> adpRoles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,arrRoles);
        // Asignar el adptador al spinner
        rol.setAdapter(adpRoles);
        // Eventos de los botones
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // invocar la actividad listUser.class
                startActivity(new Intent(getApplicationContext(),listUsers.class));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUserName = username.getText().toString();
                String mfullName = fullname.getText().toString();
                String mPassword = password.getText().toString();
                int mRol = rol.getSelectedItem().toString().equals("Administrador") ? 1 : 0;
                if (checkDataEmpty(mUserName,mfullName,mPassword)){
                    SQLiteDatabase osldbUser = oDB.getWritableDatabase();;
                    if (mUserName.equals(usernameFound)){ // No cambia el username
                        // Actualizar el registro en la tabla user
                        osldbUser.execSQL("UPDATE user set fullname = '"+mfullName+"', password = '"+mPassword+"', rol = "+mRol+" where username = '"+mUserName+"'");
                        mesageTextColor(true, "Usuario actualizado correctamente...");
                    }
                    else{
                        //Cambia el username y se buscará el nuevo ...
                        if (searchUser(mUserName).isEmpty()){
                            osldbUser.execSQL("UPDATE user set username = '"+mUserName+"', fullname = '"+mfullName+"', password = '"+mPassword+"', rol = "+mRol+" where username = '"+usernameFound+"'");
                            mesageTextColor(true, "Usuario actualizado correctamente...");
                        }
                        else{
                            mesageTextColor(false, "Usuario asignado a otro. Inténtelo de nuevo...");
                        }
                    }

                }
                else{
                    mesageTextColor(false, "Debe diligenciar todos los datos para actualizarlos");
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verficar que se haya digitado el nombre usuario
                String mUserName = username.getText().toString();
                if (!mUserName.isEmpty()){
                    // Buscar el userName (mUserName)
                    if (searchUser(mUserName).size() > 0){
                        fullname.setText(oUser.getFullname());
                        rol.setSelection(oUser.getRol() == 1 ? 0 : 1 );
                        message.setText("");
                    }
                    else{
                        mesageTextColor(false, "Usuario NO Existe. Inténtelo con otro");
                    }
                }
                else{
                    mesageTextColor(false, "Debe ingresar el usuario a buscar!");
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar que todos los datos estén diligenciados
                String mFullname = fullname.getText().toString();
                String mUsername = username.getText().toString();
                String mPassword = password.getText().toString();
                String mRol = rol.getSelectedItem().toString();
                if (checkDataEmpty(mFullname,mUsername,mPassword)){
                    if (searchUser(mUsername).size() == 0){
                        // Agregar ese usuario
                        // Crear un objeto de contentValues para agregar el registro
                        ContentValues cvUser = new ContentValues();
                        cvUser.put("username",mUsername);
                        cvUser.put("fullname", mFullname);
                        cvUser.put("password", mPassword);
                        cvUser.put("rol", mRol.equals("Administrador") ? 1:0);
                        // Agregar el usuario teniendo en cuenta el objeto de content values
                        SQLiteDatabase sdbWrite = oDB.getWritableDatabase();
                        sdbWrite.insert("user",null,cvUser);
                        sdbWrite.close();
                        mesageTextColor(true, "Usuario agregado correctamente");
                    }
                    else{
                        mesageTextColor(false, "Usuario Existente. Inténtelo con otro..." );
                    }
                }
                else{
                   mesageTextColor(false, "Debe ingresar todos los datos...");
                }
            }
        });
    }

    private ArrayList<User> searchUser(String mUsername) {
        ArrayList<User> arrUser = new ArrayList<User>();
        SQLiteDatabase osdbRead = oDB.getReadableDatabase();
        String query = "Select fullname, rol from user where username ='"+mUsername+"'";
        Cursor cUser = osdbRead.rawQuery(query,null);
        if (cUser.moveToFirst()){
            // se llenan los datos del objeto oUser
            usernameFound = mUsername;
            oUser.setUsername(mUsername);
            oUser.setFullname(cUser.getString(0));
            oUser.setPassword("");
            oUser.setRol(cUser.getInt(1));
            // Agregar este objeto al arrayList
            arrUser.add(oUser);
        }
        return arrUser;
    }

    private boolean checkDataEmpty(String mFullname, String mUsername, String mPassword) {
        return !mFullname.isEmpty() && !mUsername.isEmpty() && !mPassword.isEmpty();
    }

    public void mesageTextColor(boolean color, String mess){
        message.setTextColor(Color.parseColor(color ? "#0a8008" : "#bf2d09"));
        message.setText(mess);
    }
}