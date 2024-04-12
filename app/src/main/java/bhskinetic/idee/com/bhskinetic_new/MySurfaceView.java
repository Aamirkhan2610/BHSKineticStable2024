package bhskinetic.idee.com.bhskinetic_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Admin on 7/6/2018.
 */

public class MySurfaceView extends SurfaceView {
    public static Paint mPaint;
    public static Path path;
    public static Bitmap mBitmap;
    public static Canvas mCanvas;
    private ArrayList<PathWithPaint> _graphics1 = new ArrayList<PathWithPaint>();
    public static boolean isPathDrawn=false;
    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        mBitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        this.setBackgroundColor(Color.TRANSPARENT);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PathWithPaint pp = new PathWithPaint();
        mCanvas.drawPath(path, mPaint);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            path.moveTo(event.getX(), event.getY());
            path.lineTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(event.getX(), event.getY());
            pp.setPath(path);
            pp.setmPaint(mPaint);
            _graphics1.add(pp);
            isPathDrawn=true;
        }
        invalidate();
        return true;
    }

    public void resetPath(){
        path.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (_graphics1.size() > 0) {
            canvas.drawPath(_graphics1.get(_graphics1.size() - 1).getPath(),
                    _graphics1.get(_graphics1.size() - 1).getmPaint());

        }
    }

    public class PathWithPaint {
        private Path path;
        public Path getPath() {
            return path;
        }
        public void setPath(Path path) {
            this.path = path;
        }
        private Paint mPaint;
        public Paint getmPaint() {
            return mPaint;
        }
        public void setmPaint(Paint mPaint) {
            this.mPaint = mPaint;
        }
    }
}