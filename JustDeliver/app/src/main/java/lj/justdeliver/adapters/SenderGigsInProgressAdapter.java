package lj.justdeliver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import lj.justdeliver.R;
import lj.justdeliver.holders.SenderInProgressHolder;
import lj.justdeliver.model.OneGig;

/**
 * Created by lj on 2/20/2017.
 */

public class SenderGigsInProgressAdapter extends RecyclerView.Adapter<SenderInProgressHolder> {
    private LayoutInflater inflater;
    private ArrayList<OneGig> gigList;

    public SenderGigsInProgressAdapter(Context context, ArrayList<OneGig> gigList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gigList = gigList;
    }

    @Override
    public SenderInProgressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SenderInProgressHolder(inflater.inflate(R.layout.item_sender_gig, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(SenderInProgressHolder holder, int position) {
        holder.bind(gigList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return gigList.size();
    }
}
