package lj.justdeliver.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lj.justdeliver.AboutUsActivity;
import lj.justdeliver.R;
import lj.justdeliver.SettingsActivity;
import lj.justdeliver.adapters.SenderGigsInProgressAdapter;
import lj.justdeliver.model.OneGig;

import static android.content.Context.MODE_PRIVATE;

public class DisplaySenderInProgressGig extends Fragment {
    private ArrayList<OneGig> gigList;
    private DatabaseReference dbRef;
    private ValueEventListener eventListner;
    private SenderGigsInProgressAdapter gigAdapter;
    private Query query;


    public DisplaySenderInProgressGig() {
        // Required empty public constructor
    }

    public static DisplaySenderInProgressGig newInstance() {
        DisplaySenderInProgressGig fragment = new DisplaySenderInProgressGig();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        gigList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference("Gigs");
        SharedPreferences preferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        String creator = preferences.getString("uid", "");

        dbRef.keepSynced(true);
        query = dbRef.orderByChild("creatorID").equalTo(creator);
        eventListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    try {
                        Iterable<DataSnapshot> gigs = dataSnapshot.getChildren();
                        if (gigs != null) {
                            gigList.clear();
                            for (DataSnapshot ds : gigs) {
                                OneGig gig = ds.getValue(OneGig.class);
                                if (!gig.driverID.equals("no"))
                                    gigList.add(gig);
                            }
                            gigAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gigAdapter = new SenderGigsInProgressAdapter(getActivity(), gigList);
        query.addValueEventListener(eventListner);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_sender_gig, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(eventListner);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.all_gigs_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.menu_about_us:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(View view) {
        RecyclerView rvViewSenderGigs = (RecyclerView) view.findViewById(R.id.rvViewSenderGigs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvViewSenderGigs.setLayoutManager(layoutManager);
        rvViewSenderGigs.setAdapter(gigAdapter);

    }
}
