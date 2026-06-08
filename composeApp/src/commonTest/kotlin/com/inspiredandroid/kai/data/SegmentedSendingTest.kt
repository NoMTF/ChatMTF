package com.inspiredandroid.kai.data

import kotlin.test.Test
import kotlin.test.assertEquals

class SegmentedSendingTest {

    @Test
    fun `explicit segment marker splits assistant content`() {
        assertEquals(
            listOf("sure", "like this", "one by one"),
            parseSegmentedAssistantContent("sure<kai-segment/>like this<kai-segment/>one by one"),
        )
    }

    @Test
    fun `short conversational lines split into chat bubbles`() {
        assertEquals(
            listOf("sure", "like this", "one by one", "meow"),
            parseSegmentedAssistantContent("sure\nlike this\none by one\nmeow"),
        )
    }

    @Test
    fun `blank-line paragraphs stay as one message`() {
        val content = "sure\n\nlike this"

        assertEquals(listOf(content), parseSegmentedAssistantContent(content))
    }

    @Test
    fun `markdown lists stay as one message`() {
        val content = "- first step\n- second step"

        assertEquals(listOf(content), parseSegmentedAssistantContent(content))
    }

    @Test
    fun `code fences stay as one message`() {
        val content = "```kotlin\nprintln(\"hi\")\n```"

        assertEquals(listOf(content), parseSegmentedAssistantContent(content))
    }

    @Test
    fun `too many natural lines stay as one message`() {
        val content = "one\ntwo\nthree\nfour\nfive"

        assertEquals(listOf(content), parseSegmentedAssistantContent(content))
    }
}
