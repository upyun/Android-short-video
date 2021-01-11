package org.lasque.tusdkvideodemo.views.cosmetic.panel;

import android.view.View;

import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticPanelController;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic.panel
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  14:21
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public abstract class BasePanel {

    public interface OnPanelClickListener{
        void onClear(CosmeticTypes.Types type);
        void onClose(CosmeticTypes.Types type);
        void onClick(CosmeticTypes.Types type);
    }

    protected CosmeticTypes.Types mType;

    protected View mPanel;

    protected CosmeticPanelController mController;

    protected OnPanelClickListener onPanelClickListener;

    protected BasePanel(CosmeticPanelController controller, CosmeticTypes.Types type){
        mType = type;
        mController = controller;
        mPanel = createView();
    }

    protected abstract View createView();

    public abstract void clear();

    public View getPanel(){
        return mPanel;
    }

    public void setOnPanelClickListener(OnPanelClickListener listener){
        this.onPanelClickListener = listener;
    }
}
