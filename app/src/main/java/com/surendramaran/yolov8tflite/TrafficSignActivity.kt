//package com.surendramaran.yolov8tflite
//
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.view.View
//import android.view.WindowInsets
//import android.view.WindowInsetsController
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.surendramaran.yolov8tflite.adapter.TrafficSignAdapter
//import com.surendramaran.yolov8tflite.model.TrafficSign
//
//class TrafficSignActivity : AppCompatActivity() {
//
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var trafficSignAdapter: TrafficSignAdapter
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.traffic_sign) // Bind to the XML layout with RecyclerView
////
////        // Initialize RecyclerView
////        recyclerView = findViewById(R.id.food_list)
////        recyclerView.layoutManager = LinearLayoutManager(this)
////
////        // Sample list of traffic signs
////        val trafficSignList = listOf(
////            TrafficSign("Stop Sign", "001", "A sign indicating vehicles must stop", "https://static.vecteezy.com/system/resources/previews/003/809/110/non_2x/cute-police-chibi-character-design-vector.jpg"),
////            TrafficSign("Yield Sign", "002", "A sign indicating vehicles must yield", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-vj1eoRw2d-3o2AKCBG85fk_NK74Jgd-meA&s"),
////            TrafficSign("No Entry", "003", "A sign indicating no entry", "https://media.istockphoto.com/id/883910278/vector/no-entry-traffic-sign-isolated-on-the-white-illustration-vector.jpg?s=612x612&w=0&k=20&c=xvqwAkqtuhfsg0mC71g3y9StAggaOGHjIjqSXcC6Xbg="),
////            TrafficSign("Speed Limit 50", "004", "A sign indicating speed limit of 50", "https://media.istockphoto.com/id/1191601898/vector/speed-limit-50-sign.jpg?s=1024x1024&w=is&k=20&c=LKiW0N_YjduUVJDS1mlQ_Dwhc6jovY80OKWmqnoDMxM=")
////        )
////
////        // Set up adapter
////        trafficSignAdapter = TrafficSignAdapter(trafficSignList) { trafficSign ->
////            // Handle click events here, e.g., open a new activity or show a Toast
////            // For example, to show a Toast:
////            // Toast.makeText(this, "Clicked: ${trafficSign.name}", Toast.LENGTH_SHORT).show()
////        }
////
////        // Bind adapter to RecyclerView
////        recyclerView.adapter = trafficSignAdapter
////    }
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var trafficSignAdapter: TrafficSignAdapter
//
//    private lateinit var btnMainDishes: Button
//    private lateinit var btnSalad: Button
//    private lateinit var btnDrinks: Button
//    private lateinit var backIcon: ImageView  // Khai báo nút back icon
//
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.traffic_sign) // Bind to the XML layout with RecyclerView
//
//        // Make the app fullscreen
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.apply {
//                hide(WindowInsets.Type.statusBars())
//                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        } else {
//            @Suppress("DEPRECATION")
//            window.decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    )
//        }
//
//        // Initialize RecyclerView
//        recyclerView = findViewById(R.id.food_list)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Find buttons for categories
//        btnMainDishes = findViewById(R.id.btn_main_dishes)
//        btnSalad = findViewById(R.id.btn_salad)
//        btnDrinks = findViewById(R.id.btn_drinks)
//        backIcon = findViewById(R.id.back_icon) // Thêm nút back icon
//
//        backIcon.setOnClickListener {
//            finish()  // Quay lại màn hình trước (MainActivity)
//        }
//
//
//        // Load default list (Biển báo cấm)
//        loadTrafficSignList(getProhibitedSigns())
//        highlightSelectedButton(btnMainDishes)
//
//        // Set up category button click listeners
//        btnMainDishes.setOnClickListener {
//            loadTrafficSignList(getProhibitedSigns())
//            highlightSelectedButton(btnMainDishes)
//        }
//
//        btnSalad.setOnClickListener {
//            loadTrafficSignList(getGuideSigns())
//            highlightSelectedButton(btnSalad)
//        }
//
//        btnDrinks.setOnClickListener {
//            loadTrafficSignList(getWarningSigns())
//            highlightSelectedButton(btnDrinks)
//        }
//    }
//
//    private fun loadTrafficSignList(trafficSignList: List<TrafficSign>) {
//        if (!::trafficSignAdapter.isInitialized) {
//            // Khởi tạo Adapter nếu chưa được khởi tạo
//            trafficSignAdapter = TrafficSignAdapter(trafficSignList) { trafficSign ->
//                showTrafficSignPopup(trafficSign) // Hiển thị popup khi nhấn vào mục
//            }
//            recyclerView.adapter = trafficSignAdapter
//        } else {
//            // Cập nhật dữ liệu nếu Adapter đã được khởi tạo
//            trafficSignAdapter.updateData(trafficSignList)
//        }
//
//        // Cuộn về đầu danh sách
//        recyclerView.scrollToPosition(0)
//    }
//    private fun showTrafficSignPopup(trafficSign: TrafficSign) {
//        // Inflate layout custom dialog
//        val dialogView = layoutInflater.inflate(R.layout.dialog_traffic_sign, null)
//
//        // Ánh xạ các view trong layout
//        val signName: TextView = dialogView.findViewById(R.id.dialog_sign_name)
//        val signImage: ImageView = dialogView.findViewById(R.id.dialog_sign_image)
//        val signDescription: TextView = dialogView.findViewById(R.id.dialog_sign_description)
//        val closeButton: Button = dialogView.findViewById(R.id.dialog_close_button)
//
//        // Gán dữ liệu
//        signName.text = trafficSign.name
//        signDescription.text = trafficSign.description
//        Glide.with(this)
//            .load(trafficSign.imagePath)
//            .into(signImage)
//
//        // Tạo AlertDialog với layout tùy chỉnh
//        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
//            .setView(dialogView)
//            .create()
//
//        // Xử lý sự kiện cho nút Đóng
//        closeButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//
//
//
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun highlightSelectedButton(selectedButton: Button) {
//        // Reset button colors
//        btnMainDishes.setBackgroundColor(getColor(R.color.default_button_color))
//        btnSalad.setBackgroundColor(getColor(R.color.default_button_color))
//        btnDrinks.setBackgroundColor(getColor(R.color.default_button_color))
//
//        // Set highlighted color for the selected button
//        selectedButton.setBackgroundColor(getColor(R.color.selected_button_color))
//    }
//    // Danh sách biển báo cấm
//    private fun getProhibitedSigns(): List<TrafficSign> {
//        return listOf(
//            TrafficSign(0, "Cấm đi ngược chiều", "cam_di_nguoc_chieu", "Biển báo này chỉ dẫn phương tiện không được phép di chuyển ngược chiều. Phạm vi áp dụng cho các đường một chiều và khu vực giao thông hạn chế.", "https://thietbigiaothong247.com/wp-content/uploads/2017/04/bien-cam-di-nguoc-chieu.jpg"),
//            TrafficSign(1, "Cấm dừng và đỗ xe", "cam_dung_va_do_xe", "Biển báo này quy định các phương tiện không được dừng hoặc đỗ tại vị trí đặt biển, nhằm đảm bảo lưu thông thông thoáng và an toàn cho khu vực.", "https://bizweb.dktcdn.net/100/378/087/products/bien-bao-giao-thong.jpg?v=1583945216690"),
//            TrafficSign(2, "Cấm rẽ trái", "cam_re_trai", "Biển này yêu cầu tất cả các phương tiện không được phép rẽ trái tại ngã tư hoặc vị trí cụ thể để tránh xung đột giao thông và tai nạn.", "https://www.thietbithinhphat.com/wp-content/uploads/2023/01/Bien-cam-re-trai-510x510-1.jpg"),
//            TrafficSign(3, "Giới hạn tốc độ", "gioi_han_toc_do", "Biển báo này quy định tốc độ tối đa cho phép mà các phương tiện có thể di chuyển trong khu vực hoặc đoạn đường được chỉ định, nhằm bảo đảm an toàn giao thông.", "https://bizweb.dktcdn.net/100/415/690/files/cac-bien-bao-toc-do-2.jpg?v=1677145397513"),
//            TrafficSign(4, "Biển báo cấm", "bien_bao_cam", "Biển này thông báo một lệnh cấm chung cho các phương tiện hoặc hành vi cụ thể như cấm xe máy, xe tải, hoặc cấm vượt tại khu vực nhất định.", "https://carpla.vn/blog/wp-content/uploads/2023/11/cac-loai-bien-bao-cam.jpg"),
//            TrafficSign(5, "Biển báo nguy hiểm", "bien_nguy_hiem", "Biển này cảnh báo các tình huống giao thông nguy hiểm như đường cong gấp, lối đi có dốc lớn, hoặc khu vực dễ sạt lở, yêu cầu các tài xế giảm tốc độ và chú ý quan sát.", "https://image3.luatvietnam.vn/uploaded/images/original/2024/03/27/bien-bao-nguy-hiem-la-gi_2703152112.jpg"),
//            TrafficSign(6, "Biển hiệu lệnh", "bien_hieu_lenh", "Biển này chỉ dẫn bắt buộc về hướng đi hoặc hành vi mà người điều khiển phương tiện cần tuân thủ, như đi thẳng, rẽ phải, hoặc đi vào khu vực hạn chế.", "https://cdn.thuvienphapluat.vn/uploads/tintuc/2022/10/15/bien-bao-R.301.jpg"),
//            TrafficSign(14, "updateNamee", "updateCode", "updateDescription", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1730136225/tktbeonzg5x6t3ljuuys.png"),
//            TrafficSign(18, "duy đỗ bá", "duy_đo_ba", "ddddddddddddddddddđ", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1730194939/n03eglmfwcrpq5lzqxeo.png"),
//            TrafficSign(20, "Đường cấm", "đuong_cam", "Cấm tất cả các phương tiện cơ giới, thô sơ đi lại cả hai hướng, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688393/aecuokh8dsphxjfr75qw.png"),
//            TrafficSign(22, "Cấm ô tô", "cam_o_to", "Cấm tất cả các loại xe cơ giới kể cả môtô 3 bánh có thùng, trừ môtô 2 bánh, xe gắn máy và xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688394/knvylwpxo4mzb3vkaroi.png"),
//            TrafficSign(23, "Cấm ô tô rẽ phải", "cam_o_to_re_phai", "Cấm xe cơ giới bao gồm cả môtô 3 bánh có thùng RẼ PHẢI, trừ môtô 2 bánh, xe gắn máy và xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688396/h6rtlakilwlm5xkogvyj.png"),
//            TrafficSign(24, "Cấm ô tô rẽ trái", "cam_o_to_re_trai", "Cấm xe cơ giới bao gồm cả mô tô 3 bánh có thùng RẼ TRÁI, trừ mô tô 2 bánh, xe gắn máy và xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688396/fli0rwlcbed1zu1jvizx.png"),
//            TrafficSign(25, "Cấm mô tô", "cam_mo_to", "Cấm tất cả các loại mô tô, trừ mô tô được ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688397/hacqngls3hwva840d0l5.png"),
//            TrafficSign(26, "Cấm ô tô và mô tô", "cam_o_to_va_mo_to", "Cấm tất cả các loại xe cơ giới và mô tô đi qua, trừ xe gắn máy và xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688398/qkyk7ojl5ogexfyxoh4i.png"),
//            TrafficSign(27, "Cấm xe tải", "cam_xe_tai", "Cấm tất cả các loại ô tô chở hàng có trọng tải từ 1,5 tấn trở lên, trừ xe ưu tiên. Biển có hiệu lực cả với xe máy kéo, xe máy chuyên dùng.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688399/jyuo79t6gelhpyxci2gs.png"),
//            TrafficSign(28, "Cấm xe tải từ 2,5 tấn", "cam_xe_tai_tu_25_tan", "Cấm xe có tổng trọng lượng (trọng lượng xe cộng hàng) vượt quá con số ghi trên biển. Biển có hiệu lực cả với xe máy kéo, xe máy chuyên dùng.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688400/v8j18brbjmcagaqnq4ac.png"),
//            TrafficSign(29, "Cấm xe chở hàng nguy hiểm", "cam_xe_cho_hang_nguy_hiem", "Cấm xe chở hàng nguy hiểm", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688401/f6tumgwtzyisbvdxkwy4.png"),
//            TrafficSign(30, "Cấm ô tô khách và ô tô tải", "cam_o_to_khach_va_o_to_tai", "Cấm ô tô chở khách và các loại ô tô tải kể cả máy kéo và xe máy thi công chuyên dùng, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688402/dk94razdmxz7ij4fml4n.png"),
//            TrafficSign(31, "Cấm ô tô, máy kéo kéo moóc và sơ mi rơ moóc", "cam_o_to_may_keo_keo_mooc_va_so_mi_ro_mooc", "Cấm tất cả các loại xe cơ giới kéo theo rơ-moóc kể cả mô tô, máy kéo, ô tô khách kéo theo rơ-moóc đi qua, trừ loại ô tô sơ-mi rơ-moóc và các xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688403/udknpvulaus4id3herun.png"),
//            TrafficSign(32, "Cấm máy kéo", "cam_may_keo", "Cấm tất cả các loại máy kéo, kể cả máy kéo bánh hơi và bánh xích.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688404/vso3dk84wfiyqpat87lc.png"),
//            TrafficSign(33, "Cấm đi xe đạp", "cam_đi_xe_đap", "Cấm xe đạp đi qua. Biển không có giá trị cấm người dắt xe đạp.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688405/owbztgbhm6bs4z5lcpqn.png"),
//            TrafficSign(34, "Cấm xe đạp thồ", "cam_xe_đap_tho", "Cấm xe đạp thồ đi qua. Biển không cấm người dắt loại xe này.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688406/mv12f1abgr7hihpvcv4w.png"),
//            TrafficSign(35, "Cấm xe kéo rơ moóc", "cam_xe_keo_ro_mooc", "Cấm các loại xe cơ giới kéo theo rơ-moóc đi qua, trừ các loại xe ưu tiên hoặc xe sơ-mi rơ-moóc.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688410/vlqbr7p1rleiyabvxtow.png"),
//            TrafficSign(36, "Cấm xe máy kéo", "cam_xe_may_keo", "Cấm tất cả các loại xe máy kéo đi qua, trừ xe máy kéo ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688411/yjlakbqt6jvbl9xr8kiw.png"),
//            TrafficSign(37, "Cấm xe cơ giới kéo theo người", "cam_xe_co_gioi_keo_theo_nguoi", "Cấm các loại xe cơ giới kéo theo người, bao gồm cả xe đạp, xe gắn máy có người kéo theo.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688412/xhcx8un7rxj9lsz7jbtf.png"),
//            TrafficSign(38, "Cấm vượt", "cam_vuot", "Cấm tất cả các phương tiện vượt xe khác tại khu vực có biển báo này. Biển giúp giảm thiểu tình trạng tai nạn giao thông khi có nhiều phương tiện di chuyển gần nhau.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688413/dvo6dk4fptfjdsw4xchh.png"),
//            TrafficSign(39, "Cấm quay đầu", "cam_quay_dau", "Cấm tất cả các phương tiện quay đầu tại điểm có biển báo này, nhằm hạn chế tai nạn và đảm bảo an toàn giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688414/k0z7h2dckr7g0j5dc5l1.png"),
//            TrafficSign(40, "Cấm dừng xe tải", "cam_dung_xe_tai", "Cấm xe tải dừng hoặc đỗ tại vị trí có biển báo này, nhằm đảm bảo lưu thông và giảm tắc nghẽn giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688415/wvd9wmct2slzotfnbkz7.png"),
//            TrafficSign(41, "Cấm đỗ xe trái phép", "cam_do_xe_trai_phep", "Cấm đỗ xe trái phép tại khu vực có biển báo này. Vi phạm có thể bị phạt tiền hoặc xử lý hành chính.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688416/kfhcajd4j3efluh8mrw1.png"),
//            TrafficSign(42, "Cấm đi xe buýt", "cam_di_xe_buyt", "Cấm các loại xe buýt đi qua khu vực này, trừ các xe buýt ưu tiên hoặc xe buýt vào bến.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688417/d0zhntwscfmlufyqsxsz.png"),
//            TrafficSign(43, "Cấm dừng xe khách", "cam_dung_xe_khach", "Cấm dừng xe khách tại vị trí có biển báo này, nhằm đảm bảo lưu thông và tránh tắc nghẽn giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688418/lvnsryzgbu7f9t1t3rca.png"),
//            TrafficSign(44, "Cấm xe chở động vật", "cam_xe_cho_dong_vat", "Cấm các phương tiện vận chuyển động vật đi qua khu vực có biển báo này, nhằm bảo vệ môi trường và đảm bảo an toàn giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688419/qjqexqb3gw93v2j4khmo.png"),
//            TrafficSign(45, "Cấm xe khách và xe tải", "cam_xe_khach_va_xe_tai", "Cấm xe khách và xe tải vào khu vực này, nhằm giảm thiểu ùn tắc và đảm bảo trật tự giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688420/k8t7q1lb1wqzzlwe2tmk.png"),
//            TrafficSign(77, "Cấm ô tô tải rẽ phải và quay đầu xe", "cam_o_to_tai_re_phai_va_quay_đau_xe", "Cấm ô tô tải rẽ phải và quay đầu xe, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689037/fyyvncn6mjhnupslgwjw.png"),
//            TrafficSign(78, "Cấm ô tô tải rẽ trái và quay đầu xe", "cam_o_to_tai_re_trai_va_quay_đau_xe", "Cấm ô tô tải rẽ trái và quay đầu xe, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689038/jclmqqkxrgcmnbptqlxa.png"),
//            TrafficSign(79, "Cấm tất cả các loại xe quay đầu và rẽ phải", "cam_tat_ca_cac_loai_xe_quay_đau_va_re_phai", "Cấm tất cả các loại xe quay đầu và rẽ phải, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689039/bc7lwl2g1ot1q2b5zxr7.png"),
//            TrafficSign(80, "Cấm tất cả các loại xe quay đầu và rẽ trái", "cam_tat_ca_cac_loai_xe_quay_đau_va_re_trai", "Cấm tất cả các loại xe quay đầu và rẽ trái, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689040/zgxqkg3w8oevrsjsqptu.png"),
//            TrafficSign(81, "Cấm ô tô tải quay đầu và rẽ phải", "cam_o_to_tai_quay_đau_va_re_phai", "Cấm ô tô tải quay đầu và rẽ phải, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689041/p2rggbldlxvgr0jfzlxw.png"),
//            TrafficSign(82, "Cấm ô tô tải quay đầu và rẽ trái", "cam_o_to_tai_quay_đau_va_re_trai", "Cấm ô tô tải quay đầu và rẽ trái, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689042/sgll3vo1e75g8b7g7c5h.png"),
//            TrafficSign(83, "Cấm tất cả các loại xe quay đầu và rẽ phải và trái", "cam_tat_ca_cac_loai_xe_quay_đau_va_re_phai_va_trai", "Cấm tất cả các loại xe quay đầu và rẽ phải và trái, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689043/z3i2xl1hz7vvnvjwfdwg.png"),
//            TrafficSign(84, "Cấm đi thẳng và rẽ phải và trái", "cam_đi_thang_va_re_phai_va_trai", "Cấm tất cả các loại xe đi thẳng và rẽ phải và trái, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689044/lvs9mt9vc0gucjjkldtb.png"),
//            TrafficSign(85, "Cấm ô tô tải rẽ trái và quay đầu xe và rẽ phải", "cam_o_to_tai_re_trai_va_quay_đau_xe_va_re_phai", "Cấm ô tô tải rẽ trái và quay đầu xe và rẽ phải, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689045/ei5gftwmr8chfgnvd5gm.png"),
//            TrafficSign(86, "Cấm tất cả các loại xe quay đầu và rẽ trái và phải", "cam_tat_ca_cac_loai_xe_quay_đau_va_re_trai_va_phai", "Cấm tất cả các loại xe quay đầu và rẽ trái và phải, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689046/bv7nxdkaokcveziqpcwi.png")
//
//        )
//    }
//
//    // Danh sách biển chỉ dẫn
//    private fun getGuideSigns(): List<TrafficSign> {
//        return listOf(
//            TrafficSign(230, "Đường phía trước có làn đường dành cho ô tô khách", "đuong_phia_truoc_co_lan_đuong_danh_cho_o_to_khach", "Để chỉ dẫn cho người tham gia giao thông biết đường phía trước có làn đường dành riêng cho ô tô khách theo chiều ngược lại, đặt biển số I.413a Đường phía trước có làn đường dành cho ô tô khách. Biển được đặt ở nơi đường giao nhau đầu đường một chiều mà hướng ngược chiều có ô tô khách được phép chạy.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780735/wfvzlslftytfcwyi6rv4.png"),
//        TrafficSign(231, "Chỉ hướng đường", "chi_huong_đuong", "Ở các nơi đường bộ giao nhau, đặt biển số I.414 (a, b, c, d) Chỉ hướng đường để chỉ dẫn hướng đường đến các địa danh, khu dân cư. Trên biển cần chỉ dẫn cả số hiệu (tên) đường và cự ly (làm tròn đến kilômét, nếu cự ly < 1,0 km thì làm tròn đến 100 m):", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780736/eqozohqhypguoexeravg.png"),
//        TrafficSign(232, "Mũi tên chỉ hướng đi", "mui_ten_chi_huong_đi", "Trong khu đông dân cư, hoặc ở các đường giao nhau để chỉ dẫn hướng đi đến một địa danh lân cận tiếp theo và khoảng cách (làm tròn đến km) đến nơi đó, cần phải đặt biển số I.415 Mũi tên chỉ hướng đi. Trong trường hợp địa danh gần thì không nhất thiết phải ghi khoảng cách.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780738/lfr2clnlfcitjpt5h4bi.png"),
//        TrafficSign(233, "Đường tránh", "đuong_tranh", "Để chỉ dẫn lối đi đường tránh, đường vòng trong trường hợp đường cấm vì lý do đường, cầu bị tắc hoặc thi công và cấm một số loại xe đi qua, đặt biển số I.416 Đường tránh trước các đường giao nhau.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780739/n7rdspyb3uwq11gn6qvt.png"),
//        TrafficSign(234, "Chỉ hướng đường phải đi cho từng loại xe", "chi_huong_đuong_phai_đi_cho_tung_loai_xe", "Ở các đường giao nhau trong trường hợp cấm hoặc hạn chế một số loại xe, khi cần thiết có thể đặt biển chỉ hướng đường phải đi cho từng loại xe đến một khu dân cư tiếp theo, đặt biển số I.417 (a, b, c) Chỉ hướng đường phải đi cho từng loại xe", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780739/rozh6qcnchgcn9ftuuch.png"),
//        TrafficSign(235, "Lối đi ở những vị trí cấm rẽ", "loi_đi_o_nhung_vi_tri_cam_re", "Để chỉ lối đi ở các nơi đường giao nhau bị cấm rẽ, đặt biển số I.418 Lối đi ở những vị trí cấm rẽ. Biển được đặt ở nơi đường giao nhau trước đường cấm rẽ.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780740/ypoca8kxdhiedx2sfro3.png"),
//        TrafficSign(236, "Chỉ dẫn địa giới", "chi_dan_đia_gioi", "Để chỉ dẫn địa giới hành chính giữa các thành phố, tỉnh, huyện, đặt biển số I.419(a,b) Chỉ dẫn địa giới", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780741/mbtrj89lgfubvkyukdwd.png"),
//        TrafficSign(237, "Di tích lịch sử", "di_tich_lich_su", "Để chỉ dẫn những nơi có di tích lịch sử hoặc những nơi có danh lam thắng cảnh, những nơi có thể tham quan... ở hai ven đường.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780743/uh6ob0hpljjgjrhfjwah.png"),
//        TrafficSign(238, "Vị trí người đi bộ sang ngang", "vi_tri_nguoi_đi_bo_sang_ngang", "Để chỉ dẫn người đi bộ và người tham gia giao thông biết vị trí dành cho người đi bộ sang ngang, đặt biển số I.423 (a,b) Vị trí người đi bộ sang ngang", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780744/hwridhes94wdogverjfa.png"),
//        TrafficSign(239, "Cầu vượt qua đường cho người đi bộ", "cau_vuot_qua_đuong_cho_nguoi_đi_bo", "Để chỉ dẫn cho người đi bộ sử dụng cầu vượt qua đường, đặt biển số I.424 (a,b) Cầu vượt qua đường cho người đi bộ và I.424 (c,d) Hầm chui qua đường cho người đi bộ.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780745/zp8fyujkuq2eqjv2j8d2.png"),
//        TrafficSign(240, "Hết đoạn đường ưu tiên", "het_đoan_đuong_uu_tien", "Đến hết đoạn đường quy định là ưu tiên, đặt biển số I.402 Hết đoạn đường ưu tiên", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780747/k1pkbsrm1k11i203ufie.png"),
//        TrafficSign(241, "Trạm kiểm tra tải trọng xe", "tram_kiem_tra_tai_trong_xe", "Để chỉ dẫn nơi đặt trạm kiểm tra tải trọng xe, đặt biển số I.427b Trạm kiểm tra tải trọng xe", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780748/puoovw3hln0mpeh2ykym.png"),
//        TrafficSign(242, "Trạm sửa chữa", "tram_sua_chua", "Để chỉ dẫn nơi đặt xưởng, trạm chuyên phục vụ sửa chữa ô tô, xe máy hỏng trên đường, đặt biển số I.427a Trạm sửa chữa", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780749/gajeqjhwqnqyeokfxyzz.png"),
//        TrafficSign(243, "Xe kéo rơ-moóc", "xe_keo_romooc", "Để báo hiệu xe có kéo moóc hoặc xe kéo xe, đặt biển số I.443 Xe kéo rơ-moóc", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780750/n7rskxqu5pcdxkfliunj.png"),
//        TrafficSign(244, "Biển báo cầu vượt liên thông", "bien_bao_cau_vuot_lien_thong", "Biển đặt tại vị trí trước khi vào cầu vượt có tổ chức giao thông liên thông giữa các tuyến. Tùy theo nút giao mà bố trí biển số I.447a, I.447b, I.445c, I.447d cho phù hợp. Tại các lối rẽ thì sử dụng biển I.414c, d để báo các hướng đi.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780751/xv9fym4exevb6rkdxthm.jpg"),
//        TrafficSign(245, "Đường cụt", "đuong_cut", "Đường cụt là những đường xe không thể tiếp tục đi qua được. Những đường cụt có thể là những ngõ cụt (ở trong khu đông dân cư), đường hoặc cầu bị đứt do thiên tai, địch hoạ hoặc đường tránh dự phòng mà mà tại vị trí vượt sông, suối chưa có phương tiện vượt sông, đường đi vào cầu nhưng cầu hỏng v.v...", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780752/qpof2tie7pbszbjbwsq8.png"),
//        TrafficSign(246, "Được ưu tiên qua đường hẹp", "đuoc_uu_tien_qua_đuong_hep", "Để chỉ dẫn cho người tham gia giao thông cơ giới biết mình được quyền ưu tiên đi trước trên đoạn đường hẹp, đặt biển số I.406 Được ưu tiên qua đường hẹp", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780753/kdoiztvyj0sdojgjf3er.png"),
//        TrafficSign(247, "Đường một chiều", "đuong_mot_chieu", "Để chỉ dẫn những đoạn đường chạy một chiều, đặt biển số I.407(a,b,c) Đường một chiều. Biển số I.407a đặt sau nơi đường giao nhau, khi đã có biển R302 tại các đầu dải phân cách thì không nhất thiết đặt biển số I.407a. Biển số I.407b,c đặt trước nơi đường giao nhau và đặt trên đường chuẩn bị đi vào đường một chiều.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780754/jgulfaajaolwfk84npuv.png"),
//        TrafficSign(248, "Nơi đỗ xe", "noi_đo_xe", "Để chỉ dẫn những nơi được phép đỗ xe, những bãi đỗ xe, bến xe, v.v..., đặt biển số I.408 Nơi đỗ xe", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780755/e5jlv60q98rit2mwzell.png"),
//        TrafficSign(249, "Nơi đỗ xe một phần trên hè phố", "noi_đo_xe_mot_phan_tren_he_pho", "Để chỉ dẫn những nơi được phép đỗ xe một phần trên hè phố rộng, đặt biển số I.408a Nơi đỗ xe một phần trên hè phố Xe phải đỗ sao cho các bánh phía ghế phụ trên hè phố.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780756/kzgaeawj3c0cly4hcshq.png"),
//        TrafficSign(250, "Chỗ quay xe", "cho_quay_xe", "Để chỉ dẫn vị trí được phép quay đầu xe, đặt biển số I.409 Chỗ quay xe", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780757/rstkznbm4eno6iixnmto.png"),
//        TrafficSign(251, "Khu vực quay xe", "khu_vuc_quay_xe", "Để chỉ dẫn khu vực được phép quay đầu xe, đặt biển số I.410 Khu vực quay xe. Trên biển mô tả cách thức tiến hành quay xe.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732780758/sqy9wkhqwiefdlbymoby.png"),
//
//        )
//    }
//
//    // Danh sách biển nguy hiểm
//    private fun getWarningSigns(): List<TrafficSign> {
//        return listOf(
//            TrafficSign(80, "Giao nhau cùng mức với đường tàu điện.", "giao_nhau_cung_muc_voi_đuong_tau_đien", "Báo trước sắp đến chỗ giao nhau giao nhau cùng mức với đường tàu điện.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733060/pno3mbliczgab7cwep72.jpg"),
//            TrafficSign(81, "Chỗ ngoặt nguy hiểm vòng bên trái.", "cho_ngoat_nguy_hiem_vong_ben_trai", "Báo trước sắp tới chỗ ngoặt vòng bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733062/ytafkdiua7ouypxbbtxg.jpg"),
//            TrafficSign(82, "Chỗ ngoặt nguy hiểm vòng bên phải.", "cho_ngoat_nguy_hiem_vong_ben_phai", "Báo trước sắp tới chỗ ngoặt vòng bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733063/tk2erum5dbtkehbfidwr.jpg"),
//            TrafficSign(83, "Hai chỗ ngoặt ngược chiều nhau liên tiếp bên trái.", "hai_cho_ngoat_nguoc_chieu_nhau_lien_tiep_ben_trai", "Báo trước có từ 2 chỗ ngoặt, ở gần nhau trong đó có ít nhất một chỗ ngoặt nguy hiểm mà chỗ ngoặt đầu tiên hướng vòng bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733064/lwzet4nn0cpi8gjynccv.jpg"),
//            TrafficSign(84, "Hai chỗ ngoặt ngược chiều nhau liên tiếp bên phải.", "hai_cho_ngoat_nguoc_chieu_nhau_lien_tiep_ben_phai", "Báo trước có từ 2 chỗ ngoặt, ở gần nhau trong đó có ít nhất một chỗ ngoặt nguy hiểm mà chỗ ngoặt đầu tiên hướng vòng bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733065/h1yixcwx7wo00myjxq5b.jpg"),
//            TrafficSign(85, "Đường bị thu hẹp cả hai bên.", "đuong_bi_thu_hep_ca_hai_ben", "Báo trước đường sắp bị thu hẹp cả hai bên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733066/ghexjsb7zlbl2cnwjbry.jpg"),
//            TrafficSign(86, "Chỗ ngoặt nguy hiểm có nguy cơ lật xe bên phải.", "cho_ngoat_nguy_hiem_co_nguy_co_lat_xe_ben_phai", "Báo trước sắp tới chỗ ngoặt nguy hiểm có nguy cơ lật xe bên phải khi đường cong vòng sang trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733067/ssagplvzyz3ysjwtcrpg.jpg"),
//            TrafficSign(87, "Chỗ ngoặt nguy hiểm có nguy cơ lật xe bên trái.", "cho_ngoat_nguy_hiem_co_nguy_co_lat_xe_ben_trai", "Báo trước sắp tới chỗ ngoặt nguy hiểm có nguy cơ lật xe bên trái khi đường cong vòng sang phải", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733068/j5on5yjwmf20zcfxdwoo.jpg"),
//            TrafficSign(88, "Đường 2 chiều.", "đuong_2_chieu", "Báo đoạn đường phía trước do sửa chữa hoặc có trở ngại khác nên phải đi vào phần đường còn lại hoặc đường tạm theo cả 2 chiều.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733068/qnwdkefeplakhu1yrp3o.png"),
//            TrafficSign(89, "Đường bị thu hẹp bên phải.", "đuong_bi_thu_hep_ben_phai", "Báo trước đường bị hẹp phía bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733069/xjcnmsbfrgar6lt9bos8.jpg"),
//            TrafficSign(90, "Đường bị thu hẹp bên trái.", "đuong_bi_thu_hep_ben_trai", "Báo trước đường bị hẹp phía bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733070/yljsoz7s7ik8soxpcwwx.jpg"),
//            TrafficSign(91, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733071/csseca2opfi6thzgutix.jpg"),
//            TrafficSign(92, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733072/dhkf0lsukuh5pzex7q0s.jpg"),
//            TrafficSign(93, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733073/gitpxjupvfxbk69wqt5b.jpg"),
//            TrafficSign(94, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên liên tiếp.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733074/ikehgiw4myh6ou1lyegs.jpg"),
//            TrafficSign(95, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên liên tiếp.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733075/bsybbgsddw9lfoly1wsl.jpg"),
//            TrafficSign(96, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733076/tdxejmh3p7rs2nowqeye.jpg"),
//            TrafficSign(97, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733076/db6jo8kv6iioifr4wqjn.jpg"),
//            TrafficSign(98, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733077/pnpxdbaajy2jwaqkct9n.jpg"),
//            TrafficSign(99, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên phải và phía trước.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733078/dh0urtm4vik9e4ayrhld.jpg"),
//            TrafficSign(100, "Giao nhau với đường không ưu tiên", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở phía trước.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733079/f9doigqron1xqz5nfhdv.jpg"),
//            TrafficSign(101, "Giao nhau với đường không ưu tiên.", "giao_nhau_voi_đuong_khong_uu_tien", "Đặt trên đường ưu tiên, để báo trước sắp đến nơi giao nhau với đường không ưu tiên ở bên trái và phía trước.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733080/m9vxmrcqyarkmnat3tfv.jpg"),
//            TrafficSign(102, "Cầu hẹp.", "cau_hep", "Báo cầu phía trước là loại cầu có chiều rộng phần xe chạy nhỏ hơn hoặc bằng 1 làn đường (4,5m). Các xe khi lưu thông qua loại cầu này phải nhường nhau và chờ ở 2 đầu cầu.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733081/kfffcye8lkhlorznx4nl.jpg"),
//            TrafficSign(103, "Cầu tạm", "cau_tam", "Báo cầu phía trước là loại cầu tạm, được làm để sử dụng tạm thời cho xe qua lại.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733082/eikxmsy9nlrhjtbqwxnn.jpg"),
//            TrafficSign(104, "Cầu xoay – cầu cất", "cau_xoay__cau_cat", "Báo cầu phía trước là loại cầu xoay, cầu cất là những loại cầu trong từng khoảng thời gian có ngắt giao thông đường bộ để cho tàu thuyền qua lại. Các phương tiện đi trên đường bộ phải chờ đợi để đi qua.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733083/orwpcsi1oupgbzks1z18.png"),
//            TrafficSign(105, "Kè – vực sâu phía trước.", "ke__vuc_sau_phia_truoc", "Báo phía trước có bờ kè, vực sâu, hoặc bờ sông áp sát đường phía trước, cần đề phòng tình huống nguy hiểm vượt kè, rơi xuống vực sâu (thường cắm ở những chỗ ngoặt nguy hiểm).", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733084/g90nq886scdzggof5zd8.jpg"),
//            TrafficSign(106, "Kè – vực sâu phía trước.", "ke__vuc_sau_phia_truoc", "Báo phía trước có bờ kè, vực sâu, hoặc bờ sông áp sát đường phía bên phải, cần đề phòng tình huống nguy hiểm vượt kè, rơi xuống vực sâu (thường cắm ở những chỗ ngoặt nguy hiểm).", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733084/mkbiimckqtjxh6yvjizn.jpg"),
//            TrafficSign(107, "Kè – vực sâu phía trước.", "ke__vuc_sau_phia_truoc", "Báo phía trước có bờ kè, vực sâu, hoặc bờ sông áp sát đường phía bên trái, cần đề phòng tình huống nguy hiểm vượt kè, rơi xuống vực sâu (thường cắm ở những chỗ ngoặt nguy hiểm).", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733085/h4gcldkeohupd2umenv8.jpg"),
//            TrafficSign(108, "Đường ngầm", "đuong_ngam", "Báo trước những chỗ có đường ngầm (đường tràn). Đường ngầm là những đoạn đường vượt qua sông, suối, khe cạn mà nước có thể chảy tràn qua mặt đường thường xuyên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733086/dvh6qzxaryscavhdsc0k.jpg"),
//            TrafficSign(109, "Bến phà", "ben_pha", "Báo trước sắp đến bến phà.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733087/p6zxjkkqskxxawzpxaou.jpg"),
//            TrafficSign(110, "Dốc lên nguy hiểm.", "doc_len_nguy_hiem", "Báo trước sắp lên dốc nguy hiểm, người điều khiển phương tiện phải chọn cách chạy phù hợp để đảm bảo an toàn.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733088/zofwoz0htvybngf3am0s.jpg"),
//            TrafficSign(111, "Đường không bằng phẳng.", "đuong_khong_bang_phang", "Báo trước sắp tới đoạn đường có mặt đường không bằng phẳng, lồi lõm, sống trâu,… xe chạy với tốc độ cao sẽ bị nguy hiểm.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733089/osw6fvqggmds6odtonja.jpg"),
//            TrafficSign(112, "Đường không bằng phẳng", "đuong_khong_bang_phang", "Báo trước đoạn đường có sóng mấp mô nhân tạo (gờ giảm tốc) để hạn chế tốc độ xe chạy, bắt buộc lái xe phải chạy với tốc độ chậm trước khi qua những điểm cần kiểm soát, kiểm tra hay khu đông dân cư,…", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733090/ghxg4yyjxyyadqf0eebj.jpg"),
//            TrafficSign(113, "Đường trơn.", "đuong_tron.", "Báo trước sắp tới đoạn đường có thể xảy ra trơn trượt khi thời tiết xấu, cần tránh hãm phanh, tăng ga, sang số đột ngột hoặc xe chạy với tốc độ cao sẽ gặp nguy hiểm. Khi gặp biển báo này người điều khiển phương tiện phải giảm tốc độ xe chạy và thận trọng", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733090/k40zc34edmjkzw6rycjn.jpg"),
//            TrafficSign(114, "Lề nguy hiểm.", "le_nguy_hiem", "Để báo những nơi lề đường không ổn định, khi xe đi vào dễ gây văng đất đá hoặc bánh xe quay tại chỗ.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733091/ql9pryetvn9t5zndirw1.jpg"),
//            TrafficSign(115, "Vách núi nguy hiểm.", "vach_nui_nguy_hiem", "Báo trước đường đi sát vách núi bên trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733092/bcyehzjbbuycf1q7veq4.jpg"),
//            TrafficSign(116, "Vách núi nguy hiểm.", "vach_nui_nguy_hiem", "Báo trước đường đi sát vách núi bên phải.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733093/mtjr5szdmjulffgn1yeb.jpg"),
//            TrafficSign(117, "Đường dành cho người đi bộ cắt ngang.", "đuong_danh_cho_nguoi_đi_bo_cat_ngang", "Báo trước sắp đến phần đường ngang dành cho người đi bộ qua đường, các loại xe cộ phải nhường ưu tiên cho người đi bộ và chỉ được chạy xe nếu như không gây nguy hiểm cho người đi bộ.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733094/zamstccvbytvgc3vla3z.jpg"),
//            TrafficSign(118, "Trẻ em", "tre_em", "Báo trước gần đến đoạn đường thường có trẻ em đi ngang qua hoặc tụ tập trên đường vườn trẻ, trường học,…", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733094/dirqtvbgauwjwnoghdk8.jpg"),
//            TrafficSign(119, "Đường người đi xe đạp cắt ngang.", "đuong_nguoi_đi_xe_đap_cat_ngang", "Báo trước đến đoạn đường thường có người đi xe đạp từ những đường nhỏ cắt ngang hoặc từ đường dành cho xe đạp nhập vào đường ô tô.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733095/ywegvdz4viot6mmxynyn.jpg"),
//            TrafficSign(120, "Công trường", "cong_truong", "Báo trước gần tới đoạn đường đang tiến hành tu sửa có người và máy móc đang làm việc trên mặt đường. Người điều khiển phương tiện phải giảm tốc độ xe, không gây nguy hiểm cho người và máy móc trên đoạn đường đó.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733096/qyfod7zjuxaxayskctz5.jpg"),
//            TrafficSign(121, "Đá lở", "đa_lo", "Báo trước gần tới một đoạn đường có thể có đất đá từ trên ta luy sụt lở bất ngờ gây nguy hiểm cho xe cộ và người đi đường, đặc biệt là ở những đoạn đường miền núi. Người lái xe phải chú ý đặc biệt khi thời tiết xấu khi tầm nhìn bị hạn chế và khi dừng hay đỗ xe sau những trận mưa lớn.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733097/pn8uj4mnabfy9ojw2imk.jpg"),
//            TrafficSign(122, "Đá lở", "đa_lo", "Báo trước gần tới một đoạn đường có thể có đất đá từ trên ta luy sụt lở bất ngờ gây nguy hiểm cho xe cộ và người đi đường, đặc biệt là ở những đoạn đường miền núi. Người lái xe phải chú ý đặc biệt khi thời tiết xấu khi tầm nhìn bị hạn chế và khi dừng hay đỗ xe sau những trận mưa lớn.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733098/x3boidlyz9sso3uwlrtf.jpg"),
//            TrafficSign(123, "Sỏi đá bắn lên", "soi_đa_ban_len", "Để báo trước nơi có kết cấu mặt đường rời rạc, khi phương tiện đi qua, làm cho các viên đá, sỏi băng lên gây nguy hiểm và mất an toàn cho người và phương tiện tham gia giao thông.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733099/q0ubjlhsowyzbf4gbtk9.jpg"),
//            TrafficSign(124, "Nền đường yếu", "nen_đuong_yeu", "Để cảnh báo những đoạn nền đường yếu, đoạn đường đang theo dõi lún mà việc vận hành xe ở tốc độ cao có thể gây nguy hiểm.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733100/c4dorjhxbqkerrxcl7ge.jpg"),
//            TrafficSign(125, "Dải máy bay lên xuống.", "dai_may_bay_len_xuong", "Báo trước tới đoạn đường sát đường băng của sân bay và cắt ngang qua hướng máy bay lên xuống ở độ cao nhỏ.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732733101/d1tvaxeq2lcwzovkwtki.jpg"),
//
//        )
//    }
//}

package com.surendramaran.yolov8tflite

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surendramaran.yolov8tflite.model.TrafficSign
import com.surendramaran.yolov8tflite.adapter.TrafficSignAdapter
import com.surendramaran.yolov8tflite.model.ApiResponse
import com.surendramaran.yolov8tflite.api.RetrofitClient
import com.surendramaran.yolov8tflite.model.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class TrafficSignActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var trafficSignAdapter: TrafficSignAdapter

    private lateinit var btnMainDishes: Button
    private lateinit var btnSupplementary : Button
    private lateinit var btnCommand : Button
    private lateinit var btnSalad: Button
    private lateinit var btnDrinks: Button
    private lateinit var backIcon: ImageView // Back icon
    private lateinit var etSearch: EditText
    private var currentPage = 1       // Trang hiện tại
    private var totalPages = 1        // Tổng số trang
    private var itemsPerPage = 10
    private var currentCategoryId = 1 // Lưu category hiện tại
    private var currentKeyword: String? = null  // Lưu từ khóa tìm kiếm hiện tại
    private lateinit var itemsPerPageSpinner: Spinner


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.traffic_sign)

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.food_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with an empty list initially
        trafficSignAdapter = TrafficSignAdapter(emptyList()) { trafficSign ->
            showTrafficSignPopup(trafficSign)
        }
        recyclerView.adapter = trafficSignAdapter // Set the adapter early

        // Initialize other views
        btnMainDishes = findViewById(R.id.btn_main_dishes)
        btnSalad = findViewById(R.id.btn_salad)
        btnDrinks = findViewById(R.id.btn_drinks)
        backIcon = findViewById(R.id.back_icon)
        btnSupplementary = findViewById(R.id.btn_supplementary)
        btnCommand = findViewById(R.id.btn_command)

        // Set up back icon click listener
        backIcon.setOnClickListener {
            finish()  // Go back to previous screen
        }


        etSearch = findViewById(R.id.searchEditText)


        val categoryId = intent.getIntExtra("CATEGORY_ID", 1) // Default to 1 if not provided
        currentCategoryId = categoryId




        etSearch.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(300)
                    s?.let {
                        currentKeyword = if (it.isNotEmpty()) it.toString().trim() else null
                        currentPage = 1 // Reset page when search changes
                        fetchTrafficSigns(currentCategoryId, currentKeyword)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        itemsPerPageSpinner = findViewById(R.id.itemsPerPageSpinner)
        itemsPerPageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position).toString().toInt()
                itemsPerPage = selectedItem // Update the items per page
                currentPage = 1  // Reset to first page when changing items per page
                fetchTrafficSigns(currentCategoryId, currentKeyword)  // Fetch signs with the new page size
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
        findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                fetchTrafficSigns(categoryId, currentKeyword)
            }
        }

        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++  // Tăng trang hiện tại
                fetchTrafficSigns(categoryId, currentKeyword)  // Gọi lại API với trang mới
            }
        }

        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.category_scroll)

        // Get categoryId from Intent

        // Fetch signs and highlight button based on categoryId
        when (categoryId) {
            1 -> {
                fetchTrafficSigns(categoryId)
                highlightSelectedButton(btnMainDishes)
                horizontalScrollView.post {
                    horizontalScrollView.smoothScrollTo(btnMainDishes.left, 0)
                }
            }
            2 -> {
                fetchTrafficSigns(categoryId)
                highlightSelectedButton(btnDrinks)
                horizontalScrollView.post {
                    horizontalScrollView.smoothScrollTo(btnDrinks.left, 0)
                }
            }
            3 -> {
                fetchTrafficSigns(categoryId)
                highlightSelectedButton(btnSupplementary)
                horizontalScrollView.post {
                    horizontalScrollView.smoothScrollTo(btnSupplementary.left, 0)
                }
            }
            4 -> {
                fetchTrafficSigns(categoryId)
                highlightSelectedButton(btnCommand)
                horizontalScrollView.post {
                    horizontalScrollView.smoothScrollTo(btnCommand.left, 0)
                }
            }
            5 -> {
                fetchTrafficSigns(categoryId)
                highlightSelectedButton(btnSalad)
                horizontalScrollView.post {
                    horizontalScrollView.smoothScrollTo(btnSalad.left, 0)
                }
            }
        }


//        highlightSelectedButton(btnMainDishes)  // Highlight default button

        // Set up category button click listeners
        btnMainDishes.setOnClickListener {
            fetchTrafficSigns(categoryId = 1)  // Fetch prohibited signs
            highlightSelectedButton(btnMainDishes)
            changeCategory(1)

        }

        btnSalad.setOnClickListener {
            fetchTrafficSigns(categoryId = 5)  // Fetch guide signs
            highlightSelectedButton(btnSalad)
            changeCategory(5)

        }

        btnDrinks.setOnClickListener {
            fetchTrafficSigns(categoryId = 2)  // Fetch warning signs
            highlightSelectedButton(btnDrinks)
            changeCategory(2)

        }

        btnSupplementary.setOnClickListener {
            fetchTrafficSigns(categoryId = 3)  // Fetch warning signs
            highlightSelectedButton(btnSupplementary)
            changeCategory(3)

        }

        btnCommand.setOnClickListener {
            fetchTrafficSigns(categoryId = 4)  // Fetch warning signs
            highlightSelectedButton(btnCommand)
            changeCategory(4)

        }
    }

    private fun changeCategory(categoryId: Int) {
        if (currentCategoryId != categoryId) {
            currentCategoryId = categoryId
            currentPage = 1 // Reset page to 1 when category changes
            currentKeyword = null // Reset search keyword when category changes
            fetchTrafficSigns(currentCategoryId) // Fetch signs for the new category
        }
    }

    // This function updates the list of traffic signs in the adapter and scrolls to the top
    private fun loadTrafficSignList(trafficSignList: List<TrafficSign>) {
        // Initialize adapter if not initialized yet
        if (!::trafficSignAdapter.isInitialized) {
            trafficSignAdapter = TrafficSignAdapter(trafficSignList) { trafficSign ->
                showTrafficSignPopup(trafficSign)
            }
            recyclerView.adapter = trafficSignAdapter
        } else {
            // Update the adapter data if it was already initialized
            trafficSignAdapter.updateData(trafficSignList)
        }

        // Scroll to the top
        recyclerView.scrollToPosition(0)
    }

    // Show a popup when a traffic sign is clicked
    private fun showTrafficSignPopup(trafficSign: TrafficSign) {
        // Inflate custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_traffic_sign, null)

        // Map views in the dialog layout
        val signName: TextView = dialogView.findViewById(R.id.dialog_sign_name)
        val signImage: ImageView = dialogView.findViewById(R.id.dialog_sign_image)
        val signDescription: TextView = dialogView.findViewById(R.id.dialog_sign_description)
        val closeButton: Button = dialogView.findViewById(R.id.dialog_close_button)

        // Set traffic sign data to the dialog views
        signName.text = trafficSign.name
        signDescription.text = trafficSign.description
        Glide.with(this)
            .load(trafficSign.path)
            .into(signImage)

        // Create an AlertDialog with the custom view
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Handle Close button click to dismiss the dialog
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchTrafficSigns(categoryId: Int, keyword: String? = null) {
        val token = "Bearer ${SessionManager.getToken()}"

        RetrofitClient.apiService.searchTrafficSigns(
            token = token,
            page = currentPage,
            keyword = keyword,
            pageSize = itemsPerPage,
            categoryId = categoryId
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        val pagination = apiResponse.pagination
                        currentPage = pagination.current_page
                        totalPages = pagination.total_pages

                        val trafficSigns = apiResponse.data
                        loadTrafficSignList(trafficSigns)
                        updatePaginationInfo()
                    }
                } else {
                    Toast.makeText(this@TrafficSignActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@TrafficSignActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun updatePaginationInfo() {
        val pageInfoText = "Page $currentPage / $totalPages"
        findViewById<TextView>(R.id.pageInfo).text = pageInfoText
        findViewById<ImageButton>(R.id.btnPrevious).isEnabled = currentPage > 1
        findViewById<ImageButton>(R.id.btnNext).isEnabled = currentPage < totalPages
    }

    // Fetch traffic signs from API based on category ID
//    private fun fetchTrafficSigns(categoryId: Int, keyword: String? = null) {
//        val token = "Bearer ${SessionManager.getToken()}" // Lấy token từ SessionManager
//
//        RetrofitClient.apiService.searchTrafficSigns(
//            token = token,
//            page = 1,
//            keyword = keyword, // Thêm keyword vào đây
//            pageSize = 12,
//            categoryId = categoryId
//        ).enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                if (response.isSuccessful) {
//                    response.body()?.data?.let { trafficSigns ->
//                        Log.d("TrafficSignActivity", "Fetched ${trafficSigns.size} traffic signs.")
//                        loadTrafficSignList(trafficSigns)
//                    }
//                } else {
//                    Toast.makeText(this@TrafficSignActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                Toast.makeText(this@TrafficSignActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }


    // Highlight the selected button (used for category buttons)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun highlightSelectedButton(selectedButton: Button) {
        // Reset button colors
        btnMainDishes.setBackgroundColor(getColor(R.color.default_button_color))
        btnSalad.setBackgroundColor(getColor(R.color.default_button_color))
        btnDrinks.setBackgroundColor(getColor(R.color.default_button_color))
        btnSupplementary.setBackgroundColor(getColor(R.color.default_button_color))
        btnCommand.setBackgroundColor(getColor(R.color.default_button_color))
        // Set highlighted color for the selected button
        selectedButton.setBackgroundColor(getColor(R.color.selected_button_color))
    }
}
