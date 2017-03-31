package lj.justdeliver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import lj.justdeliver.R;
import lj.justdeliver.holders.AllGigsHolder;
import lj.justdeliver.model.OneGig;

/**
 * Created by lj on 2/20/2017.
 */

public class DriverAllGigsRecyclerAdapter extends RecyclerView.Adapter<AllGigsHolder> {
    private LayoutInflater inflater;
    private ArrayList<OneGig> gigList;

    public DriverAllGigsRecyclerAdapter(Context context, ArrayList<OneGig> gigList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gigList = gigList;
    }

    @Override
    public AllGigsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllGigsHolder(inflater.inflate(R.layout.item_sender_gig, parent, false));
    }

    @Override
    public void onBindViewHolder(AllGigsHolder holder, int position) {
        holder.bind(gigList.get(position));
    }

    @Override
    public int getItemCount() {
        return gigList.size();
    }
}
