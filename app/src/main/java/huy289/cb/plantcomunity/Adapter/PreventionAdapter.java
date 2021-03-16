package huy289.cb.plantcomunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Prevention prevention = mPreventions.get(position);
        Picasso.get().load(prevention.getImageUrl())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_leaf)
                .resize(400, 400)
                .centerCrop()
                .onlyScaleDown()
                .into(holder.image);
        holder.description.setText("Mô tả: " + prevention.getDescription());
        holder.performer.setText("Người thực hiện: " + prevention.getPerformer());
        holder.date.setText("Ngày: " + prevention.getDate());

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
