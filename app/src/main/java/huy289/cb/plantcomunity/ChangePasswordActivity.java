package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText password;
    private EditText newPassword;
    private EditText reNewPassword;
    private Button changePassword;

    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbar = findViewById(R.id.toolbar);
        password = findViewById(R.id.password);
        newPassword = findViewById(R.id.new_password);
        reNewPassword = findViewById(R.id.re_new_password);
        changePassword = findViewById(R.id.change_password);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(password.getText().toString())
                        || TextUtils.isEmpty(newPassword.getText().toString())
                        || TextUtils.isEmpty(reNewPassword.getText().toString())) {

                    Toast.makeText(ChangePasswordActivity.this, "Hãy nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();

                } else {
                    changePassword();
                }
            }
        });

    }

    private void changePassword() {
        String email = fUser.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password.getText().toString());
        fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if(newPassword.getText().toString().length() < 6) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                    } else if(!newPassword.getText().toString().equals(reNewPassword.getText().toString())) {
                        Toast.makeText(ChangePasswordActivity.this, "Nhập lại mật khẩu phải trùng với mật khẩu mới", Toast.LENGTH_SHORT).show();
                    } else {
                        fUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ChangePasswordActivity.this, "Đã thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}