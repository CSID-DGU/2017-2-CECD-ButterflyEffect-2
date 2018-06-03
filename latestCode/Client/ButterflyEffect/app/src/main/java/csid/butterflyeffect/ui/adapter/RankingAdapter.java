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

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.Famer;
import csid.butterflyeffect.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hanseungbeom on 2018. 6. 3..
 */


public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RakingViewHolder> {
    private Context mContext;
    private ArrayList<Famer> famers;
    private final int FAMER_FIRST = 1;
    private final int FAMER_OTHERS = 2;

    public RankingAdapter(Context context, ArrayList<Famer> famers) {
        mContext = context;
        this.famers = famers;
    }

    @Override
    public RakingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case FAMER_FIRST: {
                View firstView = LayoutInflater.from(mContext).inflate(R.layout.item_rank_first, parent, false);
                return new RakingViewHolder(firstView);
            }
            case FAMER_OTHERS: {
                View otherView = LayoutInflater.from(mContext).inflate(R.layout.item_rank, parent, false);
                return new RakingViewHolder(otherView);
            }
            default:{
                View otherView = LayoutInflater.from(mContext).inflate(R.layout.item_rank, parent, false);
                return new RakingViewHolder(otherView);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RakingViewHolder holder, int position) {
        Famer famer = famers.get(position);
        holder.score.setText(String.valueOf(famer.getScore()));
        holder.phone.setText(Utils.getEcryptedNumber(famer.getPhoneNumber()));
        Picasso.get().load(famer.getImageUrl()).into(holder.image);
        int userRank = position+1;

        if(userRank == 1){
            holder.markview.setImageResource(R.drawable.ic_pot);
            holder.rank.setText("");

        }else if(userRank ==2){
            holder.markview.setImageResource(R.drawable.ic_starbucks);
            holder.rank.setText("");
        }
        else if(userRank == 3) {
            holder.markview.setImageResource(R.drawable.ic_cida);
            holder.rank.setText("");
        }
        else{
            holder.markview.setImageResource(R.drawable.ic_mark);
            holder.rank.setText(String.valueOf(userRank));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return FAMER_FIRST;
        else return FAMER_OTHERS;
    }

    @Override
    public int getItemCount() {
        return famers.size();
    }

    public class RakingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView updateTime;
        TextView score;
        TextView phone;
        CircleImageView image;
        TextView rank;
        ImageView markview;

        public RakingViewHolder(View itemView) {
            super(itemView);
            updateTime = (TextView) itemView.findViewById(R.id.tv_time);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            phone = (TextView)itemView.findViewById(R.id.tv_phone);
            image = (CircleImageView)itemView.findViewById(R.id.iv_fame_image);
            rank = (TextView)itemView.findViewById(R.id.tv_rank);
            markview = (ImageView)itemView.findViewById(R.id.iv_mark);
        }
        @Override
        public void onClick(View v) {

        }
    }

}
