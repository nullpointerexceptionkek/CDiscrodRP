/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev

import lee.aspect.dev.discordrpc.Script
import lee.aspect.dev.discordrpc.Updates
import java.util.*

class UndoRedoManager(initialList: List<Updates>) {
    private val undoStack = Stack<List<Updates>>()
    private val redoStack = Stack<List<Updates>>()
    private var currentList: List<Updates> = initialList
    private var previousList: List<Updates> = initialList


    init {
        undoStack.push(initialList)
    }

    fun undo() {
        if (undoStack.size <= 1) {
            return
        }
        redoStack.push(currentList)
        currentList = undoStack.pop()
        applyChangesToScript()
    }

    fun redo() {
        if (redoStack.isEmpty()) {
            return
        }
        undoStack.push(currentList)
        currentList = redoStack.pop()
        applyChangesToScript()
    }

    fun modifyList(list: List<Updates>) {
        undoStack.push(previousList.toList()) // Make a copy of the previous list
        previousList = list.toList()
        redoStack.clear()
    }

    private fun applyChangesToScript() {
        Script.getScript().totalupdates.clear()
        currentList.toList().toTypedArray().forEach {
            Script.getScript().totalupdates.add(it)
        }
    }
}