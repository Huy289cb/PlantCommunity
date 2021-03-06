package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import huy289.cb.plantcomunity.AddPlantActivity;
import huy289.cb.plantcomunity.Model.Plant;
import huy289.cb.plantcomunity.Model.Prevention;
import huy289.cb.plantcomunity.PreventionActivity;
import huy289.cb.plantcomunity.R;

public class PreventionAdapter extends RecyclerView.Adapter<PreventionAdapter.ViewHolder> {

    private Context mContext;
    private List<Prevention> mPreventions;
    private FirebaseUser fUser;
    private String publisher;

    public PreventionAdapter(Context mContext, List<Prevention> mPreventions) {
        this.mContext = mContext;
        this.mPreventions = mPreventions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.prevention_item, parent, false);

        return new PreventionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Prevention prevention = mPreventions.get(position);
        Picasso.get().load(prevention.getImageUrl())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_leaf)
                .resize(400, 400)
                .centerCrop()
                .onlyScaleDown()
                .into(holder.image);
        holder.description.setText("M?? t???: " + prevention.getDescription());
        holder.performer.setText("Ng?????i th???c hi???n: " + prevention.getPerformer());
        holder.date.setText("Ng??y: " + prevention.getDate());

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("Plants").child(prevention.getPlantId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Plant plant = snapshot.getValue(Plant.class);
                publisher = plant.getPublisher();
                if (!fUser.getUid().equals(publisher)){
                    holder.detail.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PreventionActivity.class);
                intent.putExtra("plantId", prevention.getPlantId());
                intent.putExtra("preventionId", prevention.getId());

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPreventions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView description;
        public TextView performer;
        public TextView date;
        public Button detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            performer = itemView.findViewById(R.id.performer);
            date = itemView.findViewById(R.id.date);
            detail = itemView.findViewById(R.id.btn_detail);
        }
    }

}
