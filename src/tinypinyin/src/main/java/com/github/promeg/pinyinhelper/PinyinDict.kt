package com.github.promeg.pinyinhelper

/**
 * 字典接口，字典应保证对[PinyinDict.words]中的所有词，[PinyinDict.toPinyin]均返回非null的结果
 *
 * Created by guyacong on 2016/12/23.
 */
interface PinyinDict {
    /**
     * 字典所包含的所有词
     *
     * @return 所包含的所有词
     */
    fun words(): Set<String?>?

    /**
     * 将词转换为拼音
     *
     * @param word 词
     * @return 应保证对{@link PinyinDict#words()}中的所有词，[PinyinDict.toPinyin]均返回非null的结果
     */
    fun toPinyin(word: String?): Array<String?>?
}