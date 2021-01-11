package org.lasque.tusdkvideodemo.views.cosmetic.panel.lipstick;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticPanelController;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;
import org.lasque.tusdkvideodemo.views.cosmetic.OnItemClickListener;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.BasePanel;

import static org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes.Types.Lipstick;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic.panel
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  14:42
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class LipstickPanel extends BasePanel {

    private CosmeticTypes.LipstickState mCurrentState = CosmeticTypes.LipstickState.Moisturizing;
    private CosmeticTypes.LipstickType mCurrentType;
    private LipstickAdapter mAdapter;

    public LipstickPanel(CosmeticPanelController controller) {
        super(controller, Lipstick);
    }

    @Override
    protected View createView() {
        View panel = LayoutInflater.from(mController.getContext()).inflate(R.layout.cosmetic_lipstick_panel,null,false);
        final ImageView stateIcon = panel.findViewById(R.id.lsq_lipstick_state_icon);
        final TextView stateTitle = panel.findViewById(R.id.lsq_lipstick_state_title);
        stateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentState){
                    case Matte:
                        mCurrentState = CosmeticTypes.LipstickState.Moisturizing;
                        break;
                    case Moisturizing:
                        mCurrentState = CosmeticTypes.LipstickState.Moisturize;
                        break;
                    case Moisturize:
                        mCurrentState = CosmeticTypes.LipstickState.Matte;
                        break;
                }
                stateIcon.setImageResource(mCurrentState.mIconId);
                stateTitle.setText(mCurrentState.mTitleId);
                if (mCurrentType == null) return;
                mController.getEffect().updateLip(mCurrentState.mType,mCurrentType.mColor);
            }
        });

        final ImageView putAway = panel.findViewById(R.id.lsq_lipstick_put_away);
        putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPanelClickListener != null) onPanelClickListener.onClose(mType);
            }
        });

        final ImageView clearLips = panel.findViewById(R.id.lsq_lipstick_null);
        clearLips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mAdapter = new LipstickAdapter(CosmeticPanelController.mLipstickTypes,mController.getContext());
        mAdapter.setOnItemClickListener(new OnItemClickListener<CosmeticTypes.LipstickType, LipstickAdapter.LipstickViewHolder>() {
            @Override
            public void onItemClick(int pos, LipstickAdapter.LipstickViewHolder holder, CosmeticTypes.LipstickType item) {
                mCurrentType = item;
                mController.getEffect().updateLip(mCurrentState.mType,mCurrentType.mColor);
                mAdapter.setCurrentPos(pos);
                if (onPanelClickListener != null) onPanelClickListener.onClick(mType);

            }
        });
        RecyclerView itemList = panel.findViewById(R.id.lsq_lipstick_item_list);
        LinearLayoutManager manager = new LinearLayoutManager(mController.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemList.setLayoutManager(manager);
        itemList.setAdapter(mAdapter);
        itemList.setNestedScrollingEnabled(false);
        return panel;
    }

    @Override
    public void clear() {
        mCurrentType = null;
        mController.getEffect().closeLip();
        mAdapter.setCurrentPos(-1);
        if (onPanelClickListener != null) onPanelClickListener.onClear(mType);
    }
}
