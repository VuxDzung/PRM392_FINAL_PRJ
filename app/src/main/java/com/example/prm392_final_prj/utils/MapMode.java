package com.example.prm392_final_prj.utils;

import java.io.Serializable;

public enum MapMode implements Serializable {
    /**
     * Chỉ xem: Hiển thị các marker được truyền vào.
     * Dùng cho MapDetailActivity.
     */
    VIEW,

    /**
     * Chọn vị trí mới: Cho phép người dùng click vào bản đồ
     * để tạo/di chuyển một marker.
     * Dùng cho MapPickerActivity.
     */
    PICK_LOCATION,

    /**
     * Chọn Marker có sẵn: Hiển thị các marker, cho phép người dùng
     * click vào một marker có sẵn để chọn nó.
     * Dùng cho MapPickerActivity.
     */
    SELECT_EXISTING_MARKER
}
