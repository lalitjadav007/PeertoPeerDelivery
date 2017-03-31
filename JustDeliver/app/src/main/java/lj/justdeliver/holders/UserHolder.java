package lj.justdeliver.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import lj.justdeliver.R;
import lj.justdeliver.model.User;

/**
 * Created by lj on 2/28/2017.
 */

public class UserHolder extends RecyclerView.ViewHolder {
    private ImageView ivUserItemPic;
    private TextView tvUserItemName;
    private UserSelect userSelect;

    public UserHolder(View itemView, UserSelect userSelect) {
        super(itemView);
        this.userSelect = userSelect;
        init(itemView);
    }

    private void init(View itemView) {
        ivUserItemPic = (ImageView) itemView.findViewById(R.id.ivUserItemPic);
        tvUserItemName = (TextView) itemView.findViewById(R.id.tvUserItemName);
    }

    public void bind(final User user) {
        tvUserItemName.setText(user.fullName);
        if (user.profilePic != null)
            Glide.with(itemView.getContext()).load(user.profilePic).override(150, 150).dontAnimate().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(ivUserItemPic);
        else
            Glide.with(itemView.getContext()).load(R.drawable.placeholder).override(150, 150).dontAnimate().placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(ivUserItemPic);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSelect != null)
                    userSelect.userSelected(user);
            }
        });

    }

    public interface UserSelect {
        void userSelected(User selectedUser);
    }
}
