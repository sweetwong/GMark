package sweet.wong.sweetnote.utils

import java.util.*

class LimitedDeque<E>(var limit: Int) : LinkedList<E>() {

    override fun add(element: E): Boolean {
        super.add(element)
        while (size > limit) {
            super.remove()
        }
        return true
    }

}