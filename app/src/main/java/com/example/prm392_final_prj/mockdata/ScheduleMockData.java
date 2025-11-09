package com.example.prm392_final_prj.mockdata;

import com.example.prm392_final_prj.entity.TourScheduleEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleMockData {
    private List<TourScheduleEntity> mockData;

    public ScheduleMockData() {
        addMockDataForDemo();
    }

    public List<TourScheduleEntity> getAllSchedules() {
        return mockData;
    }

    public List<TourScheduleEntity> getSchedulesForTour(int tourId) {
        List<TourScheduleEntity> schedules = new ArrayList<>();
        for (TourScheduleEntity schedule : mockData) {
            if (schedule.getTourId() == tourId) {
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    public TourScheduleEntity getScheduleById(int id) {
        for (TourScheduleEntity schedule : mockData) {
            if (schedule.getId() == id) {
                return schedule;
            }
        }
        return null;
    }

    // Hàm tiện ích để tạo ngày/giờ
    private Date createTime(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Hàm tiện ích để thêm và gán ID
    private void addWithId(TourScheduleEntity schedule) {
        int id = mockData.size() + 1;
        schedule.setId(id);
        mockData.add(schedule);
    }

    private void addMockDataForDemo() {
        mockData = new ArrayList<>();

        // --- MOCK DATA cho Tour ID = 1 (Ví dụ: Ha Long Bay) ---
        addWithId(new TourScheduleEntity("Khách sạn A", createTime(8, 0), "56 Quán A, Tỉnh Ha Long", null, "Nhận phòng và ăn sáng.", 1, 20.983944, 107.032130));
        addWithId(new TourScheduleEntity("Trung tâm B", createTime(10, 0), "123 Đường B, Tỉnh Ha Long", null, "Tham quan trung tâm mua sắm.", 1, 20.995166, 107.054321));
        addWithId(new TourScheduleEntity("Quảng trường C", createTime(13, 30), "456 Phố C, Tỉnh Ha Long", null, "Ăn trưa và chụp ảnh.", 1, 20.975000, 107.025000));
        addWithId(new TourScheduleEntity("Nhà hàng ABC", createTime(18, 0), "789 Đại lộ D, Tỉnh Ha Long", null, "Ăn tối và nghỉ ngơi.", 1, 20.988000, 107.048500));

        // --- MOCK DATA cho Tour ID = 2 (Ví dụ: Da Nang) ---
        addWithId(new TourScheduleEntity("Sân bay Da Nang", createTime(9, 30), "Đường Nguyễn Văn Linh, Đà Nẵng", null, "Đến Đà Nẵng, check-in khách sạn.", 2, 16.0544, 108.2022));
        addWithId(new TourScheduleEntity("Bãi biển Mỹ Khê", createTime(15, 0), "Đường Võ Nguyên Giáp, Đà Nẵng", null, "Tắm biển và thư giãn.", 2, 16.0689, 108.2432));
        addWithId(new TourScheduleEntity("Cầu Rồng", createTime(20, 0), "Đường Bạch Đằng, Đà Nẵng", null, "Xem Cầu Rồng phun lửa/nước.", 2, 16.0592, 108.2238));

        // --- MOCK DATA cho Tour ID = 3 (Ví dụ: Sapa) ---
        addWithId(new TourScheduleEntity("Thị trấn Sapa", createTime(7, 0), "Trung tâm Sapa", null, "Ăn sáng và chuẩn bị hành trình.", 3, 22.3333, 103.8333));
        addWithId(new TourScheduleEntity("Đỉnh Fansipan", createTime(11, 0), "Núi Fansipan, Sapa", null, "Chinh phục 'Nóc nhà Đông Dương'.", 3, 22.3040, 103.7744));

        // --- MOCK DATA cho Tour ID = 4 (Ví dụ: Ha Noi) ---
        addWithId(new TourScheduleEntity("Hồ Hoàn Kiếm", createTime(8, 0), "Quận Hoàn Kiếm, Hà Nội", null, "Tham quan Hồ Gươm và Đền Ngọc Sơn.", 4, 21.0285, 105.8542));
        addWithId(new TourScheduleEntity("Văn Miếu Quốc Tử Giám", createTime(10, 30), "Quận Đống Đa, Hà Nội", null, "Tham quan trường đại học đầu tiên.", 4, 21.0285, 105.8504));
        addWithId(new TourScheduleEntity("Lăng Chủ tịch Hồ Chí Minh", createTime(14, 0), "Quận Ba Đình, Hà Nội", null, "Viếng Lăng Bác.", 4, 21.0367, 105.8344));

        // --- MOCK DATA cho Tour ID = 5 (Ví dụ: Nha Trang) ---
        addWithId(new TourScheduleEntity("Đảo Vinpearl", createTime(10, 0), "Đảo Hòn Tre, Nha Trang", null, "Vui chơi giải trí tại Vinpearl Land.", 5, 12.2388, 109.2132));
        addWithId(new TourScheduleEntity("Tháp Bà Ponagar", createTime(15, 0), "Đường 2 tháng 4, Nha Trang", null, "Tham quan di tích Chăm Pa cổ.", 5, 12.2855, 109.1950));
    }
}