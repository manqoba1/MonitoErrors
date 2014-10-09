package com.com.boha.monitor.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.boha.monitor.library.R;
import com.com.boha.monitor.library.adapters.ErrorAndroidAdapter;
import com.com.boha.monitor.library.dto.ErrorStoreAndroidDTO;
import com.com.boha.monitor.library.dto.ResponseDTO;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.Util;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.com.boha.monitor.library.fragments.ProjectListErrorFragment.ProjectErrorListListener} interface
 * to handle interaction events.
 * Use the {@link ProjectListErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectListErrorFragment extends Fragment implements PageFragment {

    private ProjectErrorListListener mListener;
    private AbsListView mListView;
    private TextView txtErrorCount, txtLabel;

    public static ProjectListErrorFragment newInstance(ResponseDTO r) {
        ProjectListErrorFragment fragment = new ProjectListErrorFragment();
        Bundle args = new Bundle();
        args.putSerializable("response", r);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectListErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Context ctx;
    View topView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_list_error, container, false);
        ctx = getActivity();
        topView = view.findViewById(R.id.ER_LIST_layoutx);
        Bundle b = getArguments();
        if (b != null) {
            ResponseDTO r = (ResponseDTO) b.getSerializable("response");

            projectErrorList = r.getErrorStoreAndroidList();
        }
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        adapter = new ErrorAndroidAdapter(ctx, R.layout.project_error_item, projectErrorList);
        mListView.setAdapter(adapter);
        txtLabel = (TextView) view.findViewById(R.id.ER_LIST_label);
        Statics.setRobotoFontBold(ctx, txtLabel);
        txtErrorCount = (TextView) view.findViewById(R.id.ER_LIST_ErrorCount);
        setTotals();
        return view;
    }

    private void setTotals() {

        txtErrorCount.setText("" + projectErrorList.size());
      /*  try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        animateCounts();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ProjectErrorListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void animateCounts() {

        Util.animateScaleX(txtErrorCount, 500);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ProjectErrorListListener {
        public void onErrorClicked(ErrorAndroidAdapter project);
    }

    private List<ErrorStoreAndroidDTO> projectErrorList;
    private ErrorAndroidAdapter adapter;


}
