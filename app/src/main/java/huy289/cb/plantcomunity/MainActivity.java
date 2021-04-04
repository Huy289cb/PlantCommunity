package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import huy289.cb.plantcomunity.Fragments.HomeFragment;
import huy289.cb.plantcomunity.Fragments.MyPlantFragment;
import huy289.cb.plantcomunity.Fragments.ProfileFragment;
import huy289.cb.plantcomunity.Fragments.SearchFragment;
import huy289.cb.plantcomunity.Fragments.StoreFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check user có đăng nhập thì trở lại start activity
//        if(FirebaseAuth.getInstance().getCurrentUser()==null){
//            startActivity(new Intent(MainActivity.this, StartActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            finish();
//        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.nav_store:
                        selectorFragment = new StoreFragment();
                        break;
                    case R.id.nav_plant:
                        selectorFragment = new MyPlantFragment();
                        break;
                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }
                if(selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit()
                    .putString("profileId", profileId).apply();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
}