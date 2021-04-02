package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import huy289.cb.plantcomunity.AddPlantActivity;
import huy289.cb.plantcomunity.DetailPlantActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.R;

public class MyPlantAdapter extends RecyclerView.Adapter<MyPlantAdapter.ViewHolder>{

    private Context mContext;
    private List<Plant> mPlants;
    private FirebaseUser fUser;

    public MyPlantAdapter(Context mContext, List<Plant> mPlants) {
        this.mContext = mContext;
        this.mPlants = mPlants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_plant_item, parent, false);

        return new MyPlantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Plant plant = mPlants.get(position);
        Picasso.get().load(plant.getImageUrl())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_leaf)
                .resize(400, 400)
                .centerCrop()
                .onlyScaleDown()
                .into(holder.imagePlant);
        holder.name.setText("Tên: " + plant.getName());
        holder.age.setText("Tuổi: " + plant.getAge());
        holder.address.setText("Địa chỉ: " + plant.getAddress());
        holder.quantity.setText("Tổng số lượng: " + plant.getQuantity());
        Locale vn = new Locale("vi", "VN");
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vn);
        holder.price.setText("Đơn giá: " + vndFormat.format(Float.parseFloat(plant.getPrice())));

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser.getUid().equals(plant.getPublisher())) {
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddPlantActivity.class);
                    intent.putExtra("plantId", plant.getId());
                    intent.putExtra("publisherId", plant.getPublisher());
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.btnDetail.setText("Xem chi tiết cây");
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailPlantActivity.class);
                    intent.putExtra("plantId", plant.getId());
                    mContext.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mPlants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagePlant;
        public TextView name;
        public TextView age;
        public TextView address;
        public TextView quantity;
        public TextView price;
        public Button btnDetail;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePlant = itemView.findViewById(R.id.image_plant);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            address = itemView.findViewById(R.id.address);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }

}
