package co.fanstories.android.utils.text;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by mohsal on 7/5/17.
 */

public class TextUtils {

    public static void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);

        for(URLSpan span:spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
    }
}
