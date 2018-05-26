package pikassoapp.buildappswithpaulo.com.pikasso.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pikassoapp.buildappswithpaulo.com.pikasso.ColorImages;
import pikassoapp.buildappswithpaulo.com.pikasso.ColoringSize;
import pikassoapp.buildappswithpaulo.com.pikasso.CustomPath;
import pikassoapp.buildappswithpaulo.com.pikasso.MainActivity;
import pikassoapp.buildappswithpaulo.com.pikasso.StorageActivity;
import pikassoapp.buildappswithpaulo.com.pikasso.Storageadapter;

/**
 * Created by paulodichone on 11/13/17.
 */

public class PikassoView extends View {


    public static final float TOUCH_TOLERANCE = 10;

    private static int NONE = 0;

    private static int DRAG = 1;

    private static int ZOOM = 2;

    private static float MIN_ZOOM = 1f;

    private static float MAX_ZOOM = 3f;



    private float scaleFactor = 1.f;




    public static Bitmap bitmap;
    public static Bitmap bitmap2;

    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    public HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    private OutputStream outputStream;
    private Rect imageRect;
    private static Bitmap bitmapimg;
    private int number;
    public  boolean start = false;
    public File directory;
    public static int width;
    public static int height;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public final static String NUMBER_FILE = "NUMBER FILE";
    public static boolean go_to_storage_activity = false;
    public HashMap<String, Paint> hashMapProjectColor = new HashMap<>();
    public HashMap<String, Path> hashMapProjectPath = new HashMap<>();
    //CustomPath customPath = new CustomPath();
    private Canvas canvas;
    ArrayList<Path> pathArrayList = new ArrayList<>();
    ArrayList<ColoringSize> coloringSizeArrayList = new ArrayList<>();


    private int numberfile;
    static Context contextt;
    private boolean fromfile = true;
    private boolean drawlist = true;
    private boolean Erase = true;
    private boolean invalidate;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float scalePointX;
    private float scalePointY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId;
    private float mPosX;
    private float mPosY;
    private float deltaX;
    private float deltaY;
    private int mode = 0;
    private float startX;
    private float startY;
    private float translateX;
    private float translateY;
    private boolean touchmoved = true;
    private float previousTranslateX;
    private float previousTranslateY;
    private ScaleGestureDetector detector;
    private float gx;
    private float gy;
    private float startY2;
    private float startX1;
    private boolean zoom = true;
    private int tap;


    public PikassoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        contextt = context;
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

        sharedPreferences = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        number = sharedPreferences.getInt(NUMBER_FILE,0);
        hashMapProjectColor.clear();
        hashMapProjectPath.clear();

        init();


    }

    void init() {



        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.TRANSPARENT);
        paintLine.setStyle(Paint.Style.STROKE);
       // paintLine.setStrokeWidth(20);
        setLineWidth(20);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

       pathArrayList.clear();
        drawlist = true;
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();




      // Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
      //          R.mipmap.img);

      // setBitmap(icon);
    }

    public static void setBitmap(Bitmap bitmap) {
        bitmapimg = bitmap;
    }


    public static void setcolorbitmap(Bitmap bitmap1){
        bitmap2 = bitmap1;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
       bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
       bitmapCanvas = new Canvas(bitmap);

        imageRect = new Rect(0, 0, w, h);
        width = w;
        height = h;
        if(go_to_storage_activity) {
            Intent intent = new Intent(getContext(), StorageActivity.class);
            getContext().startActivity(intent);
            go_to_storage_activity = false;
            return;
        }

        if(bitmapCanvas != null && Storageadapter.clicked_sotrage){
            try {
                getLocalHashFromFile();
                getLocalColorFromFile();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    protected void onDraw(Canvas canvas) {
        //We're going to scale the X and Y coordinates by the same amount


        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        if((translateX * -1) < 0) {
            translateX = 0;
        }

        //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth
    else if((translateX * -1) > (scaleFactor - 1) * width) {
            translateX = (1 - scaleFactor) * width;
        }
        if(translateY * -1 < 0) {
            translateY = 0;
        }
        //We do the exact same thing for the bottom bound, except in this case we use the height of the display

    else if((translateY * -1) > (scaleFactor - 1) * height) {
            translateY = (1 - scaleFactor) * height;

        }

        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level

        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.scale(scaleFactor, scaleFactor,startX1,startY2);
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);


//        canvas.restore();





        //canvas.drawBitmap(bitmapimg,0, 0, paintScreen);
        if(imageRect == null) { // I think it is always false as we are intializing it onSizeChanged
            imageRect = new Rect(0, 0, getWidth(), getHeight());
        }

        if(bitmapimg != null) {
            canvas.drawBitmap(bitmapimg, null, imageRect, paintScreen);
        }
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);



        for (Integer key : pathMap.keySet()) {

            CustomPath path = (CustomPath) pathMap.get(key);
            canvas.drawPath(path, paintLine);
            ColoringSize coloringSize = new ColoringSize(getLineWidth(),getDrawingColor(),invalidate);
            coloringSizeArrayList.add(coloringSize);
            pathArrayList.add(path);

        }
       // for(Path p: pathArrayList.size())

        if(pathArrayList.size() > 0) {
             if (drawlist){
                for (int i = 0; i < pathArrayList.size(); i++) {
                    CustomPath path = (CustomPath) pathArrayList.get(i);
                    if(coloringSizeArrayList.get(i).getErase()){
                        setLineWidth(coloringSizeArrayList.get(i).getWidth());
                        setDrawingColor(Color.TRANSPARENT);
                    } else{
                       // paintLine.setXfermode(null);
                        paintLine.setColor(coloringSizeArrayList.get(i).getColor());
                        //setDrawingColor(coloringSizeArrayList.get(i).getColor());
                        setLineWidth(coloringSizeArrayList.get(i).getWidth());
                        canvas.drawPath(pathArrayList.get(i), paintLine);
                    }
                }
       }
            drawlist = false;
        }


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if ((mode == DRAG && scaleFactor != 1f) || mode == ZOOM) {

            //invalidate();

        }


//        Log.i("drawing color = ", String.valueOf(getDrawingColor()));
////        if(getDrawingColor() == Color.TRANSPARENT){
////            return true;
////        }
//
        int action = event.getActionMasked(); // event type;
        int actionIndex = event.getActionIndex(); // pointer ( finger, mouse..)
//
//        if (action == MotionEvent.ACTION_DOWN) {
//
//            mode = NONE;
//            touchStarted(event.getX(actionIndex),
//                    event.getY(actionIndex),
//                    event.getPointerId(actionIndex));
//
//
//        } else if (action == MotionEvent.ACTION_UP ||
//                action == MotionEvent.ACTION_POINTER_UP) {
//            mode = NONE;
//
//            touchEnded(event.getPointerId(actionIndex), event);
//
//        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
//
//
//            startX = event.getX();
//            startY = event.getY();
//
//
//            touchmoved = false;
//            mode = DRAG;
//
//        } else if (action == MotionEvent.ACTION_MOVE && mode == DRAG) {
//            touchmoved = false;
////We calculate the values of translateX and translateY by finding the difference between the X/Y coordinate
//            //and the starting X/Y coordinate. Since this event is fired every time the finger moves, we're constantly
//            //updating the values of these two coordinates
//
//            translateX = event.getX() - startX;
//            translateY = event.getY() - startY;
//            Log.i("moved double = ", translateX + " " + translateX);
//
//        } else {
//            if (touchmoved) {
//                touchMoved(event);
//            }
//        }
//
//        if ((mode == DRAG && scaleFactor != 1f) || mode == ZOOM) {
//            invalidate();
//        }
//
//        if (invalidate) {
//            invalidate();
//        }


        // deals with the zoom and dragging methods

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                tap = 1;
                touchStarted(event.getX(actionIndex),
                        event.getY(actionIndex),
                        event.getPointerId(actionIndex));
                //mode = DRAG;


                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.

                // startX = event.getX() - previousTranslateX;
                // startY = event.getY() - previousTranslateY;
                break;
            case MotionEvent.ACTION_MOVE:
                touchMoved(event);

                //We calculate the values of translateX and translateY by finding the difference between the X/Y coordinate
                //and the starting X/Y coordinate. Since this event is fired every time the finger moves, we're constantly
                //updating the values of these two coordinates

//
//                    double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2)
//                            + Math.pow(event.getY() - (startY + previousTranslateY), 2));
//                    if (distance > 10) {
//                        Log.i("distance = ", String.valueOf(distance));
//                        translateX = event.getX() - startX;
//                        translateY = event.getY() - startY;
//                        invalidate();

                //    }
                //  break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2){

                if (tap == 1) {
                    scaleFactor = 1f;
                    tap = 0;
                    Log.i("tap = ", String.valueOf(tap));


        }else{
            startX1 = event.getX();
            startY2 = event.getY();
            Log.i("x,y = ", String.valueOf(startX1 + startY2));
            mode = ZOOM;
            zoom = false;
        }}
            break;




            case MotionEvent.ACTION_UP:
                mode = NONE;
                touchEnded(event.getPointerId(actionIndex), event);

                    tap++;

                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslateY
                //previousTranslateX = translateX;
                //previousTranslateY = translateY;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = DRAG;

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                //previousTranslateX = translateX;
                //previousTranslateY = translateY;
                break;
        }
        return true;
    }


        private void touchMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {

            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);


                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);
                //if(point==null)point = new Point();




                    // Calculate how far the user moved from the last update
                    deltaX = Math.abs(newX - point.x);
                    deltaY = Math.abs(newY - point.y);


                    mPosX += deltaX;
                    mPosY += deltaY;
                     
                    invalidate();

                

                // if the distance is significant enough to be considered a movement then...
                if (deltaX >= TOUCH_TOLERANCE ||
                        deltaY >= TOUCH_TOLERANCE) {
                    if (MainActivity.changeSinceLastSaved) {
                        MainActivity.changeSinceLastSaved = false;
                    }

                    path.quadTo(point.x, point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2);


                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;

                    if (!invalidate) {
                        invalidate();
                    }

                }
            }
        }

    }


    private void touchEnded(int pointerId, MotionEvent event) {

        final float x = (event.getX() - scalePointX)/mScaleFactor;
        final float y = (event.getY() - scalePointY)/mScaleFactor;


        invalidate();

        Path path = pathMap.get(pointerId); // get the corresponding Path
       ;
       if(path != null) {
           bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
           path.reset();
       }

    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; // store the path for given touch
        Point point; // store the last point in path


        if (pathMap.containsKey(pointerId) && fromfile) {
            path =  pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
//            if(point==null){
//                point = new Point();
//          }
        } else {
            path = new CustomPath();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);

        }

        // move to the coordinates of the touch
        //customPath.lineTo(x, y);
        //customPath.moveTo(x,y);


        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;

    }


    public void startEraser() {
        if(Erase) {
            paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            invalidate = true;
            Erase = false;


        }else {
            invalidate = false;
            paintLine.setXfermode(null);
            Erase = true;
        }
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();

    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public void clear() {
        pathMap.clear(); // removes all of the paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.TRANSPARENT);

        invalidate();// refresh the screen
    }

    public void saveImage() {
        String filename = "Pikasso" + System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");


        // get a URI for the location to save the file
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);


        try {
            OutputStream outputStream =
                    getContext().getContentResolver().openOutputStream(uri);

            // copy the bitmap to the outputstream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // this is our image


            try {
                outputStream.flush();
                outputStream.close();

                Toast message = Toast.makeText(getContext(), "Image Saved!!", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();





            } catch (IOException e) {

                Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();

            }
        } catch (FileNotFoundException e) {

            Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();

            // e.printStackTrace();
        }
    }


    public void saveImageToExternalStorage() {

        //Source: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        String filename = "Pikasso" + System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");


        // get a URI for the location to save the file
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);


        try {
            outputStream =
                    getContext().getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "test.jpeg");

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);


        try {
            outputStream.flush();
            outputStream.close();

            Toast message = Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getImagePath() {
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        return directory.getAbsolutePath();
    }

    public void saveToInternalStorage() {
        ContextWrapper cw = new ContextWrapper(getContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NUMBER_FILE,number+1);
         numberfile= number+1;

        String filename = "Pikasso" + System.currentTimeMillis();
        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,  "profile" + numberfile + ".jpg");
        File mypathbackground = new File(directory,  "background" + numberfile + ".jpg");
        editor.apply();



        FileOutputStream fos = null;
        FileOutputStream fosbackground = null;

        try {
            fos = new FileOutputStream(mypath);
            fosbackground = new FileOutputStream(mypathbackground);
            // Use the compress method on the BitMap object to write image to the OutputStream
            for (int i = 0; i < pathArrayList.size(); i++) {
                bitmapCanvas.drawPath(pathArrayList.get(i), paintLine);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            bitmapimg.compress(Bitmap.CompressFormat.PNG, 100, fosbackground);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
                fosbackground.flush();
                fos.close();
                Log.d("Image:", directory.getAbsolutePath());
                Toast message = Toast.makeText(getContext(), "Image Saved +" + directory.getAbsolutePath(), Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();
                savehashmap();
                if(start) {
                    bitmap.eraseColor(Color.TRANSPARENT);
                    invalidate();
                    Intent intent = new Intent(getContext(), ColorImages.class);
                    getContext().startActivity(intent);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // return directory.getAbsolutePath();
    }

    public void savehashmap(){
        try {
            saveHashToFile(pathArrayList);
            saveColorToFile(coloringSizeArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveHashToFile(ArrayList<Path> hash) throws IOException {
        String filePath = getContext().getFilesDir().getPath().toString() + "/your.properties" ;
        File file = new File(filePath);
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        //s.defaultWriteObject();
        s.writeObject(hash);
        s.close();
    }

    public void saveColorToFile(ArrayList<ColoringSize> coloringSizeArrayList) throws IOException {
        String filePath = getContext().getFilesDir().getPath().toString() + "/your.color" ;
        File file = new File(filePath);
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        //s.defaultWriteObject();
        s.writeObject(coloringSizeArrayList);
        s.close();
    }




    public void getLocalColorFromFile() throws  ClassNotFoundException, IOException {
        coloringSizeArrayList.clear();

        String filePath = contextt.getFilesDir().getPath() + "/your.color" ;
        File file = new File(filePath);
        FileInputStream f = new FileInputStream(file);

        ObjectInputStream s = new ObjectInputStream(f);
        fromfile = false;

        coloringSizeArrayList = (ArrayList<ColoringSize>) s.readObject();
    }


    public void getLocalHashFromFile() throws  ClassNotFoundException, IOException {
        pathArrayList.clear();

        String filePath = contextt.getFilesDir().getPath() + "/your.properties" ;
        File file = new File(filePath);
        FileInputStream f = new FileInputStream(file);

        ObjectInputStream s = new ObjectInputStream(f);
        fromfile = false;

        pathArrayList = (ArrayList<Path>) s.readObject();

       // bitmapCanvas.drawPath(pathMap.get(0), paintLine); // draw to bitmapCanvas
        //path.reset();

        /*

         canvas = null;
        Canvas canvas = new Canvas();
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);
        canvas.drawBitmap(bitmapimg, null, imageRect, paintScreen);
        if(bitmapCanvas != null) {
            for (Integer key : pathMap.keySet()) {
                bitmapCanvas.drawPath(pathMap.get(key), paintLine);
                canvas.drawPath(pathMap.get(key), paintLine);

            }
        }


        Log.e("hashfromfileLOCAL", ""+pathMap);
        */

    }




    public void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "profile.jpg");

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
           // ImageView imageView = findViewById(R.id.savedImageView);
           // imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
             //mode = ZOOM;
            scaleFactor *= detector.getScaleFactor();
            gx = detector.getFocusX();
            gy = detector.getFocusX();

            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }
}
