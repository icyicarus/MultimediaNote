package icyicarus.gwu.com.multimedianote.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

public class FragmentSettings extends Fragment {
    @BindView(R.id.switch_show_ok_button)
    SwitchCompat switchShowOKButton;
    SharedPreferences userPreferences = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Settings");

        userPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        switchShowOKButton.setChecked(userPreferences.getBoolean(Variables.SOB, false));
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean(Variables.SOB, switchShowOKButton.isChecked());
        editor.apply();
        editor.commit();
    }

}
