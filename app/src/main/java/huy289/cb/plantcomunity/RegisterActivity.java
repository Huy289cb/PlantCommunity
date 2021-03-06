package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import huy289.cb.plantcomunity.Model.Plant;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText fullName;
    private EditText email;
    private EditText password;
    private EditText rePassword;
    private Button register;
    private TextView loginUser;

    private FirebaseAuth mAuth;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.et_username);
        fullName = findViewById(R.id.et_fullname);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        rePassword = findViewById(R.id.et_repassword);
        register = findViewById(R.id.btn_register);
        loginUser = findViewById(R.id.tv_login);

        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = userName.getText().toString();
                String txtFullname = fullName.getText().toString();
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();
                String txtRePassword = rePassword.getText().toString();

                if(TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtFullname) || TextUtils.isEmpty(txtEmail)
                || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtRePassword)) {
                    Toast.makeText(RegisterActivity.this, "H??y nh???p ?????y ????? c??c tr?????ng", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "M???t kh???u ph???i l???n h??n 6 k?? t???", Toast.LENGTH_SHORT).show();
                } else if (!txtPassword.equals(txtRePassword)) {
                    Toast.makeText(RegisterActivity.this, "Nh???p l???i m???t kh???u ph???i tr??ng v???i m???t kh???u", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtUsername, txtFullname, txtEmail, txtPassword);
                }
            }
        });


    }

    private void registerUser(final String username, final String fullname, final String email, String password) {

        pd.setMessage("??ang t???o t??i kho???n...");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("fullname", fullname);
                        map.put("email", email);
                        map.put("username", username);
                        map.put("id", mAuth.getCurrentUser().getUid());
                        map.put("bio", "");
                        map.put("imageUrl", "default");

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.getCurrentUser().getUid()).setValue(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            pd.dismiss();
                                            Toast.makeText(RegisterActivity.this,
                                                    "C???p nh???t th??m th??ng tin c?? nh??n ????? c?? tr???i nghi???m t???t h??n",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}