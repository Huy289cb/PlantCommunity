package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import huy289.cb.plantcomunity.Adapter.MyPlantAdapter;
import huy289.cb.plantcomunity.Adapter.PreventionAdapter;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.Prevention;

public class AddPlantActivity extends AppCompatActivity {

    private ImageView close;
    private TextView title;
    private TextView save;
    private TextView progressText;
    private ProgressBar progressBar;
    private ImageView imageAdded;
    private Button chooseImage;
    private EditText name;
    private EditText age;
    private EditText address;
    private EditText quantity;
    private EditText price;
    private Button delete;

    //Growth
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText sowingDate;
    private EditText plantDate;

    private ImageView addPrevention;
    private RecyclerView recyclerViewPrevention;
    private List<Prevention> mPreventions;
    private PreventionAdapter preventionAdapter;

    private FirebaseUser fUser;

    private Uri imageUri;
    private String imageUrl;

    //get from intent
    private String plantId;
    private String pulisherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        getViews();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final Intent intent = getIntent();
        plantId = intent.getStringExtra("plantId");
        pulisherId = intent.getStringExtra("publisherId");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlant();
            }
        });

        if (intent.hasExtra("publisherId")) {
            if(pulisherId.equals(fUser.getUid())) {
                title.setText("Cập nhật cây");
                chooseImage.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                addPrevention.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference("Plants")
                        .child(plantId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Plant plant = snapshot.getValue(Plant.class);
                        Picasso.get().load(plant.getImageUrl())
                                .error(R.drawable.ic_error)
                                .placeholder(R.drawable.ic_leaf)
                                .into(imageAdded);
                        name.setText(plant.getName());
                        age.setText(plant.getAge());
                        address.setText(plant.getAddress());
                        quantity.setText(plant.getQuantity());
                        price.setText(plant.getPrice());
                        sowingDate.setText(plant.getSowingDate());
                        plantDate.setText(plant.getPlantDate());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePlant();
                    }
                });
                addPrevention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentPrevention = new Intent(AddPlantActivity.this, PreventionActivity.class);
                        intentPrevention.putExtra("plantId", plantId);
                        startActivity(intentPrevention);
                    }
                });

            } else {
                //disable view
                title.setText("Chi tiết cây");
                chooseImage.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                addPrevention.setVisibility(View.GONE);

            }
        } else {
            title.setText("Thêm mới cây");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(AddPlantActivity.this);
            }
        });

        sowingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPickerDate(sowingDate);
            }
        });

        plantDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPickerDate(plantDate);
            }
        });

        recyclerViewPrevention.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPrevention.setLayoutManager(linearLayoutManager);
        mPreventions = new ArrayList<>();

        preventionAdapter = new PreventionAdapter(this, mPreventions);
        recyclerViewPrevention.setAdapter(preventionAdapter);
        getPreventions();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlant();
            }
        });

    }

    private void getPreventions() {
        FirebaseDatabase.getInstance().getReference("Preventions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPreventions.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Prevention prevention = dataSnapshot.getValue(Prevention.class);
                    if(prevention.getPlantId().equals(plantId)) {
                        mPreventions.add(prevention);
                    }
                }
                preventionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deletePlant() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Xóa cây này?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("Plants")
                        .child(plantId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(AddPlantActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

    private void updatePlant() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("age", age.getText().toString());
        map.put("address", address.getText().toString());
        map.put("quantity", quantity.getText().toString());
        map.put("price", price.getText().toString());
        map.put("sowingDate", sowingDate.getText().toString());
        map.put("plantDate", plantDate.getText().toString());

        FirebaseDatabase.getInstance().getReference("Plants")
                .child(plantId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddPlantActivity.this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void savePlant() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        if(imageUri!=null) {
            //compress image 100% -> 12%
            imageAdded.setDrawingCacheEnabled(true);
            imageAdded.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageAdded.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 12, baos);
            byte[] data = baos.toByteArray();

            final StorageReference firePath = FirebaseStorage.getInstance()
                    .getReference("Plants").child(System.currentTimeMillis() + ".jpeg");
            StorageTask uploadtask = firePath.putBytes(data);
            uploadtask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                    progressText.setText("Đang tải lên " + progress + " %");
                }
            }).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return firePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Plants");
                    String plantId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", plantId);
                    map.put("imageUrl", imageUrl);
                    map.put("name", name.getText().toString());
                    map.put("age", age.getText().toString());
                    map.put("address", address.getText().toString());
                    map.put("quantity", quantity.getText().toString());
                    map.put("price", price.getText().toString());
                    map.put("sowingDate", sowingDate.getText().toString());
                    map.put("plantDate", plantDate.getText().toString());
                    map.put("publisher", fUser.getUid());

                    ref.child(plantId).setValue(map);

                    progressText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    addPrevention.setVisibility(View.VISIBLE);
                    Toast.makeText(AddPlantActivity.this, "Đã thêm thành công", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressText.setText("Có lỗi xảy ra khi tải lên!");
                    Toast.makeText(AddPlantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddPlantActivity.this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            progressText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            imageUri = result.getUri();
            imageAdded.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Hãy thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void editTextPickerDate(final EditText editText) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(editText);
            }
        };
        new DatePickerDialog(AddPlantActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void getViews() {
        close = findViewById(R.id.close);
        title = findViewById(R.id.title);
        save = findViewById(R.id.save);
        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        imageAdded = findViewById(R.id.image_added);
        chooseImage = findViewById(R.id.choose_image);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        address = findViewById(R.id.address);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        sowingDate = findViewById(R.id.sowingDate);
        plantDate = findViewById(R.id.plantDate);
        addPrevention = findViewById(R.id.add_prevention);
        recyclerViewPrevention = findViewById(R.id.recycle_view_prevention);
        delete = findViewById(R.id.delete);

//        set visible
        progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        addPrevention.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
    }
}