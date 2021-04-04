package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText address;
    private EditText phone;
    private EditText note;
    private Spinner spinner;
    private TextView itemCount;
    private TextView total;
    private TextView totalPrice;
    private Button purchase;

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        toolbar = findViewById(R.id.toolbar);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone_number);
        note = findViewById(R.id.note);
        spinner = findViewById(R.id.spinner_purchase);
        total = findViewById(R.id.total);
        totalPrice = findViewById(R.id.total_price);
        purchase = findViewById(R.id.purchase);
        itemCount = findViewById(R.id.item_count);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("THANH TOÁN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        itemCount.setText(intent.getStringExtra("itemCount"));
        Locale vn = new Locale("vi", "VN");
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
        total.setText(vndFormat.format(Float.parseFloat(intent.getStringExtra("total"))));
        Float sum = Float.parseFloat(intent.getStringExtra("total")) + 100000;

        totalPrice.setText(vndFormat.format(sum));

        String[] items = new String[]
                {"Thanh toán khi nhận hàng",
                "Thanh toán bằng thẻ",
                "Thanh toán bằng ví điện tử"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 2 || position == 1) {
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 1 || position == 2) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(address.getText().toString()) || TextUtils.isEmpty(phone.getText().toString())) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("address", address.getText().toString());
                    map.put("phone", phone.getText().toString());
                    map.put("note", note.getText().toString());
                    map.put("method", spinner.getSelectedItem().toString());
                    //chưa thêm vào db

                    FirebaseDatabase.getInstance().getReference("Carts").child(fUser.getUid()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(CheckoutActivity.this, "Đã đặt hàng", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(CheckoutActivity.this, "Hãy nhập địa chỉ và số điện thoại!", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

    }
}