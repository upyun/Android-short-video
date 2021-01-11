package org.lasque.tusdkvideodemo.views.cosmetic;

import android.graphics.Color;

import org.lasque.tusdk.core.seles.tusdk.cosmetic.CosmeticLipFilter.CosmeticLipType;
import com.upyun.shortvideo.R;

import static org.lasque.tusdk.core.seles.tusdk.cosmetic.CosmeticLipFilter.CosmeticLipType.COSMETIC_SHUIRUN_TYPE;
import static org.lasque.tusdk.core.seles.tusdk.cosmetic.CosmeticLipFilter.CosmeticLipType.COSMETIC_WUMIAN_TYPE;
import static org.lasque.tusdk.core.seles.tusdk.cosmetic.CosmeticLipFilter.CosmeticLipType.COSMETIC_ZIRUN_TYPE;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/15  10:33
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class CosmeticTypes {
    private CosmeticTypes() {
    }

    public enum Types {
        Lipstick(R.drawable.makeup_lipstick_ic, R.string.lsq_cosmetic_lipstick),
        Blush(R.drawable.makeup_blush_ic, R.string.lsq_cosmetic_blush),
        Eyebrow(R.drawable.makeup_eyebrow_ic, R.string.lsq_cosmetic_eyebrow),
        Eyeshadow(R.drawable.makeup_eyeshadow_ic, R.string.lsq_cosmetic_eyeshadow),
        Eyeliner(R.drawable.makeup_eyeliner_ic, R.string.lsq_cosmetic_eyeliner),
        Eyelash(R.drawable.makeup_eyelash_ic, R.string.lsq_cosmetic_eyelash);


        public int mIconId;
        public int mTitleId;

        Types(int iconId, int titleId) {
            mIconId = iconId;
            mTitleId = titleId;
        }
    }

    public enum LipstickType {
        MAC_CHILI(178, 48, 41, R.raw.lipstick_1, R.string.lsq_lipstick_0),
        DIOR_999(194, 3, 13, R.raw.lipstick_2, R.string.lsq_lipstick_1),
        ARMANI_201(106, 5, 0, R.raw.lipstick_3, R.string.lsq_lipstick_2),
        TOM_FORD_16(160, 33, 18, R.raw.lipstick_4, R.string.lsq_lipstick_3),
        GIVENCHY_317(239, 93, 71, R.raw.lipstick_5, R.string.lsq_lipstick_4),
        YSL_13(191, 27, 28, R.raw.lipstick_6, R.string.lsq_lipstick_5),
        Charlotte_Tilbury_kissing_Coachella_Coral(242, 112, 112, R.raw.lipstick_7, R.string.lsq_lipstick_6),
        YSL_402(208, 10, 57, R.raw.lipstick_8, R.string.lsq_lipstick_7),
        MAC_Lollipop(106, 18, 45, R.raw.lipstick_9, R.string.lsq_lipstick_8),
        KIKO_123(132, 40, 82, R.raw.lipstick_10, R.string.lsq_lipstick_9),
        CHANEL_53(229, 140, 122, R.raw.lipstick_11, R.string.lsq_lipstick_10),
        LANCOME_274(255, 190, 181, R.raw.lipstick_12, R.string.lsq_lipstick_11),
        NARS_CHELSEA_GIRLS(183, 128, 115, R.raw.lipstick_13, R.string.lsq_lipstick_12),
        ;
        public int mColor;
        public int mIconId;
        public int mTitleId;

        private LipstickType(int R, int G, int B, int iconId, int titleId) {
            mColor = Color.rgb(R, G, B);
            mIconId = iconId;
            mTitleId = titleId;
        }
    }

    public enum LipstickState {
        Matte(COSMETIC_WUMIAN_TYPE,R.drawable.lipstick_matte_ic,R.string.lsq_lipstick_matte),
        Moisturizing(COSMETIC_SHUIRUN_TYPE,R.drawable.lipstick_water_ic,R.string.lsq_lipstick_moisturizing),
        Moisturize(COSMETIC_ZIRUN_TYPE,R.drawable.lipstick_moist_ic,R.string.lsq_lipstick_moisturize);

        public CosmeticLipType mType;
        public int mIconId;
        public int mTitleId;

        LipstickState(CosmeticLipType type,int iconId,int titleId) {
            this.mType = type;
            this.mIconId = iconId;
            this.mTitleId = titleId;
        }

    }

    public enum EyelashType {
        A_01(R.string.lsq_eyelash_0, R.raw.eyelash_a_01, 1990),
        B_01(R.string.lsq_eyelash_1, R.raw.eyelash_b_01, 1991),
        C_01(R.string.lsq_eyelash_2, R.raw.eyelash_c_01, 1992),
        D_01(R.string.lsq_eyelash_3, R.raw.eyelash_d_01, 1993),
        E_01(R.string.lsq_eyelash_4, R.raw.eyelash_e_01, 1994),
        F_01(R.string.lsq_eyelash_5, R.raw.eyelash_f_01, 1995),
        G_01(R.string.lsq_eyelash_6, R.raw.eyelash_g_01, 1996),
        H_01(R.string.lsq_eyelash_7, R.raw.eyelash_h_01, 1997),
        I_01(R.string.lsq_eyelash_8, R.raw.eyelash_i_01, 1998),
        J_01(R.string.lsq_eyelash_9, R.raw.eyelash_j_01, 1999),
        K_01(R.string.lsq_eyelash_10, R.raw.eyelash_k_01, 2000),
        L_01(R.string.lsq_eyelash_11, R.raw.eyelash_l_01, 2001),
        M_01(R.string.lsq_eyelash_12, R.raw.eyelash_m_01, 2002),
        N_01(R.string.lsq_eyelash_13, R.raw.eyelash_n_01, 2003),
        O_01(R.string.lsq_eyelash_14, R.raw.eyelash_o_01, 2004),
        P_01(R.string.lsq_eyelash_15, R.raw.eyelash_p_01, 2005),
        Q_01(R.string.lsq_eyelash_16, R.raw.eyelash_q_01, 2006),
        R_01(R.string.lsq_eyelash_17, R.raw.eyelash_r_01, 2007),
        S_01(R.string.lsq_eyelash_18, R.raw.eyelash_s_01, 2008),
        T_01(R.string.lsq_eyelash_19, R.raw.eyelash_t_01, 2009),
        U_01(R.string.lsq_eyelash_20, R.raw.eyelash_u_01, 2010),
        V_01(R.string.lsq_eyelash_21, R.raw.eyelash_v_01, 2011),
        W_01(R.string.lsq_eyelash_22, R.raw.eyelash_w_01, 2012),
        X_01(R.string.lsq_eyelash_23, R.raw.eyelash_x_01, 2013),
        Y_01(R.string.lsq_eyelash_24, R.raw.eyelash_y_01, 2014),
        Z_01(R.string.lsq_eyelash_25, R.raw.eyelash_z_01, 2015),
        ZA_01(R.string.lsq_eyelash_26, R.raw.eyelash_za_01, 2016),
        ZB_01(R.string.lsq_eyelash_27, R.raw.eyelash_zb_01, 2017),
        ZC_01(R.string.lsq_eyelash_28, R.raw.eyelash_zc_01, 2018),
        ;
        public int mTitleId;
        public int mIconId;
        public long mGroupId;

        private EyelashType(int titleId, int iconId, long groupId) {
            mTitleId = titleId;
            mIconId = iconId;
            mGroupId = groupId;
        }
    }

    public enum EyebrowType {
        Normal_Black(R.string.lsq_eyebrow_normal_black, R.raw.eyebrow_1_normal_black_a, R.raw.eyebrow_1_normal_black_b, 1863, 1866),
        Normal_Gray(R.string.lsq_eyebrow_normal_gray, R.raw.eyebrow_1_normal_gray_a, R.raw.eyebrow_1_normal_gray_b, 1865, 1868),
        Normal_Brown(R.string.lsq_eyebrow_normal_brown, R.raw.eyebrow_1_normal_brown_a, R.raw.eyebrow_1_normal_brown_b, 1864, 1867),
        Willow_Black(R.string.lsq_eyebrow_willow_black, R.raw.eyebrow_2_willow_black_a, R.raw.eyebrow_2_willow_black_b, 1869, 1872),
        Willow_Gray(R.string.lsq_eyebrow_willow_gray, R.raw.eyebrow_2_willow_gray_a, R.raw.eyebrow_2_willow_gray_b, 1871, 1874),
        Willow_Brown(R.string.lsq_eyebrow_willow_brown, R.raw.eyebrow_2_willow_brown_a, R.raw.eyebrow_2_willow_brown_b, 1870, 1873),
        Leaf_Black(R.string.lsq_eyebrow_leaf_black, R.raw.eyebrow_3_leaf_black_a, R.raw.eyebrow_3_leaf_black_b, 1875, 1878),
        Leaf_Gray(R.string.lsq_eyebrow_leaf_gray, R.raw.eyebrow_3_leaf_gray_a, R.raw.eyebrow_3_leaf_gray_b, 1877, 1880),
        Leaf_Brown(R.string.lsq_eyebrow_leaf_brown, R.raw.eyebrow_3_leaf_brown_a, R.raw.eyebrow_3_leaf_brown_b, 1876, 1879),
        Peak_Black(R.string.lsq_eyeborw_peak_black, R.raw.eyebrow_4_peak_black_a, R.raw.eyebrow_4_peak_black_b, 1881, 1884),
        Peak_Gray(R.string.lsq_eyeborw_peak_gray, R.raw.eyebrow_4_peak_gray_a, R.raw.eyebrow_4_peak_gray_b, 1883, 1886),
        Peak_Brown(R.string.lsq_eyeborw_peak_brown, R.raw.eyebrow_4_peak_brown_a, R.raw.eyebrow_4_peak_brown_b, 1882, 1885),
        Rough_Black(R.string.lsq_eyeborw_rough_black, R.raw.eyebrow_6_rough_black_a, R.raw.eyebrow_5_rough_black_b, 1887, 1890),
        Rough_Gray(R.string.lsq_eyeborw_rough_black, R.raw.eyebrow_6_rough_black_a, R.raw.eyebrow_5_rough_black_b, 1889, 1892),
        Rough_Brown(R.string.lsq_eyeborw_rough_black, R.raw.eyebrow_6_rough_black_a, R.raw.eyebrow_5_rough_black_b, 1888, 1891),
        Curved_Black(R.string.lsq_eyebrow_curved_black, R.raw.eyebrow_7_curved_black_a, R.raw.eyebrow_7_curved_black_b, 1893, 1896),
        Curved_Gray(R.string.lsq_eyebrow_curved_gray, R.raw.eyebrow_7_curved_gray_a, R.raw.eyebrow_7_curved_gray_b, 1895, 1898),
        Curved_Brown(R.string.lsq_eyebrow_curved_brown, R.raw.eyebrow_7_curved_brown_a, R.raw.eyebrow_7_curved_brown_b, 1894, 1897),
        Barbie_Black(R.string.lsq_eyeborw_barbie_black, R.raw.eyebrow_8_barbie_black_a, R.raw.eyebrow_8_barbie_black_b, 1899, 1902),
        Barbie_Gray(R.string.lsq_eyeborw_barbie_gray, R.raw.eyebrow_8_barbie_gray_a, R.raw.eyebrow_8_barbie_gray_b, 1901, 1904),
        Barbie_Brown(R.string.lsq_eyeborw_barbie_brown, R.raw.eyebrow_8_barbie_brown_a, R.raw.eyebrow_8_barbie_brown_b, 1900, 1903),
        Peach_Black(R.string.lsq_eyeborw_peach_black, R.raw.eyebrow_9_peach_black_a, R.raw.eyebrow_9_peach_black_b, 1905, 1908),
        Peach_Gray(R.string.lsq_eyeborw_peach_gray, R.raw.eyebrow_9_peach_gray_a, R.raw.eyebrow_9_peach_gray_b, 1907, 1910),
        Peach_Brown(R.string.lsq_eyeborw_peach_brown, R.raw.eyebrow_9_peach_brown_a, R.raw.eyebrow_9_peach_brown_b, 1906, 1909),
        Moon_Black(R.string.lsq_eyeborw_moon_black, R.raw.eyebrow_10_new_moon_black_a, R.raw.eyebrow_10_new_moon_black_b, 1911, 1914),
        Moon_Gray(R.string.lsq_eyeborw_moon_gray, R.raw.eyebrow_10_new_moon_gray_a, R.raw.eyebrow_10_new_moon_gray_b, 1913, 1916),
        Moon_Brown(R.string.lsq_eyeborw_moon_brown, R.raw.eyebrow_10_new_moon_brown_a, R.raw.eyebrow_10_new_moon_brown_b, 1912, 1915),
        Broken_Black(R.string.lsq_eyebrow_broken_black, R.raw.eyebrow_11_broken_black_a, R.raw.eyebrow_11_broken_black_b, 1917, 1921),
        Broken_Gray(R.string.lsq_eyebrow_broken_gray, R.raw.eyebrow_11_broken_gray_a, R.raw.eyebrow_11_broken_gray_b, 1919, 1922),
        Broken_Brown(R.string.lsq_eyebrow_broken_brown, R.raw.eyebrow_11_broken_brown_a, R.raw.eyebrow_11_broken_brown_b, 1918, 1920),
        Wild_Black(R.string.lsq_eyeborw_wild_black, R.raw.eyebrow_12_wild_black_a, R.raw.eyebrow_12_wild_black_b, 1923, 1926),
        Wild_Gray(R.string.lsq_eyeborw_wild_gray, R.raw.eyebrow_12_wild_gray_a, R.raw.eyebrow_12_wild_gray_b, 1925, 1928),
        Wild_Brown(R.string.lsq_eyeborw_wild_brown, R.raw.eyebrow_12_wild_brown_a, R.raw.eyebrow_12_wild_brown_b, 1924, 1927),
        European_Black(R.string.lsq_eyebrow_european_black, R.raw.eyebrow_13_european_black_a, R.raw.eyebrow_13_european_black_b, 1929, 1932),
        European_Gray(R.string.lsq_eyebrow_european_gray, R.raw.eyebrow_13_european_gray_a, R.raw.eyebrow_13_european_gray_b, 1931, 1934),
        European_Brown(R.string.lsq_eyebrow_european_brown, R.raw.eyebrow_13_european_brown_a, R.raw.eyebrow_13_european_brown_b, 1930, 1933),
        Round_Black(R.string.lsq_eyebrow_round_black, R.raw.eyebrow_13_round_black_a, R.raw.eyebrow_13_round_black_b, 1935, 1939),
        Round_Gray(R.string.lsq_eyebrow_round_gray, R.raw.eyebrow_13_round_gray_a, R.raw.eyebrow_13_round_gray_b, 1937, 1940),
        Round_Brown(R.string.lsq_eyebrow_round_brown, R.raw.eyebrow_13_round_brown_a, R.raw.eyebrow_13_round_brown_b, 1936, 1938),
        Yanxi_Black(R.string.lsq_eyebrow_yanxi_black, R.raw.eyebrow_14_yanxi_black_a, R.raw.eyebrow_14_yanxi_black_b, 1941, 1944),
        Yanxi_Gray(R.string.lsq_eyebrow_yanxi_gray, R.raw.eyebrow_14_yanxi_gray_a, R.raw.eyebrow_14_yanxi_gray_b, 1943, 1946),
        Yanxi_Brown(R.string.lsq_eyebrow_yanxi_brown, R.raw.eyebrow_14_yanxi_brown_a, R.raw.eyebrow_14_yanxi_brown_b, 1942, 1945),
        ;

        public int mTitleId;
        public int mMistIconId;
        public int mMistyIconId;
        public long mMistGroupId;
        public long mMistyGroupId;

        private EyebrowType(int titleId, int mistIconId, int mistyIconId, long mistGroupId, long mistyGroupId) {
            mTitleId = titleId;
            mMistIconId = mistIconId;
            mMistyIconId = mistyIconId;
            mMistGroupId = mistGroupId;
            mMistyGroupId = mistyGroupId;
        }
    }

    public enum EyebrowState {
        MistEyebrow(R.drawable.eyebrow_fog_ic,R.string.lsq_eyebrow_fog),
        MistyBrow(R.drawable.eyebrow_root_ic,R.string.lsq_eyebrow_root);

        public int mIconId;
        public int mTitleId;

        EyebrowState(int iconId,int titleId){
            this.mIconId = iconId;
            this.mTitleId = titleId;
        }
    }

    public enum BlushType {
        A(R.string.lsq_blush_a, R.raw.blush_a_01, 2024),
        B(R.string.lsq_blush_b, R.raw.blush_b_01, 2025),
        C(R.string.lsq_blush_c, R.raw.blush_c_01, 2026),
        D(R.string.lsq_blush_d, R.raw.blush_d_01, 2027),
        E(R.string.lsq_blush_e, R.raw.blush_e_01, 2028),
        F(R.string.lsq_blush_f, R.raw.blush_f_01, 2029),
        G(R.string.lsq_blush_g, R.raw.blush_g_01, 2030),
        H(R.string.lsq_blush_h, R.raw.blush_h_01, 2031),
        I(R.string.lsq_blush_i, R.raw.blush_i_01, 2033),
        J(R.string.lsq_blush_j, R.raw.blush_j_01, 2034),
        K(R.string.lsq_blush_k, R.raw.blush_k_01, 2035),
        L(R.string.lsq_blush_l, R.raw.blush_l_01, 2036),
        M(R.string.lsq_blush_m, R.raw.blush_m_01, 2037),
        N(R.string.lsq_blush_n, R.raw.blush_n_01, 2039),
        P(R.string.lsq_blush_p, R.raw.blush_o_01, 2041),
        X(R.string.lsq_blush_x, R.raw.blush_p_01, 2043),
        ;
        public int mTitleId;
        public int mIconId;
        public long mGroupId;

        private BlushType(int titleId, int iconId, long groupId) {
            mTitleId = titleId;
            mIconId = iconId;
            mGroupId = groupId;
        }
    }

    public enum EyeshadowType {
        A_01(R.string.lsq_eyeshadow_0, R.raw.eyeshadow_a, 1947),
        B_01(R.string.lsq_eyeshadow_1, R.raw.eyeshadow_b, 1948),
        C_01(R.string.lsq_eyeshadow_2, R.raw.eyeshadow_c, 1949),
        D_01(R.string.lsq_eyeshadow_3, R.raw.eyeshadow_d, 1950),
        E_01(R.string.lsq_eyeshadow_4, R.raw.eyeshadow_e, 1951),
        F_01(R.string.lsq_eyeshadow_5, R.raw.eyeshadow_f, 1952),
        G_01(R.string.lsq_eyeshadow_6, R.raw.eyeshadow_g, 1953),
        H_01(R.string.lsq_eyeshadow_7, R.raw.eyeshadow_h, 1954),
        I_01(R.string.lsq_eyeshadow_8, R.raw.eyeshadow_i, 1955),
        J_01(R.string.lsq_eyeshadow_9, R.raw.eyeshadow_j, 1956),
        K_01(R.string.lsq_eyeshadow_10, R.raw.eyeshadow_k, 1957),
        L_01(R.string.lsq_eyeshadow_11, R.raw.eyeshadow_l, 1958),
        M_01(R.string.lsq_eyeshadow_12, R.raw.eyeshadow_m, 1959),
        N_01(R.string.lsq_eyeshadow_13, R.raw.eyeshadow_n, 1960),
        O_01(R.string.lsq_eyeshadow_14, R.raw.eyeshadow_o, 1961),
        P_01(R.string.lsq_eyeshadow_15, R.raw.eyeshadow_p, 1962),
        Q_01(R.string.lsq_eyeshadow_16, R.raw.eyeshadow_q, 1963),
        R_01(R.string.lsq_eyeshadow_17, R.raw.eyeshadow_r, 1964),
        S_01(R.string.lsq_eyeshadow_18, R.raw.eyeshadow_s, 1965),
        T_01(R.string.lsq_eyeshadow_19, R.raw.eyeshadow_t, 1966),
        U_01(R.string.lsq_eyeshadow_20, R.raw.eyeshadow_u, 1967),
        V_01(R.string.lsq_eyeshadow_21, R.raw.eyeshadow_v, 1968),
        W_01(R.string.lsq_eyeshadow_22, R.raw.eyeshadow_w, 1969),
        ;
        public int mTitleId;
        public int mIconId;
        public long mGroupId;

        private EyeshadowType(int titleId, int iconId, long groupId) {
            mTitleId = titleId;
            mIconId = iconId;
            mGroupId = groupId;
        }
    }

    public enum EyelinerType {
        A_01(R.string.lsq_eyeline_0, R.raw.eyeline_a, 1974),
        B_01(R.string.lsq_eyeline_1, R.raw.eyeline_b, 1975),
        C_01(R.string.lsq_eyeline_2, R.raw.eyeline_c, 1976),
        D_01(R.string.lsq_eyeline_3, R.raw.eyeline_d, 1977),
        E_01(R.string.lsq_eyeline_4, R.raw.eyeline_e, 1978),
        F_01(R.string.lsq_eyeline_5, R.raw.eyeline_f, 1979),
        G_01(R.string.lsq_eyeline_6, R.raw.eyeline_g, 1980),
        H_01(R.string.lsq_eyeline_7, R.raw.eyeline_h, 1981),
        I_01(R.string.lsq_eyeline_8, R.raw.eyeline_i, 1982),
        J_01(R.string.lsq_eyeline_9, R.raw.eyeline_j, 1983),
        K_01(R.string.lsq_eyeline_10, R.raw.eyeline_k, 1984),
        L_01(R.string.lsq_eyeline_11, R.raw.eyeline_l, 1985),
        M_01(R.string.lsq_eyeline_12, R.raw.eyeline_m, 1986),
        N_01(R.string.lsq_eyeline_13, R.raw.eyeline_n, 1987),
        O_01(R.string.lsq_eyeline_14, R.raw.eyeline_o, 1988),
        P_01(R.string.lsq_eyeline_15, R.raw.eyeline_p, 1989),
        ;
        public int mTitleId;
        public int mIconId;
        public long mGroupId;

        private EyelinerType(int titleId, int iconId, long groupId) {
            mTitleId = titleId;
            mIconId = iconId;
            mGroupId = groupId;
        }
    }


}
