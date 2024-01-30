package com.gpsgetwoweducation.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gpsgetwoweducation.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class Utility {

    static long oneSecond = 1000;
    static long oneMinute = oneSecond * 60;
    static long oneHour = oneMinute * 60;
    static long oneDay = oneHour * 24;
    static long oneWeek = oneDay * 7;
    static long oneYear = oneDay * 365;
    static long oneMonth = oneDay * 31;

    public static SpannableStringBuilder makeSectionOfTextColor(Context context, String text, String textToBold) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (textToBold.length() > 0 && !textToBold.trim().equals("")) {
            //for counting start/end indexes
            String testText = text.toLowerCase(Locale.US);
            String testTextToBold = textToBold.toLowerCase(Locale.US);
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            //for counting start/end indexes

            if (startingIndex < 0 || endingIndex < 0) {
                return builder.append(text);
            } else if (startingIndex >= 0 && endingIndex >= 0) {

                builder.append(text);
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), startingIndex, endingIndex, 0);
                // builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        } else {
            return builder.append(text);
        }

        return builder;
    }

    public static boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

       /* Log.d(TAG, "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);*/

        return isKeyboardShown;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String CapsFirst(String str) {
        String[] words = str.split(" ");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            ret.append(Character.toUpperCase(words[i].charAt(0)));
            ret.append(words[i].substring(1));
            if (i < words.length - 1) {
                ret.append(' ');
            }
        }
        return ret.toString();
    }

    public static String getTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss");
        Date d = sdf.parse(date);
        //long timeDifferenceInMillis = d.getTime();
        Calendar cal = Calendar.getInstance();
         cal.setTime(d);

        return cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " "+
                cal.get(Calendar.AM_PM);
    }

    public static String getDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date d = sdf.parse(date);
        //long timeDifferenceInMillis = d.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return d.toString();
    }

    @NonNull
    public static String getTimeDisplayString(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss");
        Date d = sdf.parse(date);
        //long timeDifferenceInMillis = d.getTime();
        Calendar cal = Calendar.getInstance();
        long current = cal.getTimeInMillis();
        cal.setTime(d);
        long timeDifferenceInMillis = current - cal.getTimeInMillis();
        String displayTime;

        long yearsDifference = timeDifferenceInMillis / oneYear;
        long daysDifference = timeDifferenceInMillis / oneDay;
        long hoursDifference = timeDifferenceInMillis / oneHour;
        long minutesDifference = timeDifferenceInMillis / oneMinute;
        long monthsDifference = timeDifferenceInMillis / oneMonth;
        if (yearsDifference >= 1) {
            if (yearsDifference == 1) {
                displayTime = "an year ago";
            } else {
                displayTime = yearsDifference + " yrs ago";
            }

        } else if (monthsDifference >= 1) {
            if (monthsDifference == 1) {
                displayTime = "a month ago";
            } else {
                displayTime = monthsDifference + " months";
            }

        } else if (daysDifference >= 1) {
            if (daysDifference == 1) {
                displayTime = "a day ago";
            } else {
                displayTime = daysDifference + " days ago";
            }
        } else if (hoursDifference >= 1) {
            if (hoursDifference == 1) {
                displayTime = "an hour ago";
            } else {
                displayTime = hoursDifference + " hrs ago";
            }

        } else if (minutesDifference >= 1) {
            if (minutesDifference == 1) {
                displayTime = "a min ago";
            } else {
                displayTime = minutesDifference + " min ago";
            }

        } else {
            displayTime = "Just now";
        }
        return displayTime;
    }

    private static boolean DownloadImage(Context context, ResponseBody body, ImageView view) {
        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = body.byteStream();
                out = new FileOutputStream(context.getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            } catch (IOException e) {
                Log.d("DownloadImage", e.toString());
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            int width, height;
            Bitmap bMap = BitmapFactory.decodeFile(context.getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
            width = 2 * bMap.getWidth();
            height = 6 * bMap.getHeight();
            Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
            view.setImageBitmap(bMap2);
            return true;

        } catch (IOException e) {
            Log.d("DownloadImage", e.toString());
            return false;
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

}