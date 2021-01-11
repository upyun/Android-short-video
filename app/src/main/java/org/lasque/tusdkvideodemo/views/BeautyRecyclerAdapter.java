package org.lasque.tusdkvideodemo.views;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import org.lasque.tusdk.video.editor.TuSdkMediaSkinFaceEffect;
import com.upyun.shortvideo.R;

import java.util.Arrays;
import java.util.List;

/**
 * 美颜
 *
 * @author xujie
 * @Date 2018/9/29
 */

public class BeautyRecyclerAdapter extends RecyclerView.Adapter<BeautyRecyclerAdapter.BeautyViewHolder> {

    private Context mContext;
    private List<String> mBeautyParams = Arrays.asList("skin");
    private boolean useSkinNatural = true;
    private boolean isReset = false;

    private String currentClickKey = "";

    private TuSdkMediaSkinFaceEffect.SkinFaceType mCurrentMode = TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty;

    public OnBeautyItemClickListener listener;

    public interface OnBeautyItemClickListener {
        void onChangeSkin(View v, String key, TuSdkMediaSkinFaceEffect.SkinFaceType skinMode);

        void onClear();
    }

    public void setOnSkinItemClickListener(OnBeautyItemClickListener onBeautyItemClickListener) {
        this.listener = onBeautyItemClickListener;
    }


    public BeautyRecyclerAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public BeautyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_recycler_skin_item_layout, viewGroup,false);
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = viewGroup.getWidth();
        layoutParams.height = viewGroup.getHeight();
        view.setLayoutParams(layoutParams);
        BeautyViewHolder viewHolder = new BeautyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BeautyViewHolder beautyViewHolder, final int position) {
        beautyViewHolder.resetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClear();
                beautyViewHolder.whiteningImage.setImageResource(R.drawable.lsq_ic_whitening_norl);
                beautyViewHolder.smoothingImage.setImageResource(R.drawable.lsq_ic_smoothing_norl);
                beautyViewHolder.ruddyImage.setImageResource(mCurrentMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? R.drawable.lsq_ic_ruddy_norl : R.drawable.ic_sharpen_norl);
//                beautyViewHolder.whiteningImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.smoothingImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.ruddyImage.setBackgroundResource(R.drawable.circle_bg_26);
                beautyViewHolder.whiteningName.setChecked(false);
                beautyViewHolder.smoothingName.setChecked(false);
                beautyViewHolder.ruddyName.setChecked(false);
                currentClickKey = "";
                isReset = true;
            }
        });
        switch (mCurrentMode){
            case SkinNatural:
                beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_precision_nor);
                beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_precision);
                beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_ruddy);
                break;
            case SkinMoist:
                beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_extreme_nor);
                beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_extreme);
                beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_ruddy);
                break;
            case Beauty:
                beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_new_normal);
                beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_beauty);
                beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_sharpen);
                break;
        }
        beautyViewHolder.skinBeautyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClickKey = "smoothing";

                if (isReset){
                    isReset = false;
                } else {
                    switch (mCurrentMode){
                        case SkinNatural:
                            mCurrentMode = TuSdkMediaSkinFaceEffect.SkinFaceType.SkinMoist;
                            break;
                        case SkinMoist:
                            mCurrentMode = TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty;
                            break;
                        case Beauty:
                            mCurrentMode = TuSdkMediaSkinFaceEffect.SkinFaceType.SkinNatural;
                            break;
                    }
                }

                switch (mCurrentMode){
                    case SkinNatural:
                        beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_precision_nor);
                        beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_precision);
                        beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_ruddy);
                        break;
                    case SkinMoist:
                        beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_extreme_nor);
                        beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_extreme);
                        beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_ruddy);
                        break;
                    case Beauty:
                        beautyViewHolder.skinBeautyImage.setImageResource(R.drawable.lsq_ic_skin_new_normal);
                        beautyViewHolder.skinBeautyName.setText(R.string.lsq_beauty_skin_beauty);
                        beautyViewHolder.ruddyName.setText(R.string.lsq_beauty_sharpen);
                        break;
                }
                if (listener != null) {
                    listener.onChangeSkin(v, currentClickKey, mCurrentMode);
                }

                beautyViewHolder.whiteningImage.setImageResource(R.drawable.lsq_ic_whitening_norl);
                beautyViewHolder.smoothingImage.setImageResource(R.drawable.lsq_ic_smoothing_sele);
                beautyViewHolder.ruddyImage.setImageResource(mCurrentMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? R.drawable.lsq_ic_ruddy_norl : R.drawable.ic_sharpen_norl);
//                beautyViewHolder.whiteningImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.smoothingImage.setBackgroundResource(R.drawable.circle_bg_22);
//                beautyViewHolder.ruddyImage.setBackgroundResource(R.drawable.circle_bg_26);
                beautyViewHolder.whiteningName.setChecked(false);
                beautyViewHolder.smoothingName.setChecked(true);
                beautyViewHolder.ruddyName.setChecked(false);
            }
        });
        beautyViewHolder.whiteningImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClickKey = "whitening";
                if (listener != null) listener.onChangeSkin(v, currentClickKey, mCurrentMode);

                beautyViewHolder.whiteningImage.setImageResource(R.drawable.lsq_ic_whitening_sele);
                beautyViewHolder.smoothingImage.setImageResource(R.drawable.lsq_ic_smoothing_norl);
                beautyViewHolder.ruddyImage.setImageResource(mCurrentMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? R.drawable.lsq_ic_ruddy_norl : R.drawable.ic_sharpen_norl);
//                beautyViewHolder.whiteningImage.setBackgroundResource(R.drawable.circle_bg_22);
//                beautyViewHolder.smoothingImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.ruddyImage.setBackgroundResource(R.drawable.circle_bg_26);
                beautyViewHolder.whiteningName.setChecked(true);
                beautyViewHolder.smoothingName.setChecked(false);
                beautyViewHolder.ruddyName.setChecked(false);
            }
        });
        beautyViewHolder.smoothingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClickKey = "smoothing";
                if (listener != null) listener.onChangeSkin(v, currentClickKey, mCurrentMode);

                beautyViewHolder.whiteningImage.setImageResource(R.drawable.lsq_ic_whitening_norl);
                beautyViewHolder.smoothingImage.setImageResource(R.drawable.lsq_ic_smoothing_sele);
                beautyViewHolder.ruddyImage.setImageResource(mCurrentMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? R.drawable.lsq_ic_ruddy_norl : R.drawable.ic_sharpen_norl);
//                beautyViewHolder.whiteningImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.smoothingImage.setBackgroundResource(R.drawable.circle_bg_22);
//                beautyViewHolder.ruddyImage.setBackgroundResource(R.drawable.circle_bg_26);
                beautyViewHolder.whiteningName.setChecked(false);
                beautyViewHolder.smoothingName.setChecked(true);
                beautyViewHolder.ruddyName.setChecked(false);
            }
        });
        beautyViewHolder.ruddyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClickKey = useSkinNatural ? "sharpen" : "ruddy";
                if (listener != null) listener.onChangeSkin(v, currentClickKey, mCurrentMode);

                beautyViewHolder.whiteningImage.setImageResource(R.drawable.lsq_ic_whitening_norl);
                beautyViewHolder.smoothingImage.setImageResource(R.drawable.lsq_ic_smoothing_norl);
                beautyViewHolder.ruddyImage.setImageResource(mCurrentMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? R.drawable.lsq_ic_ruddy_seel : R.drawable.ic_sharpen_select);
//                beautyViewHolder.whiteningImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.smoothingImage.setBackgroundResource(R.drawable.circle_bg_26);
//                beautyViewHolder.ruddyImage.setBackgroundResource(R.drawable.circle_bg_22);
                beautyViewHolder.whiteningName.setChecked(false);
                beautyViewHolder.smoothingName.setChecked(false);
                beautyViewHolder.ruddyName.setChecked(true);
            }
        });
        switch (currentClickKey) {
            case "whitening":
                beautyViewHolder.whiteningImage.callOnClick();
                break;
            case "smoothing":
                beautyViewHolder.smoothingImage.callOnClick();
                break;
            case "ruddy":
            case "sharpen":
                beautyViewHolder.ruddyImage.callOnClick();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mBeautyParams.size();
    }

    static class BeautyViewHolder extends RecyclerView.ViewHolder {

        public ImageView resetImage;

        public CheckedTextView skinBeautyName;
        public ImageView skinBeautyImage;

        public CheckedTextView whiteningName;
        public ImageView whiteningImage;
        public CheckedTextView smoothingName;
        public ImageView smoothingImage;
        public CheckedTextView ruddyName;
        public ImageView ruddyImage;

        public BeautyViewHolder(View itemView) {
            super(itemView);
            resetImage = itemView.findViewById(R.id.lsq_reset_image);

            skinBeautyName = itemView.findViewById(R.id.lsq_skin_beauty_name);
            skinBeautyImage = itemView.findViewById(R.id.lsq_skin_beauty_image);

            whiteningName = itemView.findViewById(R.id.lsq_whitening_name);
            whiteningImage = itemView.findViewById(R.id.lsq_whitening_image);
            smoothingName = itemView.findViewById(R.id.lsq_smoothing_name);
            smoothingImage = itemView.findViewById(R.id.lsq_smoothing_image);
            ruddyName = itemView.findViewById(R.id.lsq_ruddy_name);
            ruddyImage = itemView.findViewById(R.id.lsq_ruddy_image);
        }
    }
}
