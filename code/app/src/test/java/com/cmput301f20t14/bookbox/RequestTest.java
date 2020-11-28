package com.cmput301f20t14.bookbox;

import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Request;
import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestTest {
    private Request mockRequest() {
        Book book = mockBook();
        return new Request("Joe", "Jane", book, "2020-11-20", true, "3.14,-12.33");
    }

    private Book mockBook(){
        Book book = new Book("1234567890", "MyBook", "MyAuthor", "Me",
                Book.ACCEPTED, "MyFriend", "doc/picture/image.jpg");
        return book;
    }

    @Test
    public void TestRequestInfo() {
        Request request = mockRequest();
        assertEquals(request.getBorrower(), "Joe");
        assertEquals(request.getOwner(), "Jane");
        assertTrue(request.getAccepted());
        assertEquals(request.getDate(), "2020-11-20");
        assertEquals(request.getBook().getIsbn(), "1234567890");
    }

    @Test
    public void TestParseLatLng() {
        Request request = mockRequest();
        LatLng latLng = Request.parseLatLngString(request.getLatLng());
        assert latLng != null;
        double lat = latLng.latitude;
        double longitude = latLng.longitude;
        assertEquals(3.14, lat, 0.0);
        assertEquals(longitude, -12.33, 0.0);
    }
}
