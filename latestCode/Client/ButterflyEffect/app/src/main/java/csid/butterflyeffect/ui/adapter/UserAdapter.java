package csid.butterflyeffect.ui.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hanseungbeom on 2018. 2. 27..
 */


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context mContext;
    private ArrayList<UserInfo> userInfos;

    public UserAdapter(Context context, ArrayList<UserInfo> result) {
        mContext = context;
        userInfos = result;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        UserInfo userInfo = userInfos.get(position);
        holder.score.setText(String.valueOf(userInfo.getScore()));
        holder.background.setBackgroundColor(Utils.getColor(userInfo.getUserNumber()));
       //holder.profile.setImageBitmap(userInfo.getProfile());

        //boost view
        boolean isBoost = userInfo.isBoost();
        if(isBoost){
            holder.boost.setBackgroundColor(mContext.getResources().getColor(R.color.material_red));
        }
        else
            holder.boost.setBackgroundColor(mContext.getResources().getColor(R.color.black_semi_transparent70));

    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    public void swapData() {

    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView score;
        CircleImageView profile;
        View background;
        LinearLayout boost;

        public UserViewHolder(View itemView) {
            super(itemView);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            profile = (CircleImageView)itemView.findViewById(R.id.iv_user_profile);
            background = (View)itemView.findViewById(R.id.color_view);
            boost = (LinearLayout)itemView.findViewById(R.id.ll_boost_view);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

