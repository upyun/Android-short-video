package org.lasque.tusdkvideodemo.views.cosmetic.panel.eyebrow;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticPanelController;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;
import org.lasque.tusdkvideodemo.views.cosmetic.OnItemClickListener;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.BasePanel;

import static org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes.EyebrowState.MistEyebrow;
import static org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes.EyebrowState.MistyBrow;
import static org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes.Types.Eyebrow;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic.panel.eyebrow
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  16:33
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class EyebrowPanel extends BasePanel {

    private CosmeticTypes.EyebrowState mCurrentState = MistEyebrow;
    private CosmeticTypes.EyebrowType mCurrentType;
    private EyebrowAdapter mAdapter;


    public EyebrowPanel(CosmeticPanelController controller) {
        super(controller, Eyebrow);
    }

    @Override
    protected View createView() {
        final View panel = LayoutInflater.from(mController.getContext()).inflate(R.layout.cosmetic_eyebrow_panel, null,false);
        final ImageView stateIcon = panel.findViewById(R.id.lsq_eyebrow_state_icon);
        final TextView stateTitle = panel.findViewById(R.id.lsq_eyebrow_state_title);
        stateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentState) {
                    case MistEyebrow:
                        mCurrentState = MistyBrow;
                        break;
                    case MistyBrow:
                        mCurrentState = MistEyebrow;
                        break;
                }
                stateIcon.setImageResource(mCurrentState.mIconId);
                stateTitle.setText(mCurrentState.mTitleId);
                mAdapter.setState(mCurrentState);
                if (mCurrentType == null) return;
                CosmeticTypes.EyebrowType item = mCurrentType;
                long currentGroupId = -1;
                switch (mCurrentState) {
                    case MistEyebrow:
                        currentGroupId = item.mMistGroupId;
                        break;
                    case MistyBrow:
                        currentGroupId = item.mMistyGroupId;
                        break;
                }
                mController.getEffect().updateEyebrow(StickerLocalPackage.shared().getStickerGroup(currentGroupId).stickers.get(0));
            }
        });
        ImageView putAway = panel.findViewById(R.id.lsq_eyebrow_put_away);
        putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPanelClickListener != null) onPanelClickListener.onClose(mType);
            }
        });
        ImageView clear = panel.findViewById(R.id.lsq_eyebrow_null);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mAdapter = new EyebrowAdapter(CosmeticPanelController.mEyebrowTypes, mController.getContext());
        mAdapter.setOnItemClickListener(new OnItemClickListener<CosmeticTypes.EyebrowType, EyebrowAdapter.EyebrowViewHolder>() {
            @Override
            public void onItemClick(int pos, EyebrowAdapter.EyebrowViewHolder holder, CosmeticTypes.EyebrowType item) {
                mCurrentType = item;
                long currentGroupId = -1;
                switch (mCurrentState) {
                    case MistEyebrow:
                        currentGroupId = item.mMistGroupId;
                        break;
                    case MistyBrow:
                        currentGroupId = item.mMistyGroupId;
                        break;
                }
                mController.getEffect().updateEyebrow(StickerLocalPackage.shared().getStickerGroup(currentGroupId).stickers.get(0));
                mAdapter.setCurrentPos(pos);
                if (onPanelClickListener != null) onPanelClickListener.onClick(mType);

            }
        });
        RecyclerView itemList = panel.findViewById(R.id.lsq_eyebrow_item_list);
        LinearLayoutManager manager = new LinearLayoutManager(mController.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemList.setLayoutManager(manager);
        itemList.setAdapter(mAdapter);
        itemList.setNestedScrollingEnabled(false);
        return panel;
    }

    @Override
    public void clear() {
        mController.getEffect().closeEyebrow();
        mAdapter.setCurrentPos(-1);
        mCurrentType = null;
        if (onPanelClickListener != null) onPanelClickListener.onClear(mType);
    }
}
