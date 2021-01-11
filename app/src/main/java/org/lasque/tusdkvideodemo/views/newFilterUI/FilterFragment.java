package org.lasque.tusdkvideodemo.views.newFilterUI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.FilterGroup;
import org.lasque.tusdk.core.seles.tusdk.FilterOption;
import com.upyun.shortvideo.R;

import java.util.ArrayList;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

/**
 * TuSDK
 * TuSDKFilterEngineModule$
 *
 * @author H.ys
 * @Date 2020/07/07$ 17:29$
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class FilterFragment extends Fragment {

    private static final String FILTER_GROUP = "FilterGroup";

    public static FilterFragment newInstance(FilterGroup group) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILTER_GROUP, group);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FilterGroup mFilterGroup;
    private FilterOptionRecyclerAdapter mFilterAdapter;
    private RecyclerView mFilterView;

    private OnFilterItemClickListener mListener;
    private int previewPosition = -1;

    public void setOnFilterItemClickListener(OnFilterItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnFilterItemClickListener {
        void onFilterItemClick(String code, int positon);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(TuSdkContext.getLayoutResId("tusdk_filter_fragment_layout"), null, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mFilterGroup = (FilterGroup) bundle.getSerializable(FILTER_GROUP);
            getFilterView(view);
        }
        return view;
    }

    private void getFilterView(View view) {
        mFilterView = view.findViewById(R.id.lsq_filter_recycler_view);
        mFilterAdapter = new FilterOptionRecyclerAdapter(new ArrayList<FilterOption>(mFilterGroup.filters));
        mFilterAdapter.setItemCilckListener(mFilterClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(HORIZONTAL);
        mFilterView.setLayoutManager(layoutManager);
        mFilterView.setAdapter(mFilterAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private FilterOptionRecyclerAdapter.ItemClickListener mFilterClickListener = new FilterOptionRecyclerAdapter.ItemClickListener() {


        @Override
        public void onItemClick(int position) {
            if (previewPosition == position) {
                mFilterAdapter.changeShowParameterState();
            }
            previewPosition = position;
            String code = mFilterAdapter.getFilterCode(position);
            mFilterAdapter.setCurrentPosition(position);
            if (mListener != null) mListener.onFilterItemClick(code, position);
        }
    };

    public void removeFilter() {
        previewPosition = -1;
        mFilterAdapter.setCurrentPosition(-1);
    }

    public void setCurrentPosition(int position) {
        previewPosition = position;
        if (mFilterAdapter != null)
            mFilterAdapter.setCurrentPosition(position);

        if (mFilterView != null && position != -1) {
            mFilterView.scrollToPosition(position);
        }
    }

}
