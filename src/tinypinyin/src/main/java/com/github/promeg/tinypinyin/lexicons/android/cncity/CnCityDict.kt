package com.github.promeg.tinypinyin.lexicons.android.cncity

import android.content.Context
import com.github.promeg.tinypinyin.android.asset.lexicons.AndroidAssetDict

/**
 * Created by guyacong on 2016/12/23.
 */
internal class CnCityDict(context: Context?, private val assetFileName: String?) :
    AndroidAssetDict(context!!) {
    override fun assetFileName(): String? =
        if (assetFileName == null) "cncity.txt" else assetFileName

    companion object {
        @Volatile
        private var singleton: CnCityDict? = null
        fun getInstance(context: Context?, assetFileName: String?): CnCityDict {
            requireNotNull(context) { "context == null" }
            if (singleton == null) {
                synchronized(CnCityDict::class.java) {
                    if (singleton == null) {
                        singleton = CnCityDict(context, assetFileName)
                    }
                }
            }
            return singleton!!
        }
    }
}