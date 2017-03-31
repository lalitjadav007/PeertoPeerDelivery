package lj.justdeliver.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

import lj.justdeliver.CreateGigActivity;
import lj.justdeliver.R;
import lj.justdeliver.SelectedGigActivity;
import lj.justdeliver.model.OneGig;

/**
 * Created by lj on 2/20/2017.
 */

public class SenderInProgressHolder extends RecyclerView.ViewHolder {
    private TextView tvGigSenderTitle;
    private TextView tvGigSenderDesc;
    private TextView tvItemAmountSenderGig;
    private ImageView ivGigSenderImage;

    public SenderInProgressHolder(View itemView, int viewType) {
        super(itemView);
        if (viewType == 1) {
            init(itemView);
        } else {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    context.startActivity(new Intent(context, CreateGigActivity.class));
                }
            });
        }
    }

    private void init(View itemView) {
        tvGigSenderTitle = (TextView) itemView.findViewById(R.id.tvGigSenderTitle);
        tvGigSenderDesc = (TextView) itemView.findViewById(R.id.tvGigSenderDesc);
        tvItemAmountSenderGig = (TextView) itemView.findViewById(R.id.tvItemAmountSenderGig);
        ivGigSenderImage = (ImageView) itemView.findViewById(R.id.ivGigSenderImage);
    }

    public void bind(final OneGig oneGig) {
        tvGigSenderTitle.setText(oneGig.gigName);
        tvGigSenderDesc.setText(oneGig.deliverLocation.addressName);
        tvItemAmountSenderGig.setText(new DecimalFormat("##.##").format(oneGig.charge));

        if (oneGig.gigImage != null)
            Glide.with(ivGigSenderImage.getContext()).load(oneGig.gigImage).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).override(100, 100).into(ivGigSenderImage);
        else
            Glide.with(ivGigSenderImage.getContext()).load(R.drawable.placeholder).override(100, 100).into(ivGigSenderImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SelectedGigActivity.class);
                intent.putExtra("gig", oneGig);
                view.getContext().startActivity(intent);
            }
        });
    }
}
