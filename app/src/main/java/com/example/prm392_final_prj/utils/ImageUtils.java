package com.example.prm392_final_prj.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
public class ImageUtils {
    private static final String PROFILE_IMAGE_DIR = "profile_images";

    /**
     * Lưu ảnh từ Uri vào internal storage
     * @param context Context
     * @param imageUri Uri của ảnh được chọn
     * @param userId ID của user
     * @return Đường dẫn file đã lưu, hoặc null nếu thất bại
     */
    public static String saveImageToInternalStorage(Context context, Uri imageUri, int userId) {
        try {
            // Đọc ảnh từ Uri
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Compress và resize ảnh để tiết kiệm dung lượng
            Bitmap resizedBitmap = resizeBitmap(bitmap, 500, 500);

            // Tạo thư mục nếu chưa có
            File directory = new File(context.getFilesDir(), PROFILE_IMAGE_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo tên file unique cho mỗi user
            String fileName = "profile_" + userId + ".jpg";
            File file = new File(directory, fileName);

            // Lưu ảnh
            FileOutputStream fos = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();

            // Giải phóng bộ nhớ
            bitmap.recycle();
            resizedBitmap.recycle();

            // Trả về đường dẫn
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resize bitmap để giảm kích thước
     */
    private static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    /**
     * Load ảnh từ đường dẫn vào ImageView
     * @param imagePath Đường dẫn file ảnh
     * @param imageView ImageView cần hiển thị
     * @param defaultResId Resource ID của ảnh mặc định nếu không có ảnh
     */
    public static void loadImageIntoView(String imagePath, ImageView imageView, int defaultResId) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                return;
            }
        }
        // Nếu không có ảnh hoặc file không tồn tại, dùng ảnh mặc định
        imageView.setImageResource(defaultResId);
    }

    /**
     * Xóa ảnh profile cũ
     * @param imagePath Đường dẫn ảnh cần xóa
     */
    public static void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Kiểm tra file ảnh có tồn tại không
     */
    public static boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        File file = new File(imagePath);
        return file.exists();
    }
}
