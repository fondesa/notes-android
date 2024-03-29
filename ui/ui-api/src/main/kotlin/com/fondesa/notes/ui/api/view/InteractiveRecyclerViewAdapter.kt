/*
 * Copyright (c) 2019 Fondesa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fondesa.notes.ui.api.view

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter used to define the gestures on the rows of a [RecyclerView].
 *
 * @param VH the subtype of [InteractiveRecyclerViewHolder].
 */
abstract class InteractiveRecyclerViewAdapter<VH : InteractiveRecyclerViewHolder> :
    RecyclerView.Adapter<VH>() {

    /**
     * Constructs the [InteractiveRecyclerViewHolder] for a given [View].
     *
     * @param parent root [View] of the cell.
     * @param viewType the view type of the new [View].
     * @return new instance of [InteractiveRecyclerViewHolder].
     */
    abstract fun onCreateInteractiveViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH

    /**
     * Called when a view in a row (or its root view) is clicked.
     *
     * @param view current [View] of the cell.
     * @param position current position.
     */
    open fun onCellClick(view: View, position: Int) = Unit

    /**
     * Called when a view in a row (or its root view) is long-clicked.
     *
     * @param view current [View] of the cell.
     * @param position current position.
     */
    open fun onCellLongClick(view: View, position: Int): Boolean = false

    /**
     * Called when a view in a row (or its root view) is touched.
     *
     * @param view current [View] of the cell.
     * @param position current position.
     * @param event event triggered.
     */
    open fun onCellTouch(view: View, position: Int, event: MotionEvent): Boolean = false

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        val holder = onCreateInteractiveViewHolder(parent, viewType)
        holder.interactions.forEach {
            addGestureToView(holder, it.view, it.gesture)
        }
        return holder
    }

    private fun addGestureToView(
        holder: InteractiveRecyclerViewHolder,
        v: View,
        gesture: RecyclerViewRowGesture
    ) {
        when (gesture) {
            RecyclerViewRowGesture.CLICK -> v.setOnClickListener { view ->
                onCellClick(view, holder.adapterPosition)
            }
            RecyclerViewRowGesture.LONG_CLICK -> v.setOnLongClickListener { view ->
                onCellLongClick(view, holder.adapterPosition)
            }
            RecyclerViewRowGesture.TOUCH -> v.setOnTouchListener { view, event ->
                onCellTouch(view, holder.adapterPosition, event)
            }
        }
    }
}