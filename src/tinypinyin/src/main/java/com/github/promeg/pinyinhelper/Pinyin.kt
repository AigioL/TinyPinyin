package com.github.promeg.pinyinhelper

import android.content.Context
import com.github.promeg.pinyinhelper.Engine.toPinyin
import com.github.promeg.tinypinyin.android.asset.lexicons.AndroidAssetDict
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import org.ahocorasick.trie.Trie
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Created by guyacong on 2015/9/28.
 */
object Pinyin {
    private var mTrieDict: Trie? = null
    private var mSelector: SegmentationSelector? = null
    private var mPinyinDicts: MutableList<PinyinDict?>? = null

    val trieDict: Trie?
        get() = mTrieDict
    val selector: SegmentationSelector?
        get() = mSelector
    val pinyinDicts: MutableList<PinyinDict?>?
        get() = mPinyinDicts

    /**
     * 使用 [Pinyin.Config] 初始化Pinyin。
     *
     * @param config 相应的设置，传入null则会清空所有的词典
     */
    fun init(config: Config?) {
        if (config == null) {
            // 清空设置
            mPinyinDicts = null
            mTrieDict = null
            mSelector = null
            return
        }
        if (!config.valid()) {
            // 忽略无效Config
            return
        }
        mPinyinDicts = Collections.unmodifiableList(config.pinyinDicts!!)
        mTrieDict = Utils.dictsToTrie(config.pinyinDicts)
        mSelector = config.selector
    }

    /**
     * 向Pinyin中追加词典。
     *
     * 注意: 若有多个词典，推荐使用性能更优的 [Pinyin.init] 初始化Pinyin。
     *
     * @param dict 输入的词典
     */
    fun add(dict: PinyinDict?) {
        if (dict?.words() == null || dict.words()!!.isEmpty()) {
            // 无效字典
            return
        }
        init(Config(mPinyinDicts).with(dict))
    }

    /**
     * 返回新的[Pinyin.Config] 对象
     *
     * @return 新的Config对象
     */
    fun newConfig(): Config {
        return Config(null)
    }

    /**
     * 将输入字符串转为拼音，转换过程中会使用之前设置的用户词典，以字符为单位插入分隔符
     *
     * 例: "hello:中国"  在separator为","时，输出： "h,e,l,l,o,:,ZHONG,GUO,!"
     *
     * @param str  输入字符串
     * @param separator 分隔符
     * @return 中文转为拼音的字符串
     */
    fun toPinyin(str: String?, separator: String?): String? {
        return toPinyin(str, mTrieDict, mPinyinDicts, separator, mSelector)
    }

    /**
     * 将输入字符串转为拼音，转换过程中会使用之前设置的用户词典，以字符为单位插入分隔符
     *
     * 例: "hello:中国!"  在separator为","时，输出： "h,e,l,l,o,:,ZHONG,GUO,!"
     *
     * @param str  输入字符串
     * @param separator 分隔符
     * @param rules 自定义的规则，具有最高优先级
     * @return 中文转为拼音的字符串
     */
    fun toPinyin(str: String?, separator: String?, rules: PinyinRules?): String? {
        return if (rules != null) {
            val dicts: MutableList<PinyinDict?> = ArrayList<PinyinDict?>()
            dicts.add(rules.toPinyinMapDict())
            if (mPinyinDicts != null) {
                dicts.addAll(mPinyinDicts!!)
            }
            val config = Config(dicts)
            toPinyin(str, config, separator)
        } else {
            toPinyin(str, separator)
        }
    }

    /**
     * 将输入字符转为拼音
     *
     * @param c 输入字符
     * @return return pinyin if c is chinese in uppercase, String.valueOf(c) otherwise.
     */
    fun toPinyin(c: Char): String {
        return if (isChinese(c)) {
            if (c == PinyinData.CHAR_12295) {
                PinyinData.PINYIN_12295
            } else {
                PinyinData.PINYIN_TABLE[getPinyinCode(c)]
            }
        } else {
            c.toString()
        }
    }

    /**
     * 将输入字符转为拼音
     *
     * @param c 输入字符
     * @param rules 自定义规则，具有最高优先级
     * @return return pinyin if c is chinese in uppercase, String.valueOf(c) otherwise.
     */
    fun toPinyin(c: Char, rules: PinyinRules?): String? {
        return if (rules?.toPinyin(c) != null) {
            rules.toPinyin(c)
        } else {
            toPinyin(c)
        }
    }

    /**
     * 判断输入字符是否为汉字
     *
     * @param c 输入字符
     * @return return whether c is chinese
     */
    fun isChinese(c: Char): Boolean {
        return (PinyinData.MIN_VALUE <= c && c <= PinyinData.MAX_VALUE && getPinyinCode(c) > 0
                || PinyinData.CHAR_12295 == c)
    }

    private fun getPinyinCode(c: Char): Int {
        val offset = c - PinyinData.MIN_VALUE
        return if (0 <= offset && offset < PinyinData.PINYIN_CODE_1_OFFSET) {
            decodeIndex(PinyinCode1.PINYIN_CODE_PADDING, PinyinCode1.PINYIN_CODE, offset)
                .toInt()
        } else if (PinyinData.PINYIN_CODE_1_OFFSET <= offset
            && offset < PinyinData.PINYIN_CODE_2_OFFSET
        ) {
            decodeIndex(
                PinyinCode2.PINYIN_CODE_PADDING, PinyinCode2.PINYIN_CODE,
                offset - PinyinData.PINYIN_CODE_1_OFFSET
            ).toInt()
        } else {
            decodeIndex(
                PinyinCode3.PINYIN_CODE_PADDING, PinyinCode3.PINYIN_CODE,
                offset - PinyinData.PINYIN_CODE_2_OFFSET
            ).toInt()
        }
    }

    private fun decodeIndex(paddings: ByteArray, indexes: ByteArray, offset: Int): Short {
        val index1 = offset / 8
        val index2 = offset % 8
        var realIndex: Short
        realIndex = (indexes[offset] and 0xff.toByte()).toShort()
        if (paddings[index1] and PinyinData.BIT_MASKS[index2].toByte() != 0.toByte()) {
            realIndex = (realIndex or PinyinData.PADDING_MASK.toShort())
        }
        return realIndex
    }

    class Config constructor(dicts: List<PinyinDict?>?) {
        private var mSelector: SegmentationSelector
        private var mPinyinDicts: MutableList<PinyinDict?>? = null

        /**
         * 添加字典
         *
         * @param dict 字典
         * @return 返回Config对象，支持继续添加字典
         */
        fun with(dict: PinyinDict?): Config {
            if (dict != null) {
                if (mPinyinDicts == null) {
                    mPinyinDicts = ArrayList()
                    mPinyinDicts!!.add(dict)
                } else if (!mPinyinDicts!!.contains(dict)) {
                    mPinyinDicts!!.add(dict)
                }
            }
            return this
        }

        fun valid(): Boolean {
            return pinyinDicts != null && selector != null
        }

        val selector: SegmentationSelector?
            get() = mSelector
        val pinyinDicts: MutableList<PinyinDict?>?
            get() = mPinyinDicts

        init {
            if (dicts != null) {
                mPinyinDicts = ArrayList(dicts)
            }
            mSelector = ForwardLongestSelector()
        }

        fun getCnCityDict(context: Context?, assetFileName: String?): AndroidAssetDict =
            CnCityDict.getInstance(context, assetFileName)
    }
}