package studio.idle.emicalculator;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ujain on 2/20/15.
 */
public class ComparatorFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View comparatorView = inflater.inflate(R.layout.fragment_tab_two, container, false);
        return comparatorView;
    }
}
