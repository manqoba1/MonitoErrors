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
import android.widget.TextView;

import com.boha.monitor.library.R;
import com.com.boha.monitor.library.adapters.ErrorStatusAdapter;
import com.com.boha.monitor.library.dto.ErrorStoreDTO;
import com.com.boha.monitor.library.dto.ResponseDTO;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.Util;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ErrorStoreListFragment.OnFragmentErrorStoreListener} interface
 * to handle interaction events.
 * Use the {@link ErrorStoreListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorStoreListFragment extends Fragment implements PageFragment {
    private OnFragmentErrorStoreListener mListener;
    private AbsListView mListView;
    private TextView txtErrorCount, txtLabel;

    public static ErrorStoreListFragment newInstance(ErrorStoreDTO r) {
        ErrorStoreListFragment fragment = new ErrorStoreListFragment();
        Bundle args = new Bundle();
        args.putSerializable("response", r);
        fragment.setArguments(args);
        return fragment;
    }

    public ErrorStoreListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_error_store_list, container, false);
        ctx = getActivity();
        topView = v.findViewById(R.id.ER_LIST_layoutx);
        Bundle b = getArguments();
        if (b != null) {
            ResponseDTO r = (ResponseDTO) b.getSerializable("response");
            errorStores = r.getErrorStoreList();
        }
        mListView = (AbsListView) v.findViewById(android.R.id.list);
        adapter = new ErrorStatusAdapter(ctx,R.layout.error_store_item,errorStores);
        mListView.setAdapter(adapter);
        txtLabel = (TextView) v.findViewById(R.id.ER_LIST_label);
        Statics.setRobotoFontBold(ctx, txtLabel);
        txtErrorCount = (TextView) v.findViewById(R.id.ER_LIST_ErrorCount);
        setTotals();
        return v;
    }

    private void setTotals() {
        txtErrorCount.setText("" + errorStores.size());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        animateCounts();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(ErrorStatusAdapter uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentErrorStoreListener) activity;
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
        Util.animateScaleY(txtErrorCount,500);

    }


    public interface OnFragmentErrorStoreListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(ErrorStatusAdapter statusAdapter);
    }

    private List<ErrorStoreDTO> errorStores;
    private ErrorStatusAdapter adapter;
}
