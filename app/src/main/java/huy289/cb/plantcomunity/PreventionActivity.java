package huy289.cb.plantcomunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import huy289.cb.plantcomunity.Model.Prevention;

public class PreventionActivity extends AppCompatActivity {

    private ImageView close;
    private TextView progressText;
    private ProgressBar progressBar;
    private ImageView imageAdded;
    private Button chooseImage;
    private EditText date;
    private EditText performer;
    private EditText description;
    private Button save;
    private Button delete;

    private Uri imageUri;
    private String plantId;

    private String preventionId;

    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevention);

        getViews();

        Intent intent = getIntent();
        plantId = intent.getStringExtra("plantId");

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(PreventionActivity.this);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPickerDate(date);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrevention();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Xem chi tiết
        if(intent.hasExtra("preventionId")) {
            chooseImage.setVisibility(View.GONE);
            preventionId = intent.getStringExtra("preventionId");
            FirebaseDatabase.getInstance().getReference("Preventions")
                    .child(preventionId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Prevention prevention = snapshot.getValue(Prevention.class);

                    Picasso.get().load(prevention.getImageUrl())
                            .error(R.drawable.ic_error)
                            .placeholder(R.drawable.ic_leaf)
                            .resize(400, 400)
                            .centerCrop()
                            .onlyScaleDown()
                            .into(imageAdded);
                    description.setText(prevention.getDescription());
                    performer.setText(prevention.getPerformer());
                    date.setText(prevention.getDate());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            save.setText("Lưu thay đổi");
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePrevention();
                }
            });
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePrevention();
                }
            });
        }
    }

    private void deletePrevention() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Bạn có muốn xóa?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("Preventions")
                        .child(preventionId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(PreventionActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void updatePrevention() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("performer", performer.getText().toString());
        map.put("description", description.getText().toString());
        map.put("date", date.getText().toString());

        FirebaseDatabase.getInstance().getReference("Preventions")
                .child(preventionId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PreventionActivity.this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePrevention() {
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
                    .getReference("Preventions").child(System.currentTimeMillis() + ".jpeg");
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
                    String imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Preventions");
                    String preventionId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", preventionId);
                    map.put("imageUrl", imageUrl);
                    map.put("performer", performer.getText().toString());
                    map.put("description", description.getText().toString());
                    map.put("date", date.getText().toString());
                    map.put("plantId", plantId);

                    ref.child(preventionId).setValue(map);

                    progressText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PreventionActivity.this, "Đã thêm thành công", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressText.setText("Có lỗi xảy ra khi tải ảnh lên!");
                    Toast.makeText(PreventionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PreventionActivity.this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            progressText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

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

    private void getViews() {
        close = findViewById(R.id.close);
        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        imageAdded = findViewById(R.id.image_added);
        chooseImage = findViewById(R.id.choose_image);
        date = findViewById(R.id.date);
        performer = findViewById(R.id.performer);
        description = findViewById(R.id.description);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
    }

    private void editTextPickerDate(final EditText editText) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(editText);
            }
        };
        new DatePickerDialog(PreventionActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }
}