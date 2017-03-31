package lj.justdeliver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import lj.justdeliver.R;
import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.holders.SenderGigsHolder;
import lj.justdeliver.model.OneGig;

/**
 * Created by lj on 2/20/2017.
 */

public class SenderGigsRecyclerAdapter extends RecyclerView.Adapter<SenderGigsHolder> {
    private LayoutInflater inflater;
    private ArrayList<OneGig> gigList;

    public SenderGigsRecyclerAdapter(Context context, ArrayList<OneGig> gigList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gigList = gigList;
    }

    @Override
    public SenderGigsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new SenderGigsHolder(inflater.inflate(R.layout.item_sender_gig, parent, false), viewType);
        else
            return new SenderGigsHolder(inflater.inflate(R.layout.item_no_sender_gig, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(SenderGigsHolder holder, int position) {
        if (CommonConstants.isDriver || position < gigList.size())
            holder.bind(gigList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (CommonConstants.isDriver) {
            return 1;
        }
        if (position < gigList.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (CommonConstants.isDriver) {
            return gigList.size();
        } else
            return gigList.size() + 1;
    }
}
