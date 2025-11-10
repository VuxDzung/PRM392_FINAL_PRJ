package com.example.prm392_final_prj.mockdata;

import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.entity.UserEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TourMockData {

    // --- Hằng số trạng thái Booking ---
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_CONFIRMED = 2;
    public static final int STATUS_CANCELLED = 3;


    // --- Danh sách mock data ---
    private List<TourEntity> mockTours;
    private List<TourScheduleEntity> mockSchedules;
    private List<UserEntity> mockUsers;
    private List<BookingOrderEntity> mockBookings;

    public TourMockData() {
        addMockDataForDemo();
    }

    // --- Getters cho tất cả dữ liệu ---
    public List<TourEntity> getAllTours() {
        return mockTours;
    }
    public List<TourScheduleEntity> getAllSchedules() { return mockSchedules; }
    public List<UserEntity> getAllUsers() {
        return mockUsers;
    }
    public List<BookingOrderEntity> getAllBookings() {
        return mockBookings;
    }


    // --- Lấy dữ liệu theo ID hoặc điều kiện ---
    public TourEntity getTourById(int id) {
        for (TourEntity tour : mockTours) {
            if (tour.getId() == id) {
                return tour;
            }
        }
        return null;
    }

    public List<TourScheduleEntity> getSchedulesForTour(int tourId) {
        List<TourScheduleEntity> result = new ArrayList<>();
        for (TourScheduleEntity schedule : mockSchedules) {
            if (schedule.getTourId() == tourId) {
                result.add(schedule);
            }
        }
        return result;
    }

    // --- Hàm hash password ---
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Hàm khởi tạo TOÀN BỘ dữ liệu mẫu
     */
    private void addMockDataForDemo() {
        // Khởi tạo tất cả các danh sách
        mockTours = new ArrayList<>();
        mockSchedules = new ArrayList<>();
        mockUsers = new ArrayList<>();
        mockBookings = new ArrayList<>();

        // Thêm dữ liệu theo thứ tự
        addMockUsers();
        addMockTours();
        addMockSchedules();
        addMockBookings();
    }

    // --- 1. Thêm Mock Users ---
    private void addMockUsers() {
        // Mật khẩu cho tất cả user mẫu bên dưới đều là: "123456"
        addWithId(new UserEntity("Admin", "User", "0123456789", "admin@example.com", "123456", "ADMIN")); // ID 1
        addWithId(new UserEntity("John", "Doe", "0987654321", "a@a.com", "123456", "CUSTOMER")); // ID 2
        addWithId(new UserEntity("Jane", "Smith", "0912345678", "jane.smith@example.com", "123456", "CUSTOMER")); // ID 3
    }

    private void addWithId(UserEntity user) {
        int id = mockUsers.size() + 1;
        user.setId(id);
        // Hash password trước khi thêm
        user.setPassword(hashPassword(user.getPassword()));
        mockUsers.add(user);
    }


    // --- 2. Thêm Mock Tours ---
    private void addMockTours() {
        addWithId(new TourEntity("Ha long Bay", "4 Days - 3 Nights", 120.34, "Ha Noi", "Ha Long", false, "Bus", null, 50)); // ID 1
        addWithId(new TourEntity("Da Nang", "2 Days - 1 Nights", 185.75, "Ha Noi", "Da Nang", true, "Airplane", null, 40)); // ID 2
        addWithId(new TourEntity("Sapa Mist Escape", "3 Days - 2 Nights", 95.00, "Ha Noi", "Sapa", false, "Bus", null, 30)); // ID 3
        addWithId(new TourEntity("Hanoi Old Quarter", "1 Day", 49.99, "Ha Noi", "Hanoi", false, "Walking", null, 15)); // ID 4
        addWithId(new TourEntity("Nha Trang Mini", "4 Days - 3 Nights", 250.00, "HCM City", "Nha Trang", true, "Airplane", null, 35)); // ID 5
        addWithId(new TourEntity("Central Heritage", "5 Days - 4 Nights", 320.50, "Da Nang", "Hue", true, "Airplane", null, 25)); // ID 6
        addWithId(new TourEntity("Da Lat Flower", "6 Days - 5 Nights", 410.75, "HCM City", "Da Lat", true, "Airplane", null, 20)); // ID 7
        addWithId(new TourEntity("Mekong River Tour", "7 Days - 6 Nights", 499.00, "HCM City", "Can Tho", false, "Cruise", null, 18)); // ID 8
        addWithId(new TourEntity("Vietnam Grand Tour", "10 Days - 9 Nights", 980.00, "Ha Noi", "HCM City", true, "Airplane", null, 12)); // ID 9
        addWithId(new TourEntity("Southeast Asia", "14 Days - 13 Nights", 1500.00, "Ha Noi", "Bangkok", true, "Airplane", null, 10)); // ID 10
        addWithId(new TourEntity("Highlands Trek", "8 Days - 7 Nights", 550.00, "HCM City", "Dalat", false, "Bus", null, 15)); // ID 11
        addWithId(new TourEntity("Ninh Binh Karst", "Weekend Getaway", 150.00, "Ha Noi", "Ninh Binh", false, "Bus", null, 45)); // ID 12
        addWithId(new TourEntity("Vung Tau Relax", "Weekend Getaway", 180.00, "HCM City", "Vung Tau", false, "Car", null, 50)); // ID 13
    }

    private void addWithId(TourEntity tour) {
        int id = mockTours.size() + 1;
        tour.setId(id);
        mockTours.add(tour);
    }


    // --- 3. Thêm Mock Schedules ---
    private void addMockSchedules() {
        Calendar cal = Calendar.getInstance();

        // --- Schedule cho Tour 1: Ha long Bay (ID=1) ---
        cal.set(2025, Calendar.NOVEMBER, 10, 8, 0);
        Date d1_1 = cal.getTime();
        cal.set(2025, Calendar.NOVEMBER, 10, 12, 30);
        Date d1_2 = cal.getTime();
        cal.set(2025, Calendar.NOVEMBER, 11, 9, 0);
        Date d1_3 = cal.getTime();

        addSchedule(new TourScheduleEntity("Hanoi Departure", d1_1, "Khách sạn Metropole, Hà Nội", null,
                "Tập trung tại điểm hẹn, xe bus khởi hành.", 1, 21.0285, 105.8569));
        addSchedule(new TourScheduleEntity("Tuan Chau Harbor", d1_2, "Cảng Tuần Châu, Hạ Long", null,
                "Lên tàu, ăn trưa và nhận phòng.", 1, 20.9333, 107.0500));
        addSchedule(new TourScheduleEntity("Sung Sot Cave", d1_3, "Hang Sửng Sốt, Vịnh Hạ Long", null,
                "Tham quan hang Sửng Sốt, một trong những hang động đẹp nhất.", 1, 20.8499, 107.0805));


        // --- Schedule cho Tour 2: Da Nang (ID=2) ---
        cal.set(2025, Calendar.NOVEMBER, 15, 14, 0);
        Date d2_1 = cal.getTime();
        cal.set(2025, Calendar.NOVEMBER, 15, 16, 30);
        Date d2_2 = cal.getTime();

        addSchedule(new TourScheduleEntity("My Khe Beach", d2_1, "Bãi biển Mỹ Khê, Đà Nẵng", null,
                "Đến Đà Nẵng, nhận phòng khách sạn, tự do tắm biển.", 2, 16.0544, 108.2388));
        addSchedule(new TourScheduleEntity("Marble Mountains", d2_2, "Ngũ Hành Sơn, Đà Nẵng", null,
                "Tham quan khu danh thắng Ngũ Hành Sơn.", 2, 16.0049, 108.2638));

        // --- Schedule cho Tour 3: Sapa Mist Escape (ID=3) ---
        cal.set(2025, Calendar.NOVEMBER, 20, 6, 0);
        Date d3_1 = cal.getTime();
        cal.set(2025, Calendar.NOVEMBER, 20, 9, 30);
        Date d3_2 = cal.getTime();

        addSchedule(new TourScheduleEntity("Arrive in Sapa", d3_1, "Thị xã Sapa", null,
                "Đến Sapa, ăn sáng và nghỉ ngơi.", 3, 22.3366, 103.8440));
        addSchedule(new TourScheduleEntity("Cat Cat Village", d3_2, "Bản Cát Cát", null,
                "Đi bộ trekking tham quan bản Cát Cát của người H'Mông.", 3, 22.3275, 103.8341));
    }

    private void addSchedule(TourScheduleEntity schedule) {
        int id = mockSchedules.size() + 1;
        schedule.setId(id);
        mockSchedules.add(schedule);
    }


    // --- 4. Thêm Mock Bookings ---
    private void addMockBookings() {
        Calendar cal = Calendar.getInstance();

        // --- Booking cho User ID 2 (John Doe) ---

        // Booking 1: User 2, Tour 1 (Ha Long), 2 người lớn, 1 trẻ em, Đã xác nhận
        cal.set(2025, Calendar.NOVEMBER, 17);
        Date d1 = cal.getTime();
        addWithId(new BookingOrderEntity(d1, 2, 1, STATUS_CONFIRMED, 1, 2)); // ID 1

        // Booking 2: User 2, Tour 5 (Nha Trang), 1 người lớn, 0 trẻ em, Đang chờ
        cal.set(2025, Calendar.DECEMBER, 5);
        Date d2 = cal.getTime();
        addWithId(new BookingOrderEntity(d2, 1, 0, STATUS_PENDING, 5, 2)); // ID 2


        // --- Booking cho User ID 3 (Jane Smith) ---

        // Booking 3: User 3, Tour 3 (Sapa), 2 người lớn, 2 trẻ em, Đã xác nhận
        cal.set(2025, Calendar.NOVEMBER, 30);
        Date d3 = cal.getTime();
        addWithId(new BookingOrderEntity(d3, 2, 2, STATUS_CONFIRMED, 3, 3)); // ID 3

        // Booking 4: User 3, Tour 2 (Da Nang), 1 người lớn, 0 trẻ em, Đã hủy
        // Ngày trong quá khứ
        cal.set(2025, Calendar.NOVEMBER, 1);
        Date d4 = cal.getTime();
        addWithId(new BookingOrderEntity(d4, 1, 0, STATUS_CANCELLED, 2, 3)); // ID 4

        cal.set(2025, Calendar.JANUARY, 17);
        Date d5 = cal.getTime();
        addWithId(new BookingOrderEntity(d5, 2, 1, STATUS_CONFIRMED, 1, 2));

        cal.set(2025, Calendar.FEBRUARY, 17);
        Date d6 = cal.getTime();
        addWithId(new BookingOrderEntity(d6, 2, 1, STATUS_CONFIRMED, 1, 2));

        cal.set(2025, Calendar.DECEMBER, 17);
        Date d7 = cal.getTime();
        addWithId(new BookingOrderEntity(d7, 2, 1, STATUS_CONFIRMED, 1, 2));
    }

    private void addWithId(BookingOrderEntity booking) {
        int id = mockBookings.size() + 1;
        booking.setId(id);
        mockBookings.add(booking);
    }
}