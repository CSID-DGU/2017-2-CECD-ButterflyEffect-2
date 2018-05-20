package csid.butterflyeffect.ui.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.model.UserInfo;
import csid.butterflyeffect.util.Constants;
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
        holder.background.setBackgroundColor(Utils.getIntFromColor(userInfo.getR(),userInfo.getG(),userInfo.getB()));
        if(userInfo.getUserProfile()!=null)
            holder.profile.setImageBitmap(userInfo.getUserProfile());

        //boost view
        boolean isBoost = userInfo.isBoost();
        if(isBoost){
            holder.boost.setBackgroundColor(mContext.getResources().getColor(R.color.material_red));
        }
        else
            holder.boost.setBackgroundColor(mContext.getResources().getColor(R.color.black_semi_transparent70));

        //user color view
        ArrayList<Integer> userColors = userInfo.getColors();
        for(int i=0;i<userColors.size();i++){
            holder.colors[i].setBackgroundColor(userColors.get(i));
            int usercolor = userColors.get(i);
            //Log.d("#####", "r:"+Color.red(usercolor)+"/g:"+Color.green(usercolor)+"/b:"+Color.blue(usercolor));
        }

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
        LinearLayout background;
        LinearLayout boost;
        View colors[] = new View[Constants.USER_COLOR_LISTS_NUM];

        public UserViewHolder(View itemView) {
            super(itemView);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            profile = (CircleImageView)itemView.findViewById(R.id.iv_user_profile);
            background = (LinearLayout) itemView.findViewById(R.id.ll_color_view);
            boost = (LinearLayout)itemView.findViewById(R.id.ll_boost_view);

            for(int i=0;i< Constants.USER_COLOR_LISTS_NUM;i++)
                colors[i] = (View)itemView.findViewById(Constants.COLOR_LISTS[i]);

        }
        @Override
        public void onClick(View v) {

        }
    }
}

