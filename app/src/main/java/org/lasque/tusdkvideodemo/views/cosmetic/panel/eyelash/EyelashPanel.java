package org.lasque.tusdkvideodemo.views.cosmetic.panel.eyelash;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticPanelController;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;
import org.lasque.tusdkvideodemo.views.cosmetic.OnItemClickListener;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.BasePanel;

import static org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes.Types.Eyelash;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic.panel.eyelash
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  15:31
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class EyelashPanel extends BasePanel {

    private CosmeticTypes.EyelashType mCurrentType;

    private EyelashAdapter mAdapter;

    public EyelashPanel(CosmeticPanelController controller) {
        super(controller, Eyelash);
    }

    @Override
    protected View createView() {
        final View panel = LayoutInflater.from(mController.getContext()).inflate(R.layout.cosmetic_eyelash_panel,null,false);
        ImageView putAway = panel.findViewById(R.id.lsq_eyelash_put_away);
        putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPanelClickListener != null) onPanelClickListener.onClose(mType);
            }
        });
        ImageView clear = panel.findViewById(R.id.lsq_eyelash_null);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        mAdapter = new EyelashAdapter(CosmeticPanelController.mEyelashTypes,mController.getContext());
        mAdapter.setOnItemClickListener(new OnItemClickListener<CosmeticTypes.EyelashType, EyelashAdapter.EyelashViewHolder>() {
            @Override
            public void onItemClick(int pos, EyelashAdapter.EyelashViewHolder holder, CosmeticTypes.EyelashType item) {
                mCurrentType = item;
                StickerGroup group = StickerLocalPackage.shared().getStickerGroup(item.mGroupId);
                if (group != null) {
                    mController.getEffect().updateEyelash(group.stickers.get(0));
                }
//                mController.getEffect().updateEyelash(StickerLocalPackage.shared().getStickerGroup(item.mGroupId).stickers.get(0));
                mAdapter.setCurrentPos(pos);
                if (onPanelClickListener != null) onPanelClickListener.onClick(mType);

            }
        });
        RecyclerView itemList = panel.findViewById(R.id.lsq_eyelash_item_list);
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
        mController.getEffect().closeEyelash();
        mAdapter.setCurrentPos(-1);
        if (onPanelClickListener != null) onPanelClickListener.onClear(mType);
    }
}
