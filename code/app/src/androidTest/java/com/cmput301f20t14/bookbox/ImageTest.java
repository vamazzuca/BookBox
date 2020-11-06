package com.cmput301f20t14.bookbox;

import android.net.Uri;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.ListsActivity;
import com.cmput301f20t14.bookbox.activities.NotificationsActivity;
import com.cmput301f20t14.bookbox.entities.Image;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Image Test
 * @author ALex Mazzuca
 * @version 20.11.04
 */
public class ImageTest {

    Image imageTestObject = new Image(null, null, null, null);

    @Test
    public void ImageTest(){
        Integer height = 20;
        Integer width = 30;

        imageTestObject.setHeight(height);

        assertEquals(imageTestObject.getHeight(), height);

        imageTestObject.setWidth(width);

        assertEquals(imageTestObject.getWidth(), width);

        Uri imageUri = Uri.parse("gs://bookbox-d77e5.appspot.com/users/Alex00b22bc0-cb7a-407c-ae6e-3bb96dfbda1a");

        imageTestObject.setUri(imageUri);

        assertEquals(imageTestObject.getUri(), imageUri);

        String imageUrl = "doc/picture/image.jpg";

        imageTestObject.setUrl(imageUrl);

        assertEquals(imageTestObject.getUrl(), imageUrl);
    }
}
