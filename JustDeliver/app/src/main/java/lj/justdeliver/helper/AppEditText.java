package lj.justdeliver.helper;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.EditText;

import lj.justdeliver.R;

/**
 * Created by lj on 3/16/2017.
 */

public class AppEditText extends EditText {
    public AppEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackground(getResources().getDrawable(R.drawable.background_edittext));
        setPadding((int) getResources().getDimension(R.dimen.normal_padding), (int) getResources().getDimension(R.dimen.normal_padding),
                (int) getResources().getDimension(R.dimen.normal_padding), (int) getResources().getDimension(R.dimen.normal_padding));
    }

    public AppEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AppEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackground(getResources().getDrawable(R.drawable.background_edittext, null));
        setPadding((int) getResources().getDimension(R.dimen.normal_padding), (int) getResources().getDimension(R.dimen.normal_padding),
                (int) getResources().getDimension(R.dimen.normal_padding), (int) getResources().getDimension(R.dimen.normal_padding));
    }
}
