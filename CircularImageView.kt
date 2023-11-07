package com.satmatgroup.satmatmr

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView : AppCompatImageView {
    private val paint: Paint
    private val path: Path

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        paint = Paint()
        path = Path()
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable
        if (drawable == null) {
            super.onDraw(canvas)
            return
        }

        val bitmap = (drawable as BitmapDrawable).bitmap
        val radius = width.coerceAtLeast(height) / 2

        // Scale and center the source bitmap
        val scaledBitmap = scaleCenterCrop(bitmap, width, height)

        // Reset the path and create a circular clipping path
        path.reset()
        path.addCircle(width.toFloat() / 2, height.toFloat() / 2, radius.toFloat(), Path.Direction.CW)

        // Clip the canvas to the circular path
        canvas.clipPath(path)

        // Draw the scaled and centered bitmap within the circular path
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
    }

    private fun scaleCenterCrop(source: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val sourceWidth = source.width
        val sourceHeight = source.height

        // Calculate scaling factors for width and height
        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight

        // Choose the maximum scaling factor to maintain the aspect ratio
        val scale = xScale.coerceAtLeast(yScale)

        // Calculate the dimensions of the scaled image
        val scaledWidth = sourceWidth * scale
        val scaledHeight = sourceHeight * scale

        // Calculate the position to center the scaled image within the view
        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        // Define the destination rectangle for the scaled image
        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        // Create a new bitmap for the scaled image
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

        // Create a canvas for drawing the scaled image
        val canvas = Canvas(scaledBitmap)

        // Create a shader with the source bitmap, allowing it to be drawn as a pattern
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Create a matrix to map the source image onto the destination rectangle
        val matrix = Matrix()
        matrix.setRectToRect(RectF(0f, 0f, sourceWidth.toFloat(), sourceHeight.toFloat()), destRect, Matrix.ScaleToFit.FILL)
        shader.setLocalMatrix(matrix)

        // Set the shader as the paint for drawing
        paint.shader = shader

        // Draw the scaled bitmap within a rounded rectangle (circular image)
        canvas.drawRoundRect(destRect, newWidth.toFloat() / 2, newHeight.toFloat() / 2, paint)

        return scaledBitmap
    }
}
