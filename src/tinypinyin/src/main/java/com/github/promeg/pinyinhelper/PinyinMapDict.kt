package com.github.promeg.pinyinhelper

/**
 * 基于[java.util.Map]的字典实现，利于添加自定义字典
 *
 * Created by guyacong on 2016/12/23.
 */
abstract class PinyinMapDict : PinyinDict {
    /**
     * Key为字典的词，Value为该词所对应的拼音
     *
     * @return 包含词和对应拼音的 [java.util.Map]
     */
    abstract fun mapping(): Map<String?, Array<String?>?>?
    override fun words(): Set<String?>? {
        return if (mapping() != null) mapping()!!.keys else null
    }

    override fun toPinyin(word: String?): Array<String?>? {
        return if (mapping() != null) mapping()!![word] else null
    }
}