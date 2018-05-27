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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import csid.butterflyeffect.R;
import csid.butterflyeffect.game.BattleWorms;
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
        if(userInfo.getUserProfile()!=null)
            holder.profile.setImageBitmap(userInfo.getUserProfile());


        if(position == 0){
            holder.one.setVisibility(View.VISIBLE);
        }
        else
            holder.one.setVisibility(View.INVISIBLE);

        if(userInfo.isPlaying()){
            holder.dieView.setVisibility(View.INVISIBLE);
        }
        else
            holder.dieView.setVisibility(View.VISIBLE);

        holder.scoreColorView.setBackgroundColor(Utils.getIntFromColor(userInfo.getR(),userInfo.getG(),userInfo.getB()));
       /* //user color view
        ArrayList<Integer> userColors = userInfo.getColors();
        for(int i=0;i<userColors.size();i++){
            holder.colors[i].setBackgroundColor(userColors.get(i));
            int usercolor = userColors.get(i);
            //Log.d("#####", "r:"+Color.red(usercolor)+"/g:"+Color.green(usercolor)+"/b:"+Color.blue(usercolor));
        }*/

    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    public void swapData() {

    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView score;
        ImageView profile;
        ImageView one;
        LinearLayout dieView;
        LinearLayout scoreColorView;
        //View colors[] = new View[Constants.USER_COLOR_LISTS_NUM];

        public UserViewHolder(View itemView) {
            super(itemView);
            one = (ImageView)itemView.findViewById(R.id.iv_one);
            score = (TextView)itemView.findViewById(R.id.tv_score);
           // profile = (CircleImageView)itemView.findViewById(R.id.iv_user_profile);
            profile = (ImageView)itemView.findViewById(R.id.iv_user_profile);
            dieView = (LinearLayout) itemView.findViewById(R.id.ll_die_view);
            scoreColorView = (LinearLayout)itemView.findViewById(R.id.ll_score_color_view);

          /*  for(int i=0;i< Constants.USER_COLOR_LISTS_NUM;i++)
                colors[i] = (View)itemView.findViewById(Constants.COLOR_LISTS[i]);
            */
        }
        @Override
        public void onClick(View v) {

        }
    }
    public void swapData(ArrayList<UserInfo> userInfos){
        this.userInfos = userInfos;
        notifyDataSetChanged();
    }
}

