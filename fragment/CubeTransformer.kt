package az.myaccess.ui.activities.stories.fragment

import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2


class CubeTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        //if position = 0 (current image) pivot on x axis is on the right, else if
        // position > 0, (next image) pivot on x axis is on the left (origin of the axis)
        /*val valueanim = if(position <= 0){
            view.width
        } else {
            0
        }
        view.setPivotX(valueanim.toFloat())
        view.pivotY = view.height * 0.5f

        //it rotates with 90 degrees multiplied by current position
        view.rotationY = 90f * position*/



        val deltaY = 0.5f
        val valueanim = if(position <= 0){
            view.width
        } else {
            0
        }
        val pivotX = valueanim.toFloat()
        val pivotY = view.getHeight() * deltaY
        val rotationY = 45f * position

        view.setPivotX(pivotX)
        view.setPivotY(pivotY)
        view.setRotationY(rotationY)
    }
}


/*
* public class CubeTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float deltaY = 0.5f;
        float pivotX = position < 0f ? page.getWidth() : 0f;
        float pivotY = page.getHeight() * deltaY;
        float rotationY = 45f * position;

        page.setPivotX(pivotX);
        page.setPivotY(pivotY);
        page.setRotationY(rotationY);
    }
}*/