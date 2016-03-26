/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package despotoski.nikola.github.com.bottomnavigationlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class DrawShadowFrameLayout extends FrameLayout {
    private final Drawable mShadowDrawable;
    private int mShadowElevation = 8;
    private int mWidth;
    private int mHeight;
    private boolean mShadowVisible = true;

    public DrawShadowFrameLayout(Context context) {
        this(context, null, 0);
    }

    public DrawShadowFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawShadowFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mShadowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.shadow);
        if (mShadowDrawable != null) {
            mShadowDrawable.setCallback(this);
        }
        setWillNotDraw(!mShadowVisible);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        updateShadowBounds();
    }

    private void updateShadowBounds() {
        if (mShadowDrawable != null) {
            mShadowDrawable.setBounds(0, 0, mWidth, mShadowElevation);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mShadowDrawable != null && mShadowVisible) {
            getBackground().setBounds(0, mShadowDrawable.getBounds().bottom, mWidth, mHeight);
            mShadowDrawable.draw(canvas);
        }
    }

    public void setShadowVisible(boolean shadowVisible) {
        setWillNotDraw(!mShadowVisible);
        updateShadowBounds();
    }

    int getShadowElevation() {
        return mShadowElevation;
    }
}