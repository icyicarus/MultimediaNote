package icyicarus.gwu.com.multimedianote.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by Icarus on 1/1/2017.
 */

public class FragmentFeedback extends Fragment {
    private Boolean nameEdited = false, issueEdited = false;
    @BindView(R.id.feedback_name_layout) TextInputLayout feedbackNameLayout;
    @BindView(R.id.feedback_category_layout) TextInputLayout feedbackCategoryLayout;
    @BindView(R.id.feedback_issue_layout) TextInputLayout feedbackIssueLayout;
    @BindView(R.id.feedback_name) TextInputEditText feedbackName;
    @BindView(R.id.feedback_phone) TextInputEditText feedbackPhone;
    @BindView(R.id.feedback_issue) TextInputEditText feedbackIssue;
    @BindView(R.id.feedback_category) AppCompatSpinner feedbackCategory;
    @BindView(R.id.feedback_submit) AppCompatButton feedbackSubmit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Send Feedback");
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, v);

        List<String> spinnerItems = Arrays.asList("", "Bug Report", "Feature Request", "Just to Chat");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerItems) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v;
                if (position == 0) {
                    AppCompatTextView appCompatTextView = new AppCompatTextView(getContext());
                    appCompatTextView.setHeight(0);
                    v = appCompatTextView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackCategory.setAdapter(adapter);
        feedbackCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedbackCategoryLayout.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        feedbackName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                feedbackNameLayout.setError(s.toString().equals("") ? "Invalid Name" : null);
                nameEdited = true;
            }
        });

        feedbackPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        feedbackIssue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                feedbackIssueLayout.setError(s.toString().equals("") ? "Cannot Send Empty Message" : null);
                issueEdited = true;
            }
        });

        return v;
    }

    @OnClick(R.id.feedback_submit)
    public void buttonClick(View v) {
        if (nameEdited && issueEdited && feedbackCategoryLayout.getError() == null && !feedbackCategory.getSelectedItem().toString().equals("") && feedbackIssueLayout.getError() == null) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            StringBuilder sb = new StringBuilder();
            sb.append(feedbackCategory.getSelectedItem().toString()).append(" -- ").append(feedbackName.getText().toString());
            if (!feedbackPhone.toString().equals(""))
                sb.append(" -- ").append(feedbackPhone.getText().toString());
            i.setData(Uri.parse("mailto:xuenanxu@gwmail.gwu.edu"));
            i.putExtra(Intent.EXTRA_SUBJECT, sb.toString());
            i.putExtra(Intent.EXTRA_SUBJECT, sb.toString());
            i.putExtra(Intent.EXTRA_TEXT, feedbackIssue.getText().toString());
            startActivity(i);
        } else {
            if (feedbackCategory.getSelectedItem().toString().equals(""))
                feedbackCategoryLayout.setError("Please Select a Category");
            Snackbar.make(v, "Please fill in the form correctly!", Snackbar.LENGTH_SHORT).show();
        }
    }
}
