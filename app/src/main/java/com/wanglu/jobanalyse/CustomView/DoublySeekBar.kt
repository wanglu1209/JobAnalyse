
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.wanglu.jobanalyse.R


/**
 * Created by WangLu on 2018/5/5.
 */
class DoublySeekBar(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val SELECTED_LEFT = "L"
    private val SELECTED_RIGHT = "R"

    private val backgroundLinePaint: Paint = Paint()    // 灰色线条 未选中区域
    private val selectedLinePaint: Paint = Paint()      // 选中线条
    private val textPaint: Paint = Paint()          // 文本
    private var mWidth = 0
    private var mHeight = 0
    private val padding = 100
    private var leftArrow: Bitmap
    private var rightArrow: Bitmap
    private var lastX = 0f
    private var lastY = 0f
    private var leftArrowPosition: Float
    private var leftOffset = padding
    private var rightOffset = 0
    private var selectedArrow = ""
    private var interval = 0

    private var mText: List<String>? = null         // 上方显示的文字

    private var leftText: String = ""
    private var rightText: String = ""

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.DoublySeekBar, defStyleAttr, 0)
        backgroundLinePaint.color = Color.GRAY
        backgroundLinePaint.strokeWidth = 10f
        selectedLinePaint.strokeWidth = 10f
        selectedLinePaint.color = a.getColor(R.styleable.DoublySeekBar_selectedLineColor, Color.RED)
        textPaint.color = Color.BLACK
        textPaint.textSize = dp2px(context, 16f).toFloat()
        leftArrow = BitmapFactory.decodeResource(context.resources, a.getResourceId(R.styleable.DoublySeekBar_leftPic, R.drawable.arrow_up))
        rightArrow = BitmapFactory.decodeResource(context.resources, a.getResourceId(R.styleable.DoublySeekBar_rightPic, R.drawable.arrow_up))
        leftArrowPosition = leftArrow.width.toFloat() / 2
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var specMode: Int = View.MeasureSpec.getMode(widthMeasureSpec)
        var specSize = 0

        when (specMode) {
            View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED
            -> specSize = View.MeasureSpec.getSize(widthMeasureSpec)
        }
        mWidth = specSize

        specMode = View.MeasureSpec.getMode(heightMeasureSpec)
        when (specMode) {
            View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED
            -> {
                val rect = Rect()
                textPaint.getTextBounds("啊", 0, 1, rect)
                specSize = (leftArrow.height * 2 + selectedLinePaint.strokeWidth +rect.height() * 2).toInt()
            }
        }
        mHeight = specSize

        rightOffset = mWidth - padding
        if (mText != null) {
            interval = (mWidth - padding) / mText!!.size
        }

        setMeasuredDimension(mWidth, mHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mText != null) {
            canvas!!.drawLine(padding.toFloat(), (mHeight / 2).toFloat(), mWidth.toFloat() - padding, (mHeight / 2).toFloat(), backgroundLinePaint)
            canvas.drawLine(leftOffset.toFloat(), (mHeight / 2).toFloat(), rightOffset.toFloat(), (mHeight / 2).toFloat(), selectedLinePaint)
            canvas.drawBitmap(leftArrow, leftOffset - leftArrow.width.toFloat() / 2, leftArrow.height / 2 + (mHeight / 2).toFloat(), textPaint)
            canvas.drawBitmap(rightArrow, rightOffset - rightArrow.width.toFloat() / 2, rightArrow.height / 2 + (mHeight / 2).toFloat(), textPaint)

            val rect = Rect()
            leftText = mText!![(leftOffset - padding) / interval]
            rightText = mText!![if (((rightOffset) / interval) > mText!!.size - 1) mText!!.size - 1 else rightOffset / interval]
            textPaint.getTextBounds(leftText, 0, leftText.length, rect)
            canvas.drawText(leftText, leftOffset.toFloat() - rect.width() / 2, (mHeight / 2).toFloat() - rect.height() / 2, textPaint)
            textPaint.getTextBounds(rightText, 0, rightText.length, rect)
            canvas.drawText(rightText, rightOffset.toFloat() - rect.width() / 2, (mHeight / 2).toFloat() - rect.height() / 2, textPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                selectedArrow = collide(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = x - lastX
                if (selectedArrow == SELECTED_LEFT) {
                    leftOffset += offsetX.toInt()
                    if (leftOffset >= rightOffset - interval) leftOffset = rightOffset - interval
                    if (leftOffset < padding) leftOffset = padding
                }

                if (selectedArrow == SELECTED_RIGHT) {
                    rightOffset += offsetX.toInt()
                    if (rightOffset > mWidth - padding) rightOffset = mWidth - padding
                    if (rightOffset <= leftOffset + interval) rightOffset = leftOffset + interval
                    if (rightOffset < padding) rightOffset = padding
                }
                invalidate()

                lastX = x
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 判断点击的是否是两个其中的一个， L左边 R右边
     */
    private fun collide(event: MotionEvent): String {
        if (event.rawX > leftOffset - leftArrow.width / 2 && event.rawX < leftOffset + leftArrow.width / 2) {
            return SELECTED_LEFT
        }

        if (event.rawX > rightOffset - padding - leftArrow.width / 2 && event.rawX < rightOffset + leftArrow.width / 2) {
            return SELECTED_RIGHT
        }

        return ""
    }


    /**
     * 设置文本
     */
    fun setText(text: List<String>) {
        this.mText = text
        invalidate()
    }

    /**
     * 获取值
     */
    fun getValue(): Value{
        return Value(leftText, rightText)
    }

    data class Value(var left:String, var right:String)

    private fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics).toInt()
    }

    private fun sp2px(context: Context, spValue: Float): Int {
        return (spValue * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    private fun px2sp(context: Context, pxValue: Float): Int {
        return (pxValue / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }
}