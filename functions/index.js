const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// Hàm tính điểm từ tổng tiền (1 điểm cho mỗi 10,000 VNĐ)
const calculatePoints = (tongTien) => {
  return Math.floor(tongTien / 10000);
};

// Cloud Function lắng nghe sự kiện khi có hóa đơn mới
exports.updateCustomerStats = functions.database
    .ref("/hoa_don/{invoiceId}")
    .onCreate(async (snapshot, context) => {
      try {
        const invoice = snapshot.val();
        const maKhach = invoice.maKhach;
        const tongTien = invoice.tongTien;

        // Kiểm tra xem maKhach và tongTien có tồn tại không
        if (!maKhach || !tongTien) {
          console.log("Missing maKhach or tongTien in invoice:", invoice);
          return null;
        }

        // Tìm khách hàng trong node customers dựa trên maKhach
        const customersSnapshot = await admin
            .database()
            .ref("/customers")
            .orderByChild("maKhach")
            .equalTo(maKhach)
            .once("value");

        if (!customersSnapshot.exists()) {
          console.log("Customer not found for maKhach:", maKhach);
          return null;
        }

        // Lấy khách hàng đầu tiên (maKhach là duy nhất)
        let customerId;
        let customerData;
        customersSnapshot.forEach((childSnapshot) => {
          customerId = childSnapshot.key;
          customerData = childSnapshot.val();
        });

        // Tính toán visitCount và points mới
        const currentVisitCount = customerData.visitCount || 0;
        const currentPoints = customerData.points || 0;
        const newVisitCount = currentVisitCount + 1;
        const pointsToAdd = calculatePoints(tongTien);
        const newPoints = currentPoints + pointsToAdd;

        // Cập nhật visitCount và points
        await admin
            .database()
            .ref(`/customers/${customerId}`)
            .update({
              visitCount: newVisitCount,
              points: newPoints,
            });

        console.log(
            `Updated customer ${customerId}: ` +
                `visitCount=${newVisitCount}, points=${newPoints}`,
        );
      } catch (error) {
        console.error("Error updating customer stats:", error);
      }
    });
