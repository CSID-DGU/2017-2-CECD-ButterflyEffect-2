package csid.butterflyeffect.ui.adapter;

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
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserInfo userInfo = userInfos.get(position);
        holder.score.setText(String.valueOf(userInfo.getScore()));
        holder.background.setBackgroundColor(Utils.getColor(position));
       //holder.profile.setImageBitmap(userInfo.getProfile());

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

        public UserViewHolder(View itemView) {
            super(itemView);
            score = (TextView)itemView.findViewById(R.id.tv_score);
            profile = (CircleImageView)itemView.findViewById(R.id.iv_user_profile);
            background = (View)itemView.findViewById(R.id.color_view);

        }

        @Override
        public void onClick(View v) {

        }
    }
}

