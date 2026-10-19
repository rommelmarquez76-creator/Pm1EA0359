package com.uth.pm1ea0359.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImagenController {

    private static final int MAX_LADO = 1024;

    public static String guardarBitmap(Context ctx, Bitmap bmp) {
        File archivo = new File(ctx.getFilesDir(), "contacto_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return archivo.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    public static String guardarDesdeUri(Context ctx, Uri uri) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream in = ctx.getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, bounds);
            }
            int sample = 1;
            int mayor = Math.max(bounds.outWidth, bounds.outHeight);
            while (mayor / sample > MAX_LADO) sample *= 2;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bmp;
            try (InputStream in = ctx.getContentResolver().openInputStream(uri)) {
                bmp = BitmapFactory.decodeStream(in, null, opts);
            }
            return bmp == null ? null : guardarBitmap(ctx, bmp);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap cargar(String ruta) {
        if (ruta == null || ruta.isEmpty() || !new File(ruta).exists()) return null;
        return BitmapFactory.decodeFile(ruta);
    }
}
