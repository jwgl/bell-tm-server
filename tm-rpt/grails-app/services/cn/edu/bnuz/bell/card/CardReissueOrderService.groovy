package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.ApiClientService
import org.springframework.beans.factory.annotation.Value

class CardReissueOrderService {
    @Value('${bell.student.picturePath}')
    String picturePath

    ApiClientService apiClientService

    File[] pictureFiles(Long orderId) {
        apiClientService.get("/cardReissueOrders/${orderId}").items.collect {
            new File(picturePath, "${it.studentId}.jpg")
        }
    }
}
