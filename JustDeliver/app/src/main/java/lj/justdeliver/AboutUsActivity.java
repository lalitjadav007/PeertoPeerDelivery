package lj.justdeliver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.label_about_us));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.llCallAction).setOnClickListener(this);
        findViewById(R.id.llShowInMap).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCallAction:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+918460159166")));
                break;
            case R.id.llShowInMap:
                String uriBegin1 = "geo:23.040529,72.543063";
                String query1 = "23.040529,72.543063(Just Deliver Office)";
                String encodedQuery1 = Uri.encode(query1);
                String uriString1 = uriBegin1 + "?q=" + encodedQuery1 + "&z=16";
                Uri uri1 = Uri.parse(uriString1);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent1);
                break;
        }
    }
}
