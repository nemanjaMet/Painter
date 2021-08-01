package com.example.painter.helpers

import com.example.painter.models.CanvasSize
import com.google.common.truth.Truth
import org.junit.Test


class PainterManagerTest {

    @Test
    fun `is canvas size correct_parseCanvasDimensions`() {

        val expectedCanvasSize = CanvasSize(720,1280)
        val resultCanvasSize = PainterManager.parseCanvasDimensions("720", "1280")

        Truth.assertThat(resultCanvasSize).isEqualTo(expectedCanvasSize)
    }

}