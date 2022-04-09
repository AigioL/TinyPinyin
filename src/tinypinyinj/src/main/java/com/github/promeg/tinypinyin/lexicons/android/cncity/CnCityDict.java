package com.github.promeg.tinypinyin.lexicons.android.cncity;

import com.github.promeg.tinypinyin.android.asset.lexicons.AndroidAssetDict;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by guyacong on 2016/12/23.
 */
public final class CnCityDict extends AndroidAssetDict {

    public static final String defaultAssetFileName = "cncity.txt";
    static volatile CnCityDict singleton = null;

    String assetFileName;

    public CnCityDict(Context context, String assetFileName) {
        super(context);
        this.assetFileName = assetFileName;
    }

    @Override
    protected String assetFileName() {
        return TextUtils.isEmpty(assetFileName) ? defaultAssetFileName : assetFileName;
    }

    public static CnCityDict getInstance(Context context, String assetFileName) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (singleton == null) {
            synchronized (CnCityDict.class) {
                if (singleton == null) {
                    singleton = new CnCityDict(context, assetFileName);
                }
            }
        }
        return singleton;
    }
}
