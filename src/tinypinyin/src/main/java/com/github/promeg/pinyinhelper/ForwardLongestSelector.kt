package com.github.promeg.pinyinhelper

import org.ahocorasick.trie.Emit
import com.github.promeg.pinyinhelper.Engine.EmitComparator
import java.util.*

/**
 * 正向最大匹配选择器
 *
 * Created by guyacong on 2016/12/28.
 */
internal class ForwardLongestSelector : SegmentationSelector {
    override fun select(emits: Collection<Emit>?): List<Emit>? {
        if (emits == null) {
            return null
        }
        val results: MutableList<Emit> = ArrayList(emits)
        Collections.sort(results, HIT_COMPARATOR)
        var endValueToRemove = -1
        val emitToRemove: MutableSet<Emit> = TreeSet()
        for (emit in results) {
            if (emit.start > endValueToRemove && emit.end > endValueToRemove) {
                endValueToRemove = emit.end
            } else {
                emitToRemove.add(emit)
            }
        }
        results.removeAll(emitToRemove)
        return results
    }

    companion object {
        val HIT_COMPARATOR = EmitComparator()
    }
}