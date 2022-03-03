package ru.neosvet.health.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.health.list.HealthHolder
import java.util.*

//based on https://zatackcoder.com/android-recyclerview-swipe-to-multiple-options/
class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView,
    private val buttons: List<UnderlayButton>
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    companion object {
        private const val BUTTON_WIDTH_IN_PX = 70
    }

    var touchEvent: ((Int) -> Unit)? = null // Int - action
    private var buttonWidthInDp: Int = BUTTON_WIDTH_IN_PX
    private var swipedPos = -1
    private var swipeThreshold = 0.5f
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>> = HashMap()
    private val recoverQueue: Queue<Int> by lazy {
        createQueue()
    }
    private val neededHolder: Class<*> = HealthHolder::class.java
    private val gestureDetector: GestureDetector by lazy {
        createDetector(context)
    }

    init {
        buttonWidthInDp =
            (BUTTON_WIDTH_IN_PX * Resources.getSystem().displayMetrics.density).toInt()
        setTouchListener()
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(this.recyclerView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener() {
        recyclerView.setOnTouchListener { _, e ->
            touchEvent?.invoke(e.action)
            if (swipedPos < 0) return@setOnTouchListener false
            val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos)
                ?: return@setOnTouchListener false
            val swipedItem = swipedViewHolder.itemView
            val rect = Rect()
            swipedItem.getGlobalVisibleRect(rect)
            if (e.action == MotionEvent.ACTION_DOWN
                || e.action == MotionEvent.ACTION_UP
                || e.action == MotionEvent.ACTION_MOVE
            ) {
                val y = e.rawY.toInt()
                if (rect.top < y && rect.bottom > y)
                    gestureDetector.onTouchEvent(e)
                else {
                    recoverQueue.add(swipedPos)
                    swipedPos = -1
                    recoverSwipedItem()
                }
            }
            false
        }
    }

    private fun createQueue() = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            return if (contains(element)) false
            else super.add(element)
        }
    }

    private fun createDetector(context: Context) =
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                for (button in buttons) {
                    if (button.onClick(e.x, e.y)) break
                }
                return true
            }
        })

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (isNotNeed(viewHolder)) return
        val pos = viewHolder.adapterPosition
        if (swipedPos != pos)
            recoverQueue.add(swipedPos)
        swipedPos = pos
        buttonsBuffer.clear()
        swipeThreshold = 0.5f * buttons.size * buttonWidthInDp
        recoverSwipedItem()
    }

    private fun isNotNeed(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.javaClass != neededHolder
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return if (isNotNeed(viewHolder)) 0f else swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (isNotNeed(viewHolder)) return
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<UnderlayButton> = ArrayList()
                if (!buttonsBuffer.containsKey(pos)) {
                    buffer.addAll(buttons)
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]!!
                }
                translationX = dX * buffer.size * buttonWidthInDp / itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            recoverQueue.poll()?.let { pos ->
                recyclerView.adapter?.notifyItemChanged(pos)
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )
            right = left
        }
    }

    class UnderlayButton(
        private val image: Bitmap?,
        private val color: Int,
        private val onClick: (Int) -> Unit
    ) {
        private var pos = 0
        private var clickRegion: RectF? = null
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                onClick.invoke(pos)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()

            // Draw background
            p.color = color
            c.drawRect(rect, p)
            val r = Rect()
            val x = rect.width() / 2f - r.width() / 2f - r.left
            val y = rect.height() / 2f + r.height() / 2f - r.bottom

            // Draw image
            if (image != null) {
                c.drawBitmap(
                    image, rect.left + x - image.width / 2,
                    rect.top + y - image.height / 2, p
                )
            }
            clickRegion = rect
            this.pos = pos
        }
    }
}