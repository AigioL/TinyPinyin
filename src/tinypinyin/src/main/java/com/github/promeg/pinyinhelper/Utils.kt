package com.github.promeg.pinyinhelper

import org.ahocorasick.trie.Trie
import java.util.*

/**
 * Created by guyacong on 2016/12/28.
 */
internal object Utils {
    fun dictsToTrie(pinyinDicts: List<PinyinDict?>?): Trie? {
        val all: MutableSet<String?> = TreeSet()
        val builder = Trie.builder()
        if (pinyinDicts != null) {
            for (dict in pinyinDicts) {
                if (dict?.words() != null) {
                    all.addAll(dict.words()!!)
                }
            }
            if (all.size > 0) {
                for (key in all) {
                    builder.addKeyword(key)
                }
                return builder.build()
            }
        }
        return null
    }
}