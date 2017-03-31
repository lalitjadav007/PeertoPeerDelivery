package lj.quickdilevery.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DriverGig extends AppCompatActivity implements View.OnClickListener {
	Button createGig, viewGig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_gig);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));

		createGig = (Button) findViewById(R.id.btn_create_gig);
		viewGig = (Button) findViewById(R.id.btn_view_gig);

		createGig.setOnClickListener(this);
		viewGig.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_gig:
			startActivity(new Intent(getApplicationContext(), AllGig.class));
			break;
		case R.id.btn_view_gig:
			startActivity(new Intent(getApplicationContext(), ViewGig.class));
			break;
		}
	}
}
