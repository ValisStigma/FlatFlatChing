package com.flatflatching.flatflatching.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by rafael on 26.10.2015.
 */
public class SerialBitmap implements Serializable {

        private Bitmap bitmap;

    public Bitmap getImage() {
        return bitmap;
    }
        // TODO: Finish this constructor
        public SerialBitmap(InputStream in) {
            // Take your existing call to BitmapFactory and put it here
            bitmap = BitmapFactory.decodeStream(in);
        }

        // Converts the Bitmap into a byte array for serialization
        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
            byte bitmapBytes[] = byteStream.toByteArray();
            out.write(bitmapBytes, 0, bitmapBytes.length);
        }

        // Deserializes a byte array representing the Bitmap and decodes it
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int b;
            while((b = in.read()) != -1)
                byteStream.write(b);
            byte bitmapBytes[] = byteStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        }
    }

