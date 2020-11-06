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

    private Image mockImage() {
        Image mockImage = new Image(null, null, null, null);
        return mockImage;
    }

    @Test
    public void testImageSetters(){
        Integer height = 20;
        Integer width = 30;

        Image mockImage = mockImage();

        mockImage.setHeight(height);
        assertEquals(mockImage.getHeight(), height);

        mockImage.setWidth(width);
        assertEquals(mockImage.getWidth(), width);

        Uri imageUri = Uri.parse("gs://bookbox-d77e5.appspot.com/users/Alex00b22bc0-cb7a-407c-ae6e-3bb96dfbda1a");
        mockImage.setUri(imageUri);
        assertEquals(mockImage.getUri(), imageUri);

        String imageUrl = "doc/picture/image.jpg";
        mockImage.setUrl(imageUrl);
        assertEquals(mockImage.getUrl(), imageUrl);
    }
}
