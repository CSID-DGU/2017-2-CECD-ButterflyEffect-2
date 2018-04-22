package csid.butterflyeffect.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import csid.butterflyeffect.FirebaseTasks;
import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.Famer;

/**
 * Created by hanseungbeom on 2018. 4. 22..
 */
public class FamerAdapter extends RecyclerView.Adapter<FamerAdapter.FamerViewHolder> {
    private Context mContext;
    private ArrayList<Famer> famers;

    public FamerAdapter(Context context, ArrayList<Famer> famers) {
        mContext = context;
        this.famers = famers;
    }

    @Override
    public FamerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hall_of_fame, parent, false);
        return new FamerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FamerViewHolder holder, int position) {
        Famer famer = famers.get(position);
        holder.score.setText(String.valueOf(famer.getScore()));
        holder.updateTime.setText(String.valueOf(famer.getUpdatedTime()));
        holder.phone.setText(famer.getPhoneNumber());
        Picasso.get().load(famer.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return famers.size();
    }

    public void swapData() {

    }

    public class FamerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView updateTime;
        TextView score;
        TextView phone;
        ImageView image;


        public FamerViewHolder(View itemView) {
            super(itemView);
            updateTime = (TextView) itemView.findViewById(R.id.tv_time);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            phone = (TextView)itemView.findViewById(R.id.tv_phone);
            image = (ImageView)itemView.findViewById(R.id.iv_fame_image);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
