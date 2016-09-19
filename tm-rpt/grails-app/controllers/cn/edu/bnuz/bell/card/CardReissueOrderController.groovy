package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.report.ReportCommand
import cn.edu.bnuz.bell.report.ReportService
import cn.edu.bnuz.bell.report.ZipService
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueOrderController {
    ReportService reportService
    ZipService zipService
    CardReissueOrderService cardReissueOrderService

    def show(Long id) {
        ReportCommand cmd = new ReportCommand(
                reportDesign: 'card-reissue-order',
                format: 'xlsx',
                parameters: [orderId: id],
                titleKey: 'orderId'
        )

        def outputStream = reportService.runAndRender(cmd)
        response.setHeader("Content-disposition", cmd.contentDisposition)
        response.outputStream << outputStream.toByteArray()
    }

    def pictures(Long cardReissueOrderId) {
        File[] files = cardReissueOrderService.pictureFiles(cardReissueOrderId)
        byte[] data = zipService.zip(files)
        response.setHeader("Content-disposition", "attachment; filename=${URLEncoder.encode('学生照片', 'UTF-8')}-${cardReissueOrderId}.zip")
        response.outputStream << data
    }

    def distribute(Long cardReissueOrderId) {
        ReportCommand cmd = new ReportCommand(
                reportDesign: 'card-reissue-distribute',
                format: 'xlsx',
                parameters: [orderId: cardReissueOrderId],
                titleKey: 'orderId'
        )

        def outputStream = reportService.runAndRender(cmd)
        response.setHeader("Content-disposition", cmd.contentDisposition)
        response.outputStream << outputStream.toByteArray()
    }
}

