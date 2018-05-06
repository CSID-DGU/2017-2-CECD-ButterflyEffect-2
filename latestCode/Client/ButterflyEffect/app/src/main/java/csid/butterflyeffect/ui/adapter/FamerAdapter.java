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
import java.util.Collections;

import csid.butterflyeffect.FirebaseTasks;
import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.Famer;
import csid.butterflyeffect.util.Utils;

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
        holder.updateTime.setText(Utils.getTime(famer.getUpdatedTime()));
        holder.phone.setText(Utils.getEcryptedNumber(famer.getPhoneNumber()));
        Picasso.get().load(famer.getImageUrl()).into(holder.image);
        holder.rank.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return famers.size();
    }

    public void orderData() {
        Collections.sort(famers);
    }

    public class FamerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView updateTime;
        TextView score;
        TextView phone;
        ImageView image;
        TextView rank;


        public FamerViewHolder(View itemView) {
            super(itemView);
            updateTime = (TextView) itemView.findViewById(R.id.tv_time);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            phone = (TextView)itemView.findViewById(R.id.tv_phone);
            image = (ImageView)itemView.findViewById(R.id.iv_fame_image);
            rank = (TextView)itemView.findViewById(R.id.tv_rank);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
