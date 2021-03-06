package com.icyicarus.multimedianote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icyicarus.multimedianote.R;
import mehdi.sakout.aboutpage.AboutPage;

public class FragmentAbout extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("About");
        return new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.drawable.logo_about)
                .setDescription("by Xuenan Xu @ GWU")
                .addGroup("Connect with us")
                .addEmail("xuenanxu@gwmail.gwu.edu")
                .addWebsite("http://www.icyicarus.com/")
                .addGitHub("icyicarus")
                .create();
    }
}
